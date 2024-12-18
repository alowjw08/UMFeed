package com.example.umfeed.viewmodels.pin;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;

public class PinVerificationViewModel extends ViewModel {
    private final MutableLiveData<String> pinText = new MutableLiveData<>("");
    private static final int PIN_LENGTH = 4;
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final MutableLiveData<Boolean> isPinCorrect = new MutableLiveData<>(false);
    private MutableLiveData<String> foodBankId = new MutableLiveData<>();

    public LiveData<String> getPinText() {
        return pinText;
    }

    public LiveData<Boolean> getPinCorrect() {
        Log.d("GetPinCorrect", String.valueOf(isPinCorrect.getValue()));
        return isPinCorrect;
    }

    public void appendDigit(String digit) {
        if (pinText.getValue() != null && pinText.getValue().length() < PIN_LENGTH) {
            pinText.setValue(pinText.getValue() + digit);
        }
    }
    public void clearPin() {
        pinText.setValue(""); // Clear the PIN text
    }
    public void setFoodBankId(String foodBankId) {
        this.foodBankId.setValue(foodBankId);
    }

    public LiveData<String> getFoodBankId() {
        return foodBankId;
    }
    public boolean isPinValid() {
        return pinText.getValue() != null && pinText.getValue().length() == PIN_LENGTH;
    }

    public void verifyPin(int enteredPin) {
        String foodBankId = String.valueOf(this.foodBankId.getValue());
        isPinCorrect.setValue(null);
        firestore.collection("foodBanks")
                .document(foodBankId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        Long pinNumber = document.getLong("dailyPin");
                        if (pinNumber != null) {
                            Log.d("PinVerification", "EnteredPin: " + enteredPin);
                            Log.d("PinVerification", "PinNum: " + pinNumber);
                            // Check if the date matches and the PIN matches
                            if (enteredPin == pinNumber.intValue()) {
                                isPinCorrect.postValue(true);
                            } else {
                                isPinCorrect.postValue(false);
                            }
                        }
                    } else {
                        Log.e("PinVerification", "Firebase call failed", task.getException());
                    }
                });
    }
}