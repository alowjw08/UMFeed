package com.example.umfeed.models.reservation;

import com.google.firebase.Timestamp;

public class Reservation {
    private String reservationId; // Document ID
    private String category;
    private int quantity;
    private String foodBankId;
    private Timestamp reservationDate;
    private Timestamp expiryDate;
    private String status; // "pending", "collected", "cancelled"

    // Empty constructor for Firebase
    public Reservation() {}

    // Full constructor
    public Reservation(String reservationId, String category, int quantity, String foodBankId, Timestamp reservationDate, Timestamp expiryDate, String status) {
        this.reservationId = reservationId;
        this.category = category;
        this.quantity = quantity;
        this.foodBankId = foodBankId;
        this.reservationDate = reservationDate;
        this.expiryDate = expiryDate;
        this.status = status;
    }

    // Getters and setters
    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String id) {
        this.reservationId = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getFoodBankId() {
        return foodBankId;
    }

    public void setFoodBankId(String foodBankId) {
        this.foodBankId = foodBankId;
    }

    public Timestamp getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(Timestamp reservationDate) {
        this.reservationDate = reservationDate;
    }

    public Timestamp getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Timestamp expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

