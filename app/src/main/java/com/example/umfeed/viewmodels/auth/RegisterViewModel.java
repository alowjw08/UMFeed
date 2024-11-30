package com.example.umfeed.viewmodels.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.umfeed.repositories.UserRepository;
import com.example.umfeed.utils.ValidationUtils;

public class RegisterViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isRegistered = new MutableLiveData<>(false);

    public RegisterViewModel() {
        userRepository = new UserRepository();
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsRegistered() {
        return isRegistered;
    }

    public void register(String firstName, String lastName, String email,
                         String password, String confirmPassword) {
        // Validate all inputs
        if (firstName.trim().isEmpty()) {
            errorMessage.setValue("First name is required");
            return;
        }

        if (lastName.trim().isEmpty()) {
            errorMessage.setValue("Last name is required");
            return;
        }

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

        if (!password.equals(confirmPassword)) {
            errorMessage.setValue("Passwords do not match");
            return;
        }

        isLoading.setValue(true);
        errorMessage.setValue(null);

        userRepository.register(email, password)
                .observeForever(result -> {
                    isLoading.setValue(false);

                    if (result.isSuccess()) {
                        userRepository.updateUserProfile(firstName, lastName)
                                .observeForever(profileResult -> {
                                    if (profileResult.isSuccess()) {
                                        isRegistered.setValue(true);
                                    } else {
                                        errorMessage.setValue("Failed to create user profile");
                                    }
                                });
                    } else {
                        String error = result.getError().getMessage();
                        errorMessage.setValue(error != null ? error : "Registration failed");
                    }
                });
    }
}