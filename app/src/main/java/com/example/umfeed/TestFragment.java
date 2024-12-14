package com.example.umfeed;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.umfeed.models.foodbank.DailyPinWorkManager;
import com.example.umfeed.views.donation.DonationFragment;

public class TestFragment extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity);

        // Load the fragment you want to test
//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.fragment_container, new PinVerificationDialogFragment())
//                    .commit();
//        }

//        triggerWorkerManually();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new DonationFragment())
                    .commit();
        }
    }

    public void triggerWorkerManually() {
        DailyPinWorkManager.triggerWorkerManually(getApplicationContext());
        Log.d("Pin", "Triggered Success");
    }
}
