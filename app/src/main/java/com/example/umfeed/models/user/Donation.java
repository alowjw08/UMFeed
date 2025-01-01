package com.example.umfeed.models.user;

import java.util.Date;

public class Donation {
    private String id;
    private String category;
    private int quantity;
    private String foodBankId;
    private Date donationDate;
    private int image;

    public Donation() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public void setQuantity(Object quantityObj) {
        if (quantityObj instanceof String) {
            this.quantity = Integer.parseInt((String) quantityObj);
        } else if (quantityObj instanceof Number) {
            this.quantity = ((Number) quantityObj).intValue();
        }
    }

    public String getFoodBankId() {
        return foodBankId;
    }

    public void setFoodBankId(String foodBankId) {
        this.foodBankId = foodBankId;
    }

    public Date getDonationDate() {
        return donationDate;
    }

    public void setDonationDate(Date donationDate) {
        this.donationDate = donationDate;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
