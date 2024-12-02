package com.example.umfeed.viewmodels.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.umfeed.repositories.UserRepository;
import com.example.umfeed.utils.ValidationUtils;

public class CreateNewPasswordViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isPasswordUpdated = new MutableLiveData<>(false);
    private String userEmail;

    public CreateNewPasswordViewModel() {
        userRepository = new UserRepository();
    }

    public void setEmail(String email) {
        this.userEmail = email;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsPasswordUpdated() {
        return isPasswordUpdated;
    }

    public void updatePassword(String newPassword, String confirmPassword) {
        // Validate password
        String passwordError = ValidationUtils.getPasswordError(newPassword);
        if (passwordError != null) {
            errorMessage.setValue(passwordError);
            return;
        }

        // Check if passwords match
        if (!newPassword.equals(confirmPassword)) {
            errorMessage.setValue("Passwords do not match");
            return;
        }

        isLoading.setValue(true);
        errorMessage.setValue(null);

        userRepository.completePasswordReset(userEmail, newPassword)
                .observeForever(result -> {
                    isLoading.setValue(false);

                    if (result.isSuccess()) {
                        isPasswordUpdated.setValue(true);
                    } else {
                        String error = result.getError().getMessage();
                        errorMessage.setValue(error != null ? error : "Failed to update password");
                    }
                });
    }
}