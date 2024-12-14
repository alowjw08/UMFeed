package com.example.umfeed.models.foodbank;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

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

    // Save the PIN to Firebase
    public void savePinToFirebase(int pin) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> pinData = new HashMap<>();
        pinData.put("pin", pin);
        pinData.put("date", com.google.firebase.Timestamp.now());  // Store timestamp

        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
//        db.collection("pin").document(currentDate)
//                .set(pinData)
//                .addOnSuccessListener(aVoid -> Log.d(TAG, "Pin saved successfully"))
//                .addOnFailureListener(e -> Log.e(TAG, "Error saving pin", e));
        db.collection("pin").add(pinData)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "Pin saved successfully"))
                .addOnFailureListener(e -> Log.e(TAG, "Error saving pin", e));
    }

    // Generate and store the daily pin
    public void generateAndStoreDailyPin() {
        int pin = generatePin();
        savePinToFirebase(pin);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Generate and save the PIN
        try {
            generateAndStoreDailyPin();
            Log.d(TAG, "Daily PIN generated and stored successfully.");
            return Result.success();
        } catch (Exception e) {
            Log.e(TAG, "Error generating or storing the PIN.", e);
            return Result.failure();
        }
    }

    public void triggerWorkerManually() {
        // Create the WorkRequest
        WorkRequest dailyPinRequest = new OneTimeWorkRequest.Builder(DailyPin.class)
                .setInitialDelay(0, TimeUnit.MILLISECONDS)  // Run immediately for testing
                .build();

        // Enqueue the work request
        WorkManager.getInstance(getApplicationContext()).enqueue(dailyPinRequest);

        Log.d("DailyPin", "Worker triggered manually.");
    }
}
