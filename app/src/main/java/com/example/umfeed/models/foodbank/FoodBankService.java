package com.example.umfeed.models.foodbank;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FoodBankService {

    private FirebaseFirestore db;

    public FoodBankService() {
        db = FirebaseFirestore.getInstance();
    }

    public void fetchFoodBanks(FoodBankDataCallback foodBankDataCallback) {
        CollectionReference foodBankRef = db.collection("foodBanks");

        foodBankRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        List<FoodBank> foodBanks = new ArrayList<>();
                        if (querySnapshot != null) {
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                String name = document.getString("name");
                                String imageUrl = document.getString("imageUrl");

                                // Create a FoodBank object
                                FoodBank foodBank = new FoodBank(name, imageUrl);
                                foodBanks.add(foodBank);
                            }
                        }
                        customSort(foodBanks);
                        foodBankDataCallback.onFoodBanksFetched(foodBanks);
                    } else {
                        // Handle error
                        Log.e("FoodBankService", "Error getting documents.", task.getException());
                    }
                });
    }
    // Method to fetch foodbank data
    // Callback interface
    public interface FoodBankDataCallback {
        void onFoodBanksFetched(List<FoodBank> foodBanks);
    }

    private void customSort(List<FoodBank> foodBanks) {
        // Define the custom order of food banks
        List<String> customOrder = Arrays.asList(
                "KK1", "KK2", "KK3", "KK4", "KK5", "KK6", "KK7", "KK8", "KK9",
                "KK10", "KK11", "KK12", "KK13", "International House"
        );

        // Sort the food banks list based on the custom order
        foodBanks.sort((fb1, fb2) -> {
            String name1 = fb1.getName();  // Assuming FoodBank has getName() method
            String name2 = fb2.getName();

            int index1 = customOrder.indexOf(name1);
            int index2 = customOrder.indexOf(name2);

            // If one or both elements are not in the custom order, push them to the end
            if (index1 == -1) index1 = customOrder.size();
            if (index2 == -1) index2 = customOrder.size();

            return Integer.compare(index1, index2);
        });
    }
}
