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

    // Assuming donationSubmissionStatus is defined elsewhere in your code, such as MutableLiveData<Boolean>
    private MutableLiveData<Boolean> donationSubmissionStatus = new MutableLiveData<>();

    // Reference to Firestore database
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

    public void submitDonation(String category, Boolean vegetarian, int quantity, String foodBankName) {
        if (userId == null) {
            // Handle error: user is not logged in
            donationSubmissionStatus.setValue(false);
            return;
        }

        CollectionReference donationsRef = db.collection("users").document(userId).collection("donations");
        CollectionReference foodBanksRef = db.collection("foodBanks");

        // Find the food bank by name
        Query foodBankQuery = foodBanksRef.whereEqualTo("name", foodBankName);

        db.collection("users").document(userId)
                .update("totalDonations", FieldValue.increment(1))
                .addOnFailureListener(e -> Log.e("Error", "Failed to increment donations", e));

        foodBankQuery.get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot foodBankDoc = querySnapshot.getDocuments().get(0);
                        String foodBankId = foodBankDoc.getId();

                        // Prepare donation data
                        Map<String, Object> donationData = new HashMap<>();
                        donationData.put("category", category);
                        donationData.put("quantity", quantity);
                        donationData.put("foodBankId", foodBankId);
                        donationData.put("donationDate", FieldValue.serverTimestamp());

                        // Handle donationsRef
                        donationsRef.add(donationData)
                                .addOnSuccessListener(documentReference -> {
                                    // After adding donation, handle the inventoryRef
                                    updateInventoryRef(donationData);
                                })
                                .addOnFailureListener(e -> {
                                    // Handle failure in checking existing donations
                                    donationSubmissionStatus.setValue(false);
                                });
                    } else {
                        // Handle case where no food bank is found
                        donationSubmissionStatus.setValue(false);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure in fetching food bank
                    donationSubmissionStatus.setValue(false);
                });
    }

    private void updateInventoryRef(Map<String, Object> donationData) {
        // Reference to the food bank's inventory
        String foodBankId = (String) donationData.get("foodBankId");
        String category = (String) donationData.get("category");
        int quantity = (int) donationData.get("quantity");
        CollectionReference inventoryRef = db.collection("foodBanks").document(foodBankId).collection("inventory");

        // Check if the category already exists in the inventory
        inventoryRef.whereEqualTo("category", category).get()
                .addOnSuccessListener(inventorySnapshot -> {
                    if (!inventorySnapshot.isEmpty()) {
                        // Category exists, increment the quantity in inventory
                        DocumentSnapshot inventoryDoc = inventorySnapshot.getDocuments().get(0);
                        String inventoryDocId = inventoryDoc.getId();

                        Map<String, Object> updateData = new HashMap<>();
                        updateData.put("quantity", FieldValue.increment(quantity));  // Increment the quantity
                        updateData.put("donationDate", FieldValue.serverTimestamp());
                        inventoryRef.document(inventoryDocId).update(updateData)
                                .addOnSuccessListener(aVoid -> {
                                    // Update LiveData to indicate success
                                    donationSubmissionStatus.setValue(true);
                                })
                                .addOnFailureListener(e -> {
                                    // Handle failure in updating inventory
                                    donationSubmissionStatus.setValue(false);
                                });
                    } else {
                        // Category does not exist in inventory, create a new document
                        donationData.remove("foodBankId");
                        inventoryRef.add(donationData)
                                .addOnSuccessListener(aVoid -> {
                                    // Update LiveData to indicate success
                                    donationSubmissionStatus.setValue(true);
                                })
                                .addOnFailureListener(e -> {
                                    // Handle failure in adding to inventory
                                    donationSubmissionStatus.setValue(false);
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure in checking inventory
                    donationSubmissionStatus.setValue(false);
                });
    }
}
