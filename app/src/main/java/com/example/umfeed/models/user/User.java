package com.example.umfeed.models.user;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.example.umfeed.adapters.UserAdapter;
import com.example.umfeed.repositories.LeaderboardRepository;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.PropertyName;
import com.example.umfeed.repositories.UserRepository;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import java.util.Objects;

public class User {
    private String firstName;
    private String lastName;
    private String email;
    private boolean b40Status;
    private int totalDonations;
    private Timestamp createdAt;
    private Timestamp lastLoginAt;
    private int rank;
    private String profilePicture;

    private UserRepository userRepository;

    // Nested badges object
    private UserBadges currentBadges;

    // Default constructor required for Firestore
    public User() {}

    public User (String email) {
        this.email = email;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

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
        this.profilePicture = profilePicture;
    }

    // Enum for Donor Level
    public enum DonorLevel {
        NONE,
        BRONZE,
        SILVER,
        GOLD,
        PLATINUM
    }

    // Method to get current donor level
    public DonorLevel getDonorLevel() {
        if (totalDonations >= 50) return DonorLevel.PLATINUM;
        if (totalDonations >= 10) return DonorLevel.GOLD;
        if (totalDonations >= 5) return DonorLevel.SILVER;
        if (totalDonations >= 1) return DonorLevel.BRONZE;
        return DonorLevel.NONE;
    }

    // Method to update badges based on total donations
    public void updateBadges() {
        if (currentBadges == null) {
            currentBadges = new UserBadges();
        }

        currentBadges.setBronzeDonor(totalDonations >= 1);
        currentBadges.setSilverDonor(totalDonations >= 5);
        currentBadges.setGoldDonor(totalDonations >= 10);
        currentBadges.setPlatinumDonor(totalDonations >= 50);
    }

    // DIFF_CALLBACK to ensure that modified list items are redrawn, to improve performance in RecyclerView
    public static final DiffUtil.ItemCallback<User> DIFF_CALLBACK = new DiffUtil.ItemCallback<User>() {
        @Override
        public boolean areItemsTheSame(@NonNull User oldItem, @NonNull User newItem) {
            // Check if items represent the same user by email
            // Ensure no duplicate user
            return Objects.equals(oldItem.getEmail(), newItem.getEmail());
        }

        @Override
        public boolean areContentsTheSame(@NonNull User oldItem, @NonNull User newItem) {
            // Check if the content of the user has changed
            return Objects.equals(oldItem.getFirstName(), newItem.getFirstName()) &&
                    Objects.equals(oldItem.getLastName(), newItem.getLastName()) &&
                    oldItem.getTotalDonations() == newItem.getTotalDonations();
        }
    };


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
        // Use the LeaderboardRepository to update donations for all users
        LeaderboardRepository leaderboardRepository = new LeaderboardRepository();
        if (leaderboardRepository != null) {
            leaderboardRepository.updateTotalDonationsForAllUsers();  // Update donations for all users
        } else {
            Log.e("User", "LeaderboardRepository is null");
            return 0;
        }

        // Return the total donations for the current user
        return this.totalDonations;
    }

    public void setTotalDonations(int totalDonations) {
        this.totalDonations = totalDonations;
        updateBadges(); // Automatically update badges when donations change
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

    public int getRank() { return this.rank; }

    public void setRank(int rank) { this.rank = rank; }
    public boolean isDonor(){
        return totalDonations >= 1;
    }

    public void setCurrentBadges(UserBadges currentBadges) {
        this.currentBadges = currentBadges;
    }

    public static class UserBadges {
        private boolean bronzeDonor;
        private boolean silverDonor;
        private boolean goldDonor;
        private boolean platinumDonor;

        public UserBadges() {}

        public UserBadges(boolean bronzeDonor, boolean silverDonor, boolean goldDonor, boolean platinumDonor) {
            this.bronzeDonor = bronzeDonor;
            this.silverDonor = silverDonor;
            this.goldDonor = goldDonor;
            this.platinumDonor = platinumDonor;
        }

        public void setBronzeDonor(boolean bronzeDonor) {
            this.bronzeDonor = bronzeDonor;
        }

        public void setSilverDonor(boolean silverDonor) {
            this.silverDonor = silverDonor;
        }

        public void setGoldDonor(boolean goldDonor) {
            this.goldDonor = goldDonor;
        }

        public void setPlatinumDonor(boolean platinumDonor) {
            this.platinumDonor = platinumDonor;
        }
    }

}