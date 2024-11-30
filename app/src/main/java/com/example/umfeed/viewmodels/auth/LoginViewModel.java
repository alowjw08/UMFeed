package com.example.umfeed.viewmodels.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.umfeed.repositories.UserRepository;
import com.example.umfeed.utils.ValidationUtils;

public class LoginViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoggedIn = new MutableLiveData<>(false);

    public LoginViewModel() {
        this.userRepository = new UserRepository();
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
