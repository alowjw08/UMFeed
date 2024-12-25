package com.example.umfeed.models.foodbank;

import com.google.firebase.Timestamp;

public class FoodBankInventoryItem {
    private String id; // Unique document ID
    private String category; // e.g., "Biscuits and Snacks"
    private Timestamp donationDate; // Date and time of donation
    private String foodBankId; // ID of the associated foodbank
    private int quantity; // Quantity of the item

    // Empty constructor required for Firebase
    public FoodBankInventoryItem() {}

    // Full constructor
    public FoodBankInventoryItem(String id, String category, Timestamp donationDate, String foodBankId, int quantity) {
        this.id = id;
        this.category = category;
        this.donationDate = donationDate;
        this.foodBankId = foodBankId;
        this.quantity = quantity;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public Timestamp getDonationDate() {
        return donationDate;
    }

    public String getFoodBankId() {
        return foodBankId;
    }

    public int getQuantity() {
        return quantity;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDonationDate(Timestamp donationDate) {
        this.donationDate = donationDate;
    }

    public void setFoodBankId(String foodBankId) {
        this.foodBankId = foodBankId;
    }

}

