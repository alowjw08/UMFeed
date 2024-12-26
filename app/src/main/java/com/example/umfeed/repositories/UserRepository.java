package com.example.umfeed.repositories;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.umfeed.models.user.User;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.example.umfeed.models.Result;

public class UserRepository {
    private final FirebaseFirestore db;
    private final FirebaseAuth auth;
    private final Executor executor;
    private static final String WEB_CLIENT_ID = "478211938002-703u44t0sfalfhhg144hpc28bk93vtcv.apps.googleusercontent.com";
    private final SignInClient oneTapClient;
    public UserRepository() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        executor = Executors.newSingleThreadExecutor();
        oneTapClient = null;
    }

    public UserRepository(Context context) {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        executor = Executors.newSingleThreadExecutor();
        oneTapClient = Identity.getSignInClient(context);
    }

    public SignInClient getOneTapClient() {
        return oneTapClient;
    }

    public BeginSignInRequest getSignInRequest() {
        return BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(WEB_CLIENT_ID)
                        .setFilterByAuthorizedAccounts(true)
                        .build())
                .build();
    }

    public LiveData<Result<FirebaseUser>> signInWithGoogle(String idToken) {
        MutableLiveData<Result<FirebaseUser>> resultMutableLiveData = new MutableLiveData<>();

        executor.execute(() -> {
            AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
            auth.signInWithCredential(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            createOrUpdateGoogleUserProfile(user);
                            resultMutableLiveData.setValue(Result.success(user));
                        } else {
                            resultMutableLiveData.setValue(Result.failure(task.getException()));
                        }
                    });
        });
        return resultMutableLiveData;
    }

    private void createOrUpdateGoogleUserProfile(FirebaseUser firebaseUser) {
        if (firebaseUser == null) return;

        db.collection("users").document(firebaseUser.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        User newUser = new User();
                        newUser.setEmail(firebaseUser.getEmail());
                        newUser.setFirstName(firebaseUser.getDisplayName());
                        newUser.setCreatedAt(Timestamp.now());
                        newUser.setLastLoginAt(Timestamp.now());
                        newUser.setTotalDonations(0);
                        verifyB40Status(firebaseUser.getEmail(), newUser::setB40Status);

                        User.UserBadges badges = new User.UserBadges(false, false, false, false);
                        newUser.setCurrentBadges(badges);

                        db.collection("users").document(firebaseUser.getUid())
                                .set(newUser)
                                .addOnFailureListener(e -> Log.e("UserRepository", "Error in creating google user profile", e));
                    } else {
                        db.collection("users").document(firebaseUser.getUid())
                                .update("lastLoginAt", Timestamp.now())
                                .addOnFailureListener(e -> Log.e("UserRepository", "Error updating last login", e));
                    }
                })
                .addOnFailureListener(e -> Log.e("UserRepository", "Error in checking user profile", e));
    }

    public LiveData<Result<FirebaseUser>> loginWithEmail(String email, String password) {
        MutableLiveData<Result<FirebaseUser>> resultLiveData = new MutableLiveData<>();

        executor.execute(() -> auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        updateLastLogin();
                        resultLiveData.setValue(Result.success(auth.getCurrentUser()));
                    } else {
                        resultLiveData.setValue(Result.failure(task.getException()));
                    }
                }));

        return resultLiveData;
    }



    public LiveData<Result<FirebaseUser>> register(String email, String password) {
        MutableLiveData<Result<FirebaseUser>> resultLiveData = new MutableLiveData<>();

        executor.execute(() -> auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        createUserProfile(firebaseUser);
                        resultLiveData.setValue(Result.success(firebaseUser));
                    } else {
                        resultLiveData.setValue(Result.failure(task.getException()));
                    }
                }));

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
                    .addOnSuccessListener(aVoid -> resultLiveData.postValue(Result.success(currentUser)))
                    .addOnFailureListener(e -> resultLiveData.postValue(Result.failure(e)));
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

        User.UserBadges badges = new User.UserBadges(false, false, false, false);
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

        executor.execute(() -> auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        resultMutableLiveData.setValue(Result.success(null));
                    } else {
                        resultMutableLiveData.setValue(Result.failure(task.getException()));
                    }
                }));
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