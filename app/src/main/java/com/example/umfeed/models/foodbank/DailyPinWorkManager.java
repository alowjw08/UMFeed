package com.example.umfeed.models.foodbank;
import android.content.Context;
import android.util.Log;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import java.util.concurrent.TimeUnit;

//public class DailyPinWorkManager {
//
//    // Method to schedule the worker to run at midnight
//    public static void scheduleDailyPinTask(Context context) {
//        long currentTimeMillis = System.currentTimeMillis();
//        long midnightTimeMillis = getMidnightTimeMillis();
//        long delay = midnightTimeMillis - currentTimeMillis;
//
//        if (delay < 0) {
//            // If the calculated time is negative, it means we've passed midnight, so schedule for the next day.
//            delay += TimeUnit.DAYS.toMillis(1);
//        }
//
//        // Create a PeriodicWorkRequest to schedule the worker
//        PeriodicWorkRequest dailyPinRequest = new PeriodicWorkRequest.Builder(DailyPin.class, 1, TimeUnit.DAYS)
//                .setInitialDelay(delay, TimeUnit.MILLISECONDS)  // Set the initial delay to midnight
//                .build();
//
//        // Enqueue the work
////        WorkManager.getInstance(context).enqueue(dailyPinRequest);
//        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
//                "DailyPinTask",
//                ExistingPeriodicWorkPolicy.KEEP,
//                dailyPinRequest
//        );
//    }
//
//
//    // Method to calculate the time until midnight
//    private static long getMidnightTimeMillis() {
//        long currentTimeMillis = System.currentTimeMillis();
//        return currentTimeMillis + (TimeUnit.DAYS.toMillis(1) - currentTimeMillis % TimeUnit.DAYS.toMillis(1));
//    }
//
//    public static void triggerWorkerManually(Context context) {
//        // Create the WorkRequest (OneTimeWorkRequest to run immediately)
//        WorkRequest dailyPinRequest = new OneTimeWorkRequest.Builder(DailyPin.class)
//                .setInitialDelay(0, TimeUnit.MILLISECONDS)  // Run immediately for testing
//                .build();
//
//        // Enqueue the work request using WorkManager
//        WorkManager.getInstance(context).enqueue(dailyPinRequest);
//
//        Log.d("DailyPin", "Worker triggered manually.");
//    }
//}

public class DailyPinWorkManager {

    // Method to schedule the worker to run at midnight
    public static void scheduleDailyPinTask(Context context) {
        long currentTimeMillis = System.currentTimeMillis();
        long midnightTimeMillis = getMidnightTimeMillis();
        long delay = midnightTimeMillis - currentTimeMillis;

        if (delay < 0) {
            // If the calculated time is negative, it means we've passed midnight, so schedule for the next day.
            delay += TimeUnit.DAYS.toMillis(1);
        }

        // Create a PeriodicWorkRequest to schedule the worker
        PeriodicWorkRequest dailyPinRequest = new PeriodicWorkRequest.Builder(DailyPin.class, 1, TimeUnit.DAYS)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)  // Set the initial delay to midnight
                .build();

        // Enqueue the work
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "DailyPinTask",
                ExistingPeriodicWorkPolicy.KEEP,
                dailyPinRequest
        );
    }

    // Method to calculate the time until midnight
    private static long getMidnightTimeMillis() {
        long currentTimeMillis = System.currentTimeMillis();
        return currentTimeMillis + (TimeUnit.DAYS.toMillis(1) - currentTimeMillis % TimeUnit.DAYS.toMillis(1));
    }

    // Trigger the worker manually
    public static void triggerWorkerManually(Context context) {
        WorkRequest dailyPinRequest = new OneTimeWorkRequest.Builder(DailyPin.class)
                .setInitialDelay(0, TimeUnit.MILLISECONDS)  // Run immediately for testing
                .build();

        WorkManager.getInstance(context).enqueue(dailyPinRequest);
        Log.d("DailyPin", "Worker triggered manually.");
    }
}
