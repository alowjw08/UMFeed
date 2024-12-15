package com.example.umfeed.viewmodels.donation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DonationViewModel extends ViewModel {

    // LiveData to hold the submission status or response
    private MutableLiveData<Boolean> donationSubmissionStatus = new MutableLiveData<>();

    public LiveData<Boolean> getDonationSubmissionStatus() {
        return donationSubmissionStatus;
    }

    // Method to handle donation submission
    public void submitDonation(String foodName, String category, Boolean vegetarian, int quantity, com.google.firebase.Timestamp expiryDate, String location) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference donationsRef = db.collection("donations");

        // Create a map of donation data
        Map<String, Object> donationData = new HashMap<>();
        donationData.put("foodName", foodName);
        donationData.put("category", category);
        donationData.put("vegetarian", vegetarian);
        donationData.put("quantity", quantity);
        donationData.put("expiryDate", expiryDate);
        donationData.put("location", location);
        donationData.put("donationDate", FieldValue.serverTimestamp());

        // Submit data to Firestore
        donationsRef.add(donationData)
                .addOnSuccessListener(documentReference -> {
                    // Update LiveData to indicate success
                    donationSubmissionStatus.setValue(true);
                })
                .addOnFailureListener(e -> {
                    // Update LiveData to indicate failure
                    donationSubmissionStatus.setValue(false);
                });
    }
}
