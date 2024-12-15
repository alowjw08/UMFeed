package com.example.umfeed.viewmodels.pin;

import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.umfeed.R;

public class DialogSuccessViewModel extends ViewModel {
    private MutableLiveData<String> mainText = new MutableLiveData<>();
    private MutableLiveData<String> helperText = new MutableLiveData<>();
    private MutableLiveData<Integer> imageResource = new MutableLiveData<>();

//     Getters for LiveData
    public LiveData<String> getMainText() {
        return mainText;
    }
    public LiveData<String> getHelperText() {
        return helperText;
    }
    public LiveData<Integer> getImageResource() {
        return imageResource;
    }

    public void setDonationMessage() {
        mainText.setValue("Thank you for donating!");
        helperText.setValue("Your kindness and generosity are much appreciated.");
        imageResource.setValue(R.drawable.success_tick);

    }

    public void setReservationMessage() {
        mainText.setValue("Congratulations!");
        helperText.setValue("You have successfully reserved the food!");
        imageResource.setValue(R.drawable.success_tick);
    }

    public void setCollectionMessage() {
        mainText.setValue("Congratulations!");
        helperText.setValue("You have successfully collected the food!");
        imageResource.setValue(R.drawable.success_tick);
    }

    public void setDonationErrorMessage() {
            mainText.setValue("Wrong pin!");
            helperText.setValue("Please enter the pin displayed at the food bank.");
            imageResource.setValue(R.drawable.error_cross);
//            Log.d("Main text: ", (String) mainTextView.getText());
    }

    public void setReservationErrorMessage() {
        mainText.setValue("Sorry!");
        helperText.setValue("You have reserved 3 units of food today.");
        imageResource.setValue(R.drawable.error_cross);
    }

    public void setCollectionErrorMessage() {
        mainText.setValue("Wrong pin!");
        helperText.setValue("Please enter the pin displayed at the food bank.");
        imageResource.setValue(R.drawable.error_cross);
    }
}