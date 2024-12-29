package com.example.umfeed.viewmodels.user;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.umfeed.models.Result;
import com.example.umfeed.models.user.User;
import com.example.umfeed.repositories.UserRepository;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserViewModel extends ViewModel {
    private final MutableLiveData<User> currentUser = new MutableLiveData<>();

    public UserViewModel() {
        fetchUserData();
    }

    public LiveData<User> getCurrentUser() {
        return currentUser;
    }

    private void fetchUserData() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Replace "userId" with the ID of the logged-in user
        String userId = "currentUserId";
        firestore.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            user.updateBadges();
                            currentUser.setValue(user);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle error
                    currentUser.setValue(null);
                });
    }


}