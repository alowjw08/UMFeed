package com.example.umfeed.repositories;

import androidx.annotation.NonNull;
import com.example.umfeed.models.foodbank.FoodBankInventoryItem;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class FoodBankRepository {
    private final FirebaseFirestore db;
    private final CollectionReference foodBankCollection;

    public FoodBankRepository() {
        db = FirebaseFirestore.getInstance();
        foodBankCollection = db.collection("foodBanks");
    }

    // Fetch inventory items for a specific foodbank
    public void getFoodBankInventory(String foodBankId, Consumer<List<FoodBankInventoryItem>> onSuccess, Consumer<Exception> onFailure) {
        foodBankCollection.document(foodBankId).collection("inventory")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<FoodBankInventoryItem> inventoryList = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        FoodBankInventoryItem item = doc.toObject(FoodBankInventoryItem.class);
                        if (item != null) {
                            item.setId(doc.getId()); // Set the document ID
                            inventoryList.add(item);
                        }
                    }
                    onSuccess.accept(inventoryList);
                })
                .addOnFailureListener(onFailure::accept);
    }
}
