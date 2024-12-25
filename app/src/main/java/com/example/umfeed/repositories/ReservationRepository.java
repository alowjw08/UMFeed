package com.example.umfeed.repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.umfeed.models.reservation.Reservation;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class ReservationRepository {
    private final FirebaseFirestore db;
    private final String userId;

    public ReservationRepository() {
        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void reserveFood(String foodBankId, String categoryId, String category, int quantity, ReservationCallback callback) {
        Log.d("ReservationRepository", "reserveFood called");
        Date today = new Date();
        Timestamp todayTimestamp = new Timestamp(today);
        Timestamp expiryTimestamp = new Timestamp(new Date(today.getTime() + 7L * 24 * 60 * 60 * 1000));

        // Check daily reservation limit
        db.collection("users")
                .document(userId)
                .collection("reservations")
                .whereEqualTo("reservationDate", todayTimestamp)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    Log.d("ReservationRepository", "Daily reservations fetched");
                    int totalReserved = querySnapshot.getDocuments().stream()
                            .mapToInt(doc -> doc.getLong("quantity").intValue())
                            .sum();

                    if (totalReserved + quantity > 3) {
                        callback.onFailure("You can only reserve up to 3 units per day!");
                        return;
                    }

                    // Run transaction to update inventory and create reservation
                    db.runTransaction(transaction -> {
                                // Get inventory document
                                var inventoryRef = db.collection("foodBanks").document(foodBankId).collection("inventory").document(categoryId);
                                var inventoryDoc = transaction.get(inventoryRef);

                                int currentQuantity = inventoryDoc.getLong("quantity").intValue();
                                if (currentQuantity < quantity) {
                                    throw new RuntimeException("Insufficient inventory");
                                }

                                // Create reservation
                                Map<String, Object> reservation = new HashMap<>();
                                reservation.put("category", category);
                                reservation.put("quantity", quantity);
                                reservation.put("foodBankId", foodBankId);
                                reservation.put("reservationDate", todayTimestamp);
                                reservation.put("expiryDate", expiryTimestamp);
                                reservation.put("status", "pending");

                                var reservationRef = db.collection("users").document(userId).collection("reservations").document();
                                transaction.set(reservationRef, reservation);

                                // Update inventory quantity
                                transaction.update(inventoryRef, "quantity", currentQuantity - quantity);

                                return null;
                            }).addOnSuccessListener(aVoid -> {
                                callback.showSuccessDialog(); // Trigger success dialog
                            }).addOnFailureListener(e -> {
                                callback.showErrorDialog("Reservation failed: " + e.getMessage());
                    });
                            })
                            .addOnFailureListener(e -> callback.onFailure("Failed to check daily reservations."));
                }

    public void getUserReservations(ReservationListCallback callback) {
        db.collection("users").document(userId).collection("reservations")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Reservation> reservations = querySnapshot.toObjects(Reservation.class);
                    callback.onSuccess(reservations);
                })
                .addOnFailureListener(e -> callback.onFailure("Failed to load reservations: " + e.getMessage()));
    }

    public interface ReservationListCallback {
        void onSuccess(List<Reservation> reservations);
        void onFailure(String error);
    }

    public interface ReservationCallback {
        void onSuccess(); // Called when the reservation is successful
        void onFailure(String error); // Called when there is an error
        void showSuccessDialog(); // Show success dialog
        void showErrorDialog(String error); // Show error dialog
    }
}
