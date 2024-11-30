package com.example.umfeed.viewmodels.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.umfeed.repositories.UserRepository;
import com.example.umfeed.utils.ValidationUtils;

public class ForgotPasswordViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isEmailSent = new MutableLiveData<>(false);

    public ForgotPasswordViewModel() {
        userRepository = new UserRepository();
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsEmailSent() {
        return isEmailSent;
    }

    public void resetPassword(String email) {
        String emailError = ValidationUtils.getEmailError(email);
        if (emailError != null) {
            errorMessage.setValue(emailError);
            return;
        }

        isLoading.setValue(true);
        errorMessage.setValue(null);

        userRepository.resetPassword(email)
                .observeForever(result -> {
                    isLoading.setValue(false);

                    if (result.isSuccess()) {
                        isEmailSent.setValue(true);
                    } else {
                        String error = result.getError().getMessage();
                        errorMessage.setValue(error != null ? error : "Failed to send reset email");
                    }
                });
    }
}