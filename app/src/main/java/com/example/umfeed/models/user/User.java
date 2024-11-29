package com.example.umfeed.models.user;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.PropertyName;

public class User {
    private String firstName;
    private String lastName;
    private String email;
    private boolean b40Status;
    private int totalDonations;
    private Timestamp createdAt;
    private Timestamp lastLoginAt;

    // Nested badges object
    private UserBadges currentBadges;

    // Default constructor required for Firestore
    public User() {}

    public User(String firstName, String lastName, String email,
                boolean b40Status, int totalDonations,
                Timestamp createdAt, Timestamp lastLoginAt,
                UserBadges currentBadges) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.b40Status = b40Status;
        this.totalDonations = totalDonations;
        this.createdAt = createdAt;
        this.lastLoginAt = lastLoginAt;
        this.currentBadges = currentBadges;
    }

    // Getters and Setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }


    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @PropertyName("b40Status")
    public boolean isB40Status() {
        return b40Status;
    }

    @PropertyName("b40Status")
    public void setB40Status(boolean b40Status) {
        this.b40Status = b40Status;
    }

    public int getTotalDonations() {
        return totalDonations;
    }

    public void setTotalDonations(int totalDonations) {
        this.totalDonations = totalDonations;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(Timestamp lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public UserBadges getCurrentBadges() {
        return currentBadges;
    }

    public void setCurrentBadges(UserBadges currentBadges) {
        this.currentBadges = currentBadges;
    }

    public static class UserBadges {
        private boolean bronzeDonor;
        private boolean silverDonor;
        private boolean goldDonor;

        public UserBadges() {}

        public UserBadges(boolean bronzeDonor, boolean silverDonor, boolean goldDonor) {
            this.bronzeDonor = bronzeDonor;
            this.silverDonor = silverDonor;
            this.goldDonor = goldDonor;
        }

        public boolean isBronzeDonor() {
            return bronzeDonor;
        }

        public void setBronzeDonor(boolean bronzeDonor) {
            this.bronzeDonor = bronzeDonor;
        }

        public boolean isSilverDonor() {
            return silverDonor;
        }

        public void setSilverDonor(boolean silverDonor) {
            this.silverDonor = silverDonor;
        }

        public boolean isGoldDonor() {
            return goldDonor;
        }

        public void setGoldDonor(boolean goldDonor) {
            this.goldDonor = goldDonor;
        }
    }
}