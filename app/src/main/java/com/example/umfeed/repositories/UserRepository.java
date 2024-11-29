package com.example.umfeed.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.umfeed.models.user.User;

public class UserRepository {
    private final FirebaseFirestore db;
    private final FirebaseAuth auth;
    public UserRepository() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    public LiveData<User> getCurrentUser() {
        MutableLiveData<User> userLiveData = new MutableLiveData<>();

        // Get current authenticated user
        String currentUserId = auth.getCurrentUser() != null ?
                auth.getCurrentUser().getUid() : null;

        if (currentUserId == null) {
            userLiveData.setValue(null);
            return userLiveData;
        }

        // Listen to user document changes
        db.collection("users")
                .document(currentUserId)
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null) {
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {
                        User user = snapshot.toObject(User.class);
                        if (user != null) {
                            userLiveData.setValue(user);
                        }
                    } else {
                        userLiveData.setValue(null);
                    }
                });

        return userLiveData;
    }
}