package com.example.umfeed.viewmodels.donation;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.umfeed.models.foodbank.FoodBank;
import com.example.umfeed.models.user.Donation;
import com.example.umfeed.viewmodels.foodbank.FoodbankListViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDonationViewModel extends ViewModel {
    private final MutableLiveData<List<Donation>> filteredDonationsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private final List<Donation> allDonations = new ArrayList<>();

    private final MutableLiveData<List<Donation>> donationsLiveData = new MutableLiveData<>();
    Map<String, String> foodBankIdToNameMap = new HashMap<>();
    private List<String> selectedCategories = new ArrayList<>();
    private List<String> selectedLocations = new ArrayList<>();
    private String selectedDateRange = "";

    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    // Expose the filtered donations and loading state
    public LiveData<List<Donation>> getDonations() {
        return donationsLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    // Load donations from Firestore
    public void loadUserDonations() {
        String userId = auth.getCurrentUser().getUid();
        isLoading.setValue(true);

        CollectionReference donationsRef = firestore.collection("users")
                .document(userId)
                .collection("donations");

        donationsRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    isLoading.setValue(false);
                    List<Donation> donations = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Donation donation = doc.toObject(Donation.class);
                        donation.setId(doc.getId());
                        donations.add(donation);
                    }

                    // Sort donations by date in descending order (most recent first)
                    Collections.sort(donations, (d1, d2) -> {
                        if (d1.getDonationDate() != null && d2.getDonationDate() != null) {
                            return d2.getDonationDate().compareTo(d1.getDonationDate());
                        } else if (d1.getDonationDate() == null) {
                            return 1; // Place donations with null dates at the end
                        } else {
                            return -1; // Place donations with null dates at the beginning
                        }
                    });
                    allDonations.clear();
                    allDonations.addAll(donations);
                    applyFilters(); // Apply filters after loading donations
                })
                .addOnFailureListener(e -> {
                    isLoading.setValue(false);
                    errorMessage.setValue("Failed to load donations: " + e.getMessage());
                });
    }

    // Update filters and apply them
    public void updateCategoryFilter(List<String> selectedCategories) {
        this.selectedCategories = selectedCategories;
        applyFilters();
    }

    public void updateLocationFilter(List<String> selectedLocations) {
        this.selectedLocations = selectedLocations;
        applyFilters();
    }

    public void updateDateFilter(String selectedDateRange) {
        this.selectedDateRange = selectedDateRange;
        applyFilters();
    }

    // Apply the filters to the donations list
    private void applyFilters() {
        List<Donation> filteredDonations = new ArrayList<>(allDonations);

        // Filter by category
        if (!selectedCategories.isEmpty()) {
            filteredDonations.removeIf(donation -> !selectedCategories.contains(donation.getCategory()));
        }

        // Filter by location
        loadFoodBanks();
        if (!selectedLocations.isEmpty()) {
            filteredDonations.removeIf(donation -> {
                // Get the food bank name from the donation
                String donationFoodBankId = donation.getFoodBankId();

                // Get the food bank ID from the map using the food bank name
                String foodBankName = foodBankIdToNameMap.get(donationFoodBankId);

                // Return true if the donation's food bank name is not in the selected locations
                return foodBankName == null || !selectedLocations.contains(foodBankName);
            });
        }

        // Filter by date range
        if (!selectedDateRange.isEmpty()) {
            filteredDonations.removeIf(donation -> !isDonationWithinDateRange(donation));
        }

        // Update the filtered donations
        donationsLiveData.postValue(filteredDonations);
    }

    // Check if a donation is within the selected date range
    private boolean isDonationWithinDateRange(Donation donation) {
        long currentTime = System.currentTimeMillis();
        long donationTime = donation.getDonationDate().getTime();

        switch (selectedDateRange) {
            case "Last 7 days":
                return currentTime - donationTime <= 7L * 24 * 60 * 60 * 1000;
            case "This Month":
                return currentTime - donationTime <= 30L * 24 * 60 * 60 * 1000;
            case "This Year":
                return currentTime - donationTime <= 365L * 24 * 60 * 60 * 1000;
            case "All Time":
                return true;
            default:
                return false;
        }
    }

    private void loadFoodBanks() {
        CollectionReference foodBankRef = firestore.collection("foodBanks");

        foodBankRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            // Clear existing map to avoid duplication if called multiple times
                            foodBankIdToNameMap.clear();

                            // Populate the map with food bank names and ids
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                String id = document.getId();
                                String name = document.getString("name");

                                if (name != null && id != null) {
                                    foodBankIdToNameMap.put(id, name);
                                }
                            }
                        }
                    } else {
                        Log.e("FoodBank", "Failed to load food banks: " + task.getException());
                    }
                });
    }
}