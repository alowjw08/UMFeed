package com.example.umfeed.viewmodels.chat;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.umfeed.R;
import com.example.umfeed.models.chat.ChatMessage;
import com.example.umfeed.repositories.ChatRepository;
import com.example.umfeed.services.OpenAIService;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatViewModel extends AndroidViewModel {
    private final ChatRepository repository;
    private final OpenAIService openAIService;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isLoadingMore = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final ExecutorService executor;
    private static final String TAG = "ChatViewModel";
    private boolean isServiceInitialized = false;

    public ChatViewModel(@NonNull Application application) {
        super(application);
        this.repository = new ChatRepository();
        String apiKey = getApplication().getString(R.string.openai_api_key);
        this.executor = Executors.newSingleThreadExecutor();
        // Initialize OpenAI service safely
        OpenAIService tempService = null;
        try {;
            if (apiKey == null || apiKey.trim().isEmpty()) {
                Log.e(TAG, "OpenAI API key is missing");
                error.setValue("Configuration error: API key missing");
            } else {
                tempService = new OpenAIService(apiKey);
                isServiceInitialized = true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize OpenAI service", e);
            error.setValue("Failed to initialize chat service");
        }
        this.openAIService = tempService;

    }

    public LiveData<List<ChatMessage>> getMessages() {
        Log.d(TAG, "Getting messages LiveData");
        return repository.getMessages();
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    public LiveData<Boolean> getIsLoadingMore() {
        return isLoadingMore;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void loadMoreMessages() {
        isLoadingMore.setValue(true);
        executor.execute(() -> {
            repository.loadMoreMessages();
            isLoadingMore.postValue(false);
        });
    }

    public void sendMessage(String messageText) {
        if (messageText == null || messageText.trim().isEmpty()) return;
        Log.d(TAG, "Sending message: " + messageText);
        // Don't proceed if service isn't initialized
        if (!isServiceInitialized) {
            error.setValue("Chat service is not available");
            return;
        }

        try {
            // Create and save user message
            ChatMessage userMessage = new ChatMessage(messageText.trim(), "user");
            userMessage.setSending(true);

            repository.saveMessage(userMessage)
                    .addOnSuccessListener(aVoid -> {
                        // Show loading state
                        isLoading.setValue(true);
                        error.setValue(null);

                        // Process with OpenAI in background
                        executor.execute(() -> {
                            try {
                                String response = openAIService.sendMessage(messageText);

                                // Update user message status
                                userMessage.setSending(false);
                                repository.updateMessage(userMessage.getMessageId(), userMessage.getMessage());

                                // Save bot response
                                ChatMessage botMessage = new ChatMessage(response, "bot");
                                repository.saveMessage(botMessage)
                                        .addOnFailureListener(e -> {
                                            Log.e(TAG, "Failed to save bot message", e);
                                            error.postValue("Failed to save response");
                                        });

                                isLoading.postValue(false);
                            } catch (Exception e) {
                                Log.e(TAG, "Error getting response", e);
                                userMessage.setSending(false);
                                userMessage.setHasError(true);
                                userMessage.setErrorMessage("Failed to send message");
                                repository.updateMessage(userMessage.getMessageId(), userMessage.getMessage());

                                error.postValue("Failed to get response: " + e.getMessage());
                                isLoading.postValue(false);
                            }
                        });
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to save user message", e);
                        error.setValue("Failed to send message");
                        isLoading.setValue(false);
                    });

        } catch (Exception e) {
            Log.e(TAG, "Error in sendMessage", e);
            error.setValue("An error occurred while sending message");
            isLoading.setValue(false);
        }
    }
    public void deleteMessage(String messageId) {
        repository.deleteMessage(messageId);
    }

    public void retryMessage(ChatMessage message) {
        message.setSending(true);
        message.setHasError(false);
        message.setErrorMessage(null);
        repository.updateMessage(message.getMessageId(), message.getMessage());
        sendMessage(message.getMessage());
    }

    public void clearChat() {
        repository.clearChat();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}
