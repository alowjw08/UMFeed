package com.example.umfeed;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.umfeed.models.foodbank.DailyPinWorkManager;
import com.example.umfeed.views.donation.DonationFragment;
import com.example.umfeed.views.donation.DonationListFragment;
import com.example.umfeed.views.foodbank.FoodbankListFragment;

public class TestFragment extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity);

//        triggerWorkerManually();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new DonationListFragment())
                    .commit();
        }
    }

    public void triggerWorkerManually() {
        DailyPinWorkManager.triggerWorkerManually(getApplicationContext());
        Log.d("Pin", "Triggered Success");
    }
}
