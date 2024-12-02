package com.example.umfeed.viewmodels.auth;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.umfeed.repositories.UserRepository;
import com.example.umfeed.utils.ValidationUtils;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInClient;

public class LoginViewModel extends AndroidViewModel {
    private final UserRepository userRepository;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoggedIn = new MutableLiveData<>(false);

    public LoginViewModel(Application application) {
        super(application);
        this.userRepository = new UserRepository(application);
        checkLoginState();
    }

    private void checkLoginState() {
        isLoggedIn.setValue(userRepository.isUserLoggedIn());
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsLoggedIn() {
        return isLoggedIn;
    }
    public SignInClient getOneTapClient() {
        return userRepository.getOneTapClient();
    }

    public BeginSignInRequest getSignInRequest() {
        return userRepository.getSignInRequest();
    }

    public void signInWithGoogle(String idToken) {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        userRepository.signInWithGoogle(idToken)
                .observeForever(result -> {
                    isLoading.setValue(false);

                    if (result.isSuccess()) {
                        isLoggedIn.setValue(true);
                    } else {
                        String error = result.getError().getMessage();
                        errorMessage.setValue(error != null ? error : "Google sign in failed");
                    }
                });
    }

    public void loginWithEmail (String email, String password) {
        String emailError = ValidationUtils.getEmailError(email);
        if (emailError != null) {
            errorMessage.setValue(emailError);
            return;
        }

        String passwordError = ValidationUtils.getPasswordError(password);
        if (passwordError != null) {
            errorMessage.setValue(passwordError);
            return;
        }

        isLoading.setValue(true);
        errorMessage.setValue(null);

        userRepository.loginWithEmail(email, password)
                .observeForever(result -> {
                    isLoading.setValue(false);

                    if (result.isSuccess()) {
                        isLoggedIn.setValue(true);
                    } else {
                        String error = result.getError().getMessage();
                        errorMessage.setValue(error != null ? error : "Login failed");
                    }
                });
    }
    public void logout() {
        userRepository.signOut();
        isLoggedIn.setValue(false);
    }
}
