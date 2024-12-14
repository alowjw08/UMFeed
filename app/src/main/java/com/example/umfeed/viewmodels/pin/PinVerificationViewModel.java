package com.example.umfeed.viewmodels.pin;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;

public class PinVerificationViewModel extends ViewModel {
    private final MutableLiveData<String> pinText = new MutableLiveData<>("");
    private static final int PIN_LENGTH = 4;
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private boolean pinCorrect = false;

    public LiveData<String> getPinText() {
        return pinText;
    }

    public boolean getPinCorrect() {
        return pinCorrect;
    }

    public void appendDigit(String digit) {
        if (pinText.getValue() != null && pinText.getValue().length() < PIN_LENGTH) {
            pinText.setValue(pinText.getValue() + digit);
        }
    }

    public boolean isPinValid() {
        return pinText.getValue() != null && pinText.getValue().length() == PIN_LENGTH;
    }

    public void verifyPin(int enteredPin) {
        firestore.collection("pin")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.e("PinVerification", "Firebase call Successful");

                        for (DocumentSnapshot document : task.getResult()) {
                            // Fetch 'pin' as a number and 'date' as a timestamp
                            Integer pinNumber = document.getLong("pin").intValue(); // Assuming 'pin' is stored as a number
                            Date timestamp = document.getDate("date"); // Assuming 'date' is stored as a timestamp
                            Log.e("PinVerification", pinNumber.toString());
                            Log.e("PinVerification", String.valueOf(timestamp));
                            if (timestamp != null && pinNumber != null) {
                                // Extract only the date part for comparison
                                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                                sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+8"));
                                String storedDate = sdf.format(timestamp);
                                String currentDate = sdf.format(new Date());

                                Log.d("PinVerification", "CurrentDate: " + currentDate);
                                Log.d("PinVerification", "StoredDate: " + storedDate);
                                Log.d("PinVerification", "EnteredPin: " + enteredPin);
                                Log.d("PinVerification", "PinNum: " + pinNumber);
                                // Check if the date matches and the PIN matches
                                if (currentDate.equals(storedDate) && enteredPin == pinNumber) {
                                    pinCorrect = true;
                                    break;
                                }
                            }
                        }
                        Log.d("PinVerification", "False or True: " + pinCorrect);
                    } else {
                        Log.e("PinVerification", "Firebase call failed", task.getException());
                    }
                });
    }
}