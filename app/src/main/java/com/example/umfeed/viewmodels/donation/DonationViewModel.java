package com.example.umfeed.viewmodels.donation;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class DonationViewModel extends ViewModel {

    // LiveData to hold the submission status or response
    private MutableLiveData<Boolean> donationSubmissionStatus = new MutableLiveData<>();

    public LiveData<Boolean> getDonationSubmissionStatus() {
        return donationSubmissionStatus;
    }

    // Method to handle donation submission
    public void submitDonation(String category, Boolean vegetarian, int quantity, String foodBankName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

        if (userId == null) {
            // Handle error: user is not logged in
            donationSubmissionStatus.setValue(false);
            return;
        }

        CollectionReference donationsRef = db.collection("users").document(userId).collection("donations");
        CollectionReference foodBanksRef = db.collection("foodBanks");
        Query foodBankQuery = foodBanksRef.whereEqualTo("name", foodBankName);

        foodBankQuery.get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        // Get the first matching food bank (or handle if there are multiple results)
                        DocumentSnapshot foodBankDoc = querySnapshot.getDocuments().get(0);
                        String foodBankId = foodBankDoc.getId(); // This is the foodBankId

                        // Create a map of donation data
                        Map<String, Object> donationData = new HashMap<>();
                        donationData.put("category", category);
                        donationData.put("vegetarian", vegetarian);
                        donationData.put("quantity", quantity);
//                        donationData.put("expiryDate", expiryDate);
                        donationData.put("foodBankId", foodBankId);
                        donationData.put("donationDate", FieldValue.serverTimestamp());
                        donationData.put("isReserved", false);
                        donationData.put("isCollected", false);

                        // Submit the donation to the user's donations collection
                        donationsRef.add(donationData)
                                .addOnSuccessListener(documentReference -> {
                                    String donationId = documentReference.getId();

                                    // Reference to the food bank's inventory
                                    CollectionReference inventoryRef = db.collection("foodBanks").document(foodBankId).collection("inventory");

                                    // Submit the same donation data to the food bank's inventory with the same donationId
                                    inventoryRef.document(donationId).set(donationData)
                                            .addOnSuccessListener(aVoid -> {
                                                // Update LiveData to indicate success
                                                donationSubmissionStatus.setValue(true);
                                            })
                                            .addOnFailureListener(e -> {
                                                // Handle failure in adding to inventory
                                                donationSubmissionStatus.setValue(false);
                                            });

                                    // Update LiveData to indicate success
                                    donationSubmissionStatus.setValue(true);
                                })
                                .addOnFailureListener(e -> {
                                    // Update LiveData to indicate failure
                                    donationSubmissionStatus.setValue(false);
                                });
                    } else {
                        // Handle case where no food bank is found for the given location
                        donationSubmissionStatus.setValue(false);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure in fetching food bank
                    donationSubmissionStatus.setValue(false);
                    Log.d("donationSubmissionStatus", "false");
                });
        Log.d("donationSubmissionStatus", String.valueOf(donationSubmissionStatus.getValue()));
    }
}
