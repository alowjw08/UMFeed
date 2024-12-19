package com.example.umfeed.models.foodbank;

import com.google.firebase.Timestamp;

public class FoodBankInventoryItem {
    private String id; // Unique document ID
    private String category; // e.g., "Biscuits and Snacks"
    private Timestamp donationDate; // Date and time of donation
    private String foodBankId; // ID of the associated foodbank
    private boolean isCollected; // Status of whether the item is collected
    private boolean isReserved; // Status of whether the item is reserved
    private int quantity; // Quantity of the item
    private boolean vegetarian; // Whether the item is vegetarian

    // Empty constructor required for Firebase
    public FoodBankInventoryItem() {}

    // Full constructor
    public FoodBankInventoryItem(String id, String category, Timestamp donationDate, String foodBankId,
                                 boolean isCollected, boolean isReserved, int quantity, boolean vegetarian) {
        this.id = id;
        this.category = category;
        this.donationDate = donationDate;
        this.foodBankId = foodBankId;
        this.isCollected = isCollected;
        this.isReserved = isReserved;
        this.quantity = quantity;
        this.vegetarian = vegetarian;
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

    public boolean isCollected() {
        return isCollected;
    }

    public boolean isReserved() {
        return isReserved;
    }

    public int getQuantity() {
        return quantity;
    }

    public boolean isVegetarian() {
        return vegetarian;
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

    public void setCollected(boolean collected) {
        isCollected = collected;
    }

    public void setReserved(boolean reserved) {
        isReserved = reserved;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setVegetarian(boolean vegetarian) {
        this.vegetarian = vegetarian;
    }
}

