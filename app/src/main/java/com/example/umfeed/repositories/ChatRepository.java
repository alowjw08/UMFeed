package com.example.umfeed.repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.umfeed.models.chat.ChatMessage;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatRepository {
    private static final String TAG = "ChatRepository";
    private final FirebaseFirestore db;
    private final String userId;
    private final MutableLiveData<List<ChatMessage>> messages = new MutableLiveData<>(new ArrayList<>());
    private static final int PAGE_SIZE = 50;
    private DocumentSnapshot lastVisible;
    private boolean isLoading = false;

    public ChatRepository(){
        this.db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        this.userId = currentUser != null ? currentUser.getUid() : null;
        if (userId != null) {
            loadInitialMessages();
        }
    }

    public LiveData<List<ChatMessage>> getMessages() {
        return messages;
    }

    private void loadInitialMessages() {
        if (isLoading) return;
        isLoading = true;

        db.collection("chatHistory")
                .document(userId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(PAGE_SIZE)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<ChatMessage> messageList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        ChatMessage message = document.toObject(ChatMessage.class);
                        message.setMessageId(document.getId());
                        messageList.add(0, message);
                    }

                    if (!queryDocumentSnapshots.isEmpty()) {
                        lastVisible = queryDocumentSnapshots.getDocuments()
                                .get(queryDocumentSnapshots.size() - 1);
                    }

                    messages.setValue(messageList);
                    isLoading = false;
                    setupMessageListener();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading messages", e);
                    isLoading = false;
                });
    }

    public void loadMoreMessages() {
        if (isLoading || lastVisible == null) return;
        isLoading = true;

        db.collection("chatHistory")
                .document(userId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .limit(PAGE_SIZE)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<ChatMessage> currentMessages = messages.getValue();
                    if (currentMessages == null) {
                        currentMessages = new ArrayList<>();
                    }

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        ChatMessage message = document.toObject(ChatMessage.class);
                        message.setMessageId(document.getId());
                        currentMessages.add(0, message);
                    }

                    if (!queryDocumentSnapshots.isEmpty()) {
                        lastVisible = queryDocumentSnapshots.getDocuments()
                                .get(queryDocumentSnapshots.size() - 1);
                    }

                    messages.setValue(currentMessages);
                    isLoading = false;
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading more messages", e);
                    isLoading = false;
                });
    }

    private void setupMessageListener() {
        if (userId == null) return;

        db.collection("chatHistory")
                .document(userId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(1)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Listen failed.", error);
                        return;
                    }

                    if (snapshots != null && !snapshots.isEmpty()) {
                        QueryDocumentSnapshot latestDoc = (QueryDocumentSnapshot) snapshots.getDocuments().get(0);
                        ChatMessage latestMessage = latestDoc.toObject(ChatMessage.class);
                        latestMessage.setMessageId(latestDoc.getId());

                        List<ChatMessage> currentMessages = messages.getValue();
                        if (currentMessages == null) {
                            currentMessages = new ArrayList<>();
                        }

                        // Add new message if it doesn't exist
                        if (currentMessages.isEmpty() ||
                                !currentMessages.get(currentMessages.size() - 1)
                                        .getMessageId().equals(latestMessage.getMessageId())) {
                            currentMessages.add(latestMessage);
                            messages.setValue(currentMessages);
                        }
                    }
                });
    }

    public Task<Void> saveMessage(ChatMessage message) {
        if (userId == null) {
            return Tasks.forException(new IllegalStateException("User not logged in"));
        }

        try {
            message.setTimestamp(Timestamp.now());
            return db.collection("chatHistory")
                    .document(userId)
                    .collection("messages")
                    .add(message)
                    .continueWith(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            message.setMessageId(task.getResult().getId());
                        }
                        return null;
                    });
        } catch (Exception e) {
            Log.e(TAG, "Error saving message", e);
            return Tasks.forException(e);
        }
    }

    public void deleteMessage(String messageId) {
        if (userId == null) return;

        db.collection("chatHistory")
                .document(userId)
                .collection("messages")
                .document(messageId)
                .delete()
                .addOnFailureListener(e -> Log.e(TAG, "Error deleting message", e));
    }

    public Task<Void> updateMessage(String messageId, String newContent, boolean sending) {
        if (userId == null) {
            return Tasks.forException(new IllegalStateException("User not logged in"));
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("message", newContent);
        updates.put("sending", sending);

        return db.collection("chatHistory")
                .document(userId)
                .collection("messages")
                .document(messageId)
                .update(updates)
                .addOnFailureListener(e -> Log.e(TAG, "Error updating message", e));
    }

    public void clearChat() {
        if (userId == null) return;

        WriteBatch batch = db.batch();

        db.collection("chatHistory")
                .document(userId)
                .collection("messages")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        batch.delete(document.getReference());
                    }

                    // Commit the batch
                    batch.commit()
                            .addOnSuccessListener(aVoid -> {
                                // Only update UI after successful deletion
                                messages.setValue(new ArrayList<>());
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Error clearing chat", e);
                                // Optionally notify the UI layer of failure
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching messages to clear", e);
                });
    }

    public void updateMessagesList(List<ChatMessage> updatedMessages) {
        messages.setValue(updatedMessages);
    }
}
