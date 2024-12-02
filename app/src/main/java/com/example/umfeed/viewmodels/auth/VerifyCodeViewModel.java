package com.example.umfeed.viewmodels.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.umfeed.repositories.UserRepository;

public class VerifyCodeViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isVerified = new MutableLiveData<>(false);
    private String userEmail;

    public VerifyCodeViewModel() {
        this.userRepository = new UserRepository();
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsVerified() {
        return isVerified;
    }
    public void verifyCode(String code) {
        if (code.length() != 4) {
            errorMessage.setValue("Please enter a valid 4-digit code");
            return;
        }

        isLoading.setValue(true);
        errorMessage.setValue(null);

        userRepository.verifyResetCode(userEmail, code)
                .observeForever(result -> {
                    isLoading.setValue(false);
                    if(result.isSuccess()) {
                        isVerified.setValue(true);
                    } else {
                        errorMessage.setValue("Invalid Verification Code");
                    }
                });
    }

    public void resendCode() {
        if(userEmail == null) {
            errorMessage.setValue("Email address not found");
            return;
        }

        isLoading.setValue(true);
        errorMessage.setValue(null);

        userRepository.resetPassword(userEmail)
                .observeForever(result -> {
                    isLoading.setValue(false);

                    if(result.isSuccess()) {
                        errorMessage.setValue("New Code sent to your email");
                    } else {
                        errorMessage.setValue("Failed to send new code");
                    }
                });
    }
}
