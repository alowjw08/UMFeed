package com.example.umfeed.viewmodels.pin;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.umfeed.R;

public class DialogSuccessViewModel extends ViewModel {

    private final MutableLiveData<String> mainText = new MutableLiveData<>();
    private final MutableLiveData<String> helperText = new MutableLiveData<>();
    private final MutableLiveData<Integer> imageResource = new MutableLiveData<>();

    public LiveData<String> getMainText() {
        return mainText;
    }

    public LiveData<String> getHelperText() {
        return helperText;
    }

    public LiveData<Integer> getImageResource() {
        return imageResource;
    }

    // Clear any existing state
    public void clearState() {
        mainText.setValue("");
        helperText.setValue("");
        imageResource.setValue(null);
    }

    public void setDonationMessage() {
        clearState();
        mainText.setValue("Thank you for donating!");
        helperText.setValue("Your kindness and generosity are much appreciated.");
        imageResource.setValue(R.drawable.success_tick);
    }

    public void setReservationMessage() {
        Log.d("Dialog Success View Model", "Setting reservation success message");
        // Use postValue to ensure thread safety
        mainText.postValue("Congratulations!");
        helperText.postValue("You have successfully reserved the food!");
        imageResource.postValue(R.drawable.success_tick);
    }

    public void setDonationErrorMessage() {
        clearState();
        mainText.setValue("Wrong pin!");
        helperText.setValue("Please enter the pin displayed at the food bank.");
        imageResource.setValue(R.drawable.error_cross);
    }

    public void setReservationErrorMessage() {
        clearState();
        mainText.setValue("Sorry!");
        helperText.setValue("You have reached your daily reservation limit.");
        imageResource.setValue(R.drawable.error_cross);
    }
}