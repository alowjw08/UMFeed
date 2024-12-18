package com.example.umfeed.utils;

//helper class to validate the b40Status is user collection
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class B40ValidationUtil {
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    public B40ValidationUtil() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    public interface B40StatusCallback {
        void onResult(boolean isB40);
        void onError(Exception e);
    }

    public void checkB40Status(B40StatusCallback callback) {
        // Get the current user's ID
        String currentUserId = auth.getCurrentUser().getUid();

        // Reference to the user's document in Firestore
        DocumentReference userRef = db.collection("users").document(currentUserId);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Fetch the b40Status field
                    boolean isB40 = document.getBoolean("b40Status") != null
                            && document.getBoolean("b40Status");
                    callback.onResult(isB40);
                } else {
                    callback.onResult(false);
                }
            } else {
                callback.onError(task.getException());
            }
        });
    }
}

