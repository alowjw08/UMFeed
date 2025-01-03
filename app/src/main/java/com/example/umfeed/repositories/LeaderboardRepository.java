package com.example.umfeed.repositories;

import android.util.Log;

import com.example.umfeed.models.user.User;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class LeaderboardRepository {

    private static final String TAG = "LeaderboardRepository";
    private final FirebaseFirestore db;

    public LeaderboardRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    private List<User> parseDonors(QuerySnapshot querySnapshot) {
        List<User> users = new ArrayList<>();
        for (var document : querySnapshot.getDocuments()) {
            User user = document.toObject(User.class);
            if (user != null && user.isDonor()) {
                users.add(user);
            }
        }
        return users;
    }

    public void fetchLeaderboardData(Consumer<LeaderboardData> callback, Consumer<Exception> errorCallback) {
        db.collection("users")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Parse only donors for the leaderboard
                    List<User> users = parseDonors(queryDocumentSnapshots);
                    int totalDonations = users.stream().mapToInt(User::getTotalDonations).sum();

                    // Sort users by total donations in descending order
                    users.sort(Comparator.comparingInt(User::getTotalDonations).reversed());

                    // Set ranks with equal ranks for users with the same total donations
                    int currentRank = 1;
                    for (int i = 0; i < users.size(); i++) {
                        // If this user has the same donation total as the previous user, they get the same rank
                        if (i > 0 && users.get(i).getTotalDonations() == users.get(i - 1).getTotalDonations()) {
                            users.get(i).setRank(users.get(i - 1).getRank());
                        } else {
                            users.get(i).setRank(currentRank);
                        }

                        // Only increment the rank if the current user's donation total is different from the previous user
                        if (i == users.size() - 1 || users.get(i).getTotalDonations() != users.get(i + 1).getTotalDonations()) {
                            currentRank = users.get(i).getRank() + 1;  // Set the rank for the next distinct total donations
                        }
                    }

                    // Calculate recipient count (Check reservations for all users, including non-donors)
                    calculateRecipientCount(queryDocumentSnapshots.getDocuments(), recipientCount -> {
                        LeaderboardData leaderboardData = new LeaderboardData(users, users.size(), totalDonations, recipientCount);
                        callback.accept(leaderboardData);
                    });

                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error querying users", e);
                    errorCallback.accept(e);
                });
    }

    private void calculateRecipientCount(List<DocumentSnapshot> allDocuments, Consumer<Integer> callback) {
        AtomicInteger recipientCount = new AtomicInteger(0);
        AtomicInteger pendingTasks = new AtomicInteger(allDocuments.size());

        for (DocumentSnapshot document : allDocuments) {
            // Convert document to User
            User user = document.toObject(User.class);
            if (user != null) {
                // Check reservation for all users
                hasReservation(user, hasReservation -> {
                    Log.d(TAG, "Checking recipient eligibility for user: " + user.getEmail() +
                            ", B40Status: " + user.isB40Status() +
                            ", hasReservation: " + hasReservation);

                    if (user.isB40Status() && hasReservation) {
                        recipientCount.incrementAndGet();
                    }

                    // Check if all tasks are complete
                    if (pendingTasks.decrementAndGet() == 0) {
                        Log.d(TAG, "Total recipients: " + recipientCount.get());
                        callback.accept(recipientCount.get());
                    }
                });
            }
        }
    }



    private void hasReservation(User user, Consumer<Boolean> callback) {
        db.collection("users")
                .whereEqualTo("email", user.getEmail())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        String userDocId = queryDocumentSnapshots.getDocuments().get(0).getId();
                        db.collection("users")
                                .document(userDocId)
                                .collection("reservations")
                                .get()
                                .addOnSuccessListener(reservationQuerySnapshot -> {
                                    // Pass the result (whether there are any reservations)
                                    callback.accept(reservationQuerySnapshot.size() > 0);
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Error checking reservations", e);
                                    callback.accept(false); // Assume no reservation if error occurs
                                });
                    } else {
                        callback.accept(false); // Assume no reservation if no user is found
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error querying users", e);
                    callback.accept(false); // Assume no reservation if error occurs
                });
    }

    public void updateRanksForAllUsers() {
        db.collection("users")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<User> users = parseDonors(queryDocumentSnapshots);
                    users.sort(Comparator.comparingInt(User::getTotalDonations).reversed());

                    // Set ranks locally without updating Firestore
                    for (int i = 0; i < users.size(); i++) {
                        users.get(i).setRank(i + 1); // Local rank calculation
                    }

                    updateTotalDonationsForAllUsers();

                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error querying users", e);
                });
    }

    public void updateTotalDonationsForAllUsers() {
        // Get all users from the 'users' collection
        db.collection("users").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null) {
                        // Iterate through each user document
                        for (DocumentSnapshot userSnapshot : queryDocumentSnapshots) {
                            String userId = userSnapshot.getId();

                            // Get the donations collection for this user
                            db.collection("users")
                                    .document(userId)
                                    .collection("donations")
                                    .get()
                                    .addOnSuccessListener(donationsSnapshot -> {
                                        updateRanksForAllUsers();
                                    })
                                    .addOnFailureListener(e -> Log.e("LeaderboardRepository", "Error getting donations", e));
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("LeaderboardRepository", "Error getting users", e));
    }

    public static class LeaderboardData {
        private final List<User> users;
        private final int donorCount;
        private final int totalDonations;
        private final int recipientCount;

        public LeaderboardData(List<User> users, int donorCount, int totalDonations, int recipientCount) {
            this.users = users;
            this.donorCount = donorCount;
            this.totalDonations = totalDonations;
            this.recipientCount = recipientCount;
        }

        public List<User> getUsers() {
            return users;
        }

        public int getDonorCount() {
            return donorCount;
        }

        public int getTotalDonations() {
            return totalDonations;
        }

        public int getRecipientCount() {
            return recipientCount;
        }
    }
}




