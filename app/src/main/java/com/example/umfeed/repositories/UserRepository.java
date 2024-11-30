package com.example.umfeed.repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.umfeed.models.user.User;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.example.umfeed.models.Result;

public class UserRepository {
    private final FirebaseFirestore db;
    private final FirebaseAuth auth;
    private final Executor executor;
    public UserRepository() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        executor = Executors.newSingleThreadExecutor();
    }

    public LiveData<Result<FirebaseUser>> loginWithEmail(String email, String password) {
        MutableLiveData<Result<FirebaseUser>> resultLiveData = new MutableLiveData<>();

        executor.execute(() -> {
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            updateLastLogin();
                            resultLiveData.setValue(Result.success(auth.getCurrentUser()));
                        } else {
                            resultLiveData.setValue(Result.failure(task.getException()));
                        }
                    });
        });

        return resultLiveData;
    }

    public LiveData<Result<FirebaseUser>> register(String email, String password) {
        MutableLiveData<Result<FirebaseUser>> resultLiveData = new MutableLiveData<>();

        executor.execute(() -> {
            auth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            createUserProfile(firebaseUser);
                            resultLiveData.setValue(Result.success(firebaseUser));
                        } else {
                            resultLiveData.setValue(Result.failure(task.getException()));
                        }
                    });
        });

        return resultLiveData;
    }
    public LiveData<Result<FirebaseUser>> updateUserProfile(String firstName, String lastName) {
        MutableLiveData<Result<FirebaseUser>> resultLiveData = new MutableLiveData<>();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            resultLiveData.setValue(Result.failure(new IllegalStateException("User not logged in")));
            return resultLiveData;
        }
        executor.execute(() -> {
            String uid = currentUser.getUid();
            db.collection("users").document(uid)
                    .update(
                            "firstName", firstName,
                            "lastName", lastName
                    )
                    .addOnSuccessListener(aVoid -> {
                        resultLiveData.postValue(Result.success(currentUser));
                    })
                    .addOnFailureListener(e -> {
                        resultLiveData.postValue(Result.failure(e));
                    });
        });
        return resultLiveData;
    }
    private void createUserProfile(FirebaseUser firebaseUser) {
        if (firebaseUser == null) return;

        User newUser = new User();
        newUser.setEmail(firebaseUser.getEmail());
        newUser.setCreatedAt(Timestamp.now());
        newUser.setLastLoginAt(Timestamp.now());
        newUser.setTotalDonations(0);
        verifyB40Status(firebaseUser.getEmail(), newUser::setB40Status);

        User.UserBadges badges = new User.UserBadges(false, false, false);
        newUser.setCurrentBadges(badges);

        db.collection("users").document(firebaseUser.getUid())
                .set(newUser)
                .addOnFailureListener(e ->
                        Log.e("UserRepository", "Error creating user profile, e"));
    }



    public interface B40StatusCallback {
        void onStatusChecked(boolean isB40);
    }
    private void verifyB40Status(String email, B40StatusCallback callback) {
        db.collection("b40Students")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            callback.onStatusChecked(true);
                        } else {
                            callback.onStatusChecked(false);
                        }
                    } else {
                        Log.e("FireStoreError", "Error checking email: ", task.getException());
                        callback.onStatusChecked(false);
                    }
                });
    }


    private void updateLastLogin() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) return;

        db.collection("users").document(currentUser.getUid())
                .update("lastLoginAt", Timestamp.now())
                .addOnFailureListener(e ->
                    Log.e("UserRepository", "Error updating last login", e));
    }

    public LiveData<Result<Void>> resetPassword(String email) {
        MutableLiveData<Result<Void>> resultMutableLiveData = new MutableLiveData<>();

        executor.execute(() -> {
            auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            resultMutableLiveData.setValue(Result.success(null));
                        } else {
                            resultMutableLiveData.setValue(Result.failure(task.getException()));
                        }
                    });
        });
        return resultMutableLiveData;
    }

    public void signOut() {
        auth.signOut();
    }

    public boolean isUserLoggedIn() {
        return auth.getCurrentUser() != null;
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