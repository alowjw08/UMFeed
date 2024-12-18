package com.example.umfeed.models.foodbank;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

//public class DailyPin extends Worker {
//
//    private static final String TAG = "DailyPin";
//    private static final int PIN_LENGTH = 4;
//    private static final int[] ALLOWED_DIGITS = {1, 2, 3, 4};
//
//    public DailyPin(@NonNull Context context, @NonNull WorkerParameters workerParams) {
//        super(context, workerParams);
//    }
//
//    public int generatePin() {
//        Random random = new Random();
//        StringBuilder pin = new StringBuilder(PIN_LENGTH);
//
//        for (int i = 0; i < PIN_LENGTH; i++) {
//            int randomDigit = ALLOWED_DIGITS[random.nextInt(ALLOWED_DIGITS.length)];
//            pin.append(randomDigit);
//        }
//
//        return Integer.parseInt(pin.toString());
//    }
//
//    // Save the PIN to Firebase
//    public void savePinToFirebase(int pin) {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        Map<String, Object> pinData = new HashMap<>();
//        pinData.put("pin", pin);
//        pinData.put("date", com.google.firebase.Timestamp.now());  // Store timestamp
//
//        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
//        db.collection("pin").add(pinData)
//                .addOnSuccessListener(documentReference -> Log.d(TAG, "Pin saved successfully"))
//                .addOnFailureListener(e -> Log.e(TAG, "Error saving pin", e));
//    }
//
//    // Generate and store the daily pin
//    public void generateAndStoreDailyPin() {
//        int pin = generatePin();
//        savePinToFirebase(pin);
//    }
//
//    @NonNull
//    @Override
//    public Result doWork() {
//        // Generate and save the PIN
//        try {
//            generateAndStoreDailyPin();
//            Log.d(TAG, "Daily PIN generated and stored successfully.");
//            return Result.success();
//        } catch (Exception e) {
//            Log.e(TAG, "Error generating or storing the PIN.", e);
//            return Result.failure();
//        }
//    }
//
//    public void triggerWorkerManually() {
//        // Create the WorkRequest
//        WorkRequest dailyPinRequest = new OneTimeWorkRequest.Builder(DailyPin.class)
//                .setInitialDelay(0, TimeUnit.MILLISECONDS)  // Run immediately for testing
//                .build();
//
//        // Enqueue the work request
//        WorkManager.getInstance(getApplicationContext()).enqueue(dailyPinRequest);
//
//        Log.d("DailyPin", "Worker triggered manually.");
//    }
//}

public class DailyPin extends Worker {

    private static final String TAG = "DailyPin";
    private static final int PIN_LENGTH = 4;
    private static final int[] ALLOWED_DIGITS = {1, 2, 3, 4};

    public DailyPin(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    public int generatePin() {
        Random random = new Random();
        StringBuilder pin = new StringBuilder(PIN_LENGTH);

        for (int i = 0; i < PIN_LENGTH; i++) {
            int randomDigit = ALLOWED_DIGITS[random.nextInt(ALLOWED_DIGITS.length)];
            pin.append(randomDigit);
        }

        return Integer.parseInt(pin.toString());
    }

    // Save the PIN to Firebase for a specific Food Bank
    public void savePinToFirebase(String foodBankId, int pin) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> pinData = new HashMap<>();
        pinData.put("dailyPin", (int) pin);
        pinData.put("pinTimestamp", (Timestamp) com.google.firebase.Timestamp.now());  // Store timestamp

        // Update the specific food bank's document with the generated PIN and timestamp
        db.collection("foodBanks").document(foodBankId)
                .update(pinData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Pin saved successfully for food bank: " + foodBankId))
                .addOnFailureListener(e -> Log.e(TAG, "Error saving pin for food bank: " + foodBankId, e));
    }

    // Generate and store the daily pin for all food banks
    public void generateAndStoreDailyPinForAllFoodBanks() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Fetch all food banks
        db.collection("foodBanks").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String foodBankId = documentSnapshot.getId();
                        int pin = generatePin();  // Generate a random pin
                        savePinToFirebase(foodBankId, pin);  // Save the pin to the respective food bank
                    }
                    Log.d(TAG, "Daily pins generated and stored successfully.");
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error fetching food banks: ", e));
    }

    @NonNull
    @Override
    public Result doWork() {
        // Generate and save the PIN for each food bank
        try {
            generateAndStoreDailyPinForAllFoodBanks();
            return Result.success();
        } catch (Exception e) {
            Log.e(TAG, "Error generating or storing the PIN.", e);
            return Result.failure();
        }
    }

    // Trigger worker manually for testing
    public void triggerWorkerManually() {
        WorkRequest dailyPinRequest = new OneTimeWorkRequest.Builder(DailyPin.class)
                .setInitialDelay(0, TimeUnit.MILLISECONDS)  // Run immediately for testing
                .build();

        WorkManager.getInstance(getApplicationContext()).enqueue(dailyPinRequest);
        Log.d("DailyPin", "Worker triggered manually.");
    }
}

