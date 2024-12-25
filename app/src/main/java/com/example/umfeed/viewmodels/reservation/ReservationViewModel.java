package com.example.umfeed.viewmodels.reservation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.umfeed.repositories.ReservationRepository;

public class ReservationViewModel extends ViewModel {
    private final ReservationRepository repository;
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> showSuccessDialog = new MutableLiveData<>();
    private final MutableLiveData<Boolean> showErrorDialog = new MutableLiveData<>();

    public ReservationViewModel() {
        repository = new ReservationRepository();
    }

    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getShowSuccessDialog() {
        return showSuccessDialog;
    }

    public LiveData<Boolean> getShowErrorDialog() {
        return showErrorDialog;
    }

    public void reserveFood(String foodBankId, String categoryId, String category, int quantity) {
        repository.reserveFood(foodBankId, categoryId, category, quantity, new ReservationRepository.ReservationCallback() {
            @Override
            public void onSuccess() {
                successMessage.setValue("Food reserved successfully!");
                showSuccessDialog.setValue(true); // Trigger success dialog
            }

            @Override
            public void onFailure(String error) {
                errorMessage.setValue(error);
                showErrorDialog.setValue(true); // Trigger error dialog
            }

            @Override
            public void showSuccessDialog() {
                showSuccessDialog.setValue(true); // Trigger success dialog
            }

            @Override
            public void showErrorDialog(String error) {
                errorMessage.setValue(error);
                showErrorDialog.setValue(true); // Trigger error dialog
            }
        });
    }

    public void clearDialogFlags() {
        showSuccessDialog.setValue(false);
        showErrorDialog.setValue(false);
    }
}
