package com.example.umfeed.repositories;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.umfeed.R;
import com.example.umfeed.models.reservation.Reservation;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
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
        Log.d("ReservationRepository", "reserveFood called for quantity: " + quantity);

        // Get current date timestamp
        Calendar calendar = Calendar.getInstance();
        // Set to start of day
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Timestamp startOfDay = new Timestamp(new Date(calendar.getTimeInMillis()));

        // Set to end of day
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        Timestamp endOfDay = new Timestamp(new Date(calendar.getTimeInMillis()));

        // First check total quantity for today's reservations
        db.collection("users")
                .document(userId)
                .collection("reservations")
                .whereGreaterThanOrEqualTo("reservationDate", startOfDay)
                .whereLessThanOrEqualTo("reservationDate", endOfDay)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    // Calculate total quantity across all reservations
                    int totalQuantity = 0;
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Long docQuantity = doc.getLong("quantity");
                        if (docQuantity != null) {
                            totalQuantity += docQuantity;
                        }
                    }

                    // Check if new reservation would exceed limit
                    if (totalQuantity + quantity > 3) {
                        String errorMsg = String.format(
                                "Daily limit exceeded. You have already reserved %d item(s) today. Maximum allowed is 3 items per day.",
                                totalQuantity
                        );
                        callback.onFailure(errorMsg);
                        return;
                    }

                    // If under limit, proceed with reservation
                    Date today = new Date();
                    Timestamp todayTimestamp = new Timestamp(today);
                    Timestamp expiryTimestamp = new Timestamp(new Date(today.getTime() + 7L * 24 * 60 * 60 * 1000));

                    // Run transaction to update inventory and create reservation
                    db.runTransaction(transaction -> {
                                // Get inventory document
                                var inventoryRef = db.collection("foodBanks")
                                        .document(foodBankId)
                                        .collection("inventory")
                                        .document(categoryId);
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

                                var reservationRef = db.collection("users")
                                        .document(userId)
                                        .collection("reservations")
                                        .document();
                                transaction.set(reservationRef, reservation);

                                // Update inventory quantity
                                transaction.update(inventoryRef, "quantity", currentQuantity - quantity);

                                return null;
                            })
                            .addOnSuccessListener(aVoid -> {
                                Log.d("ReservationRepository", "Reservation successful");
                                callback.showSuccessDialog();
                            })
                            .addOnFailureListener(e -> {
                                Log.e("ReservationRepository", "Reservation failed", e);
                                callback.showErrorDialog("Reservation failed: " + e.getMessage());
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("ReservationRepository", "Failed to check daily reservations", e);
                    callback.onFailure("Failed to check daily reservations: " + e.getMessage());
                });
    }

    public void getUserReservations(ReservationListCallback callback) {
        db.collection("users").document(userId).collection("reservations")
                .orderBy("expiryDate", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Reservation> reservations = new ArrayList<>();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        Reservation reservation = document.toObject(Reservation.class);
                        if (reservation != null && reservation.getStatus().equals("pending")) {
                            reservation.setReservationId(document.getId());

                            Timestamp expiryTimestamp = reservation.getExpiryDate();
                            if (expiryTimestamp != null) {
                                long daysUntilExpiry = (expiryTimestamp.getSeconds() * 1000L - System.currentTimeMillis()) / (24 * 60 * 60 * 1000);

                                if (daysUntilExpiry < 0) {
                                    updateReservationStatus(reservation.getReservationId(), "expired");
                                    reservation.setStatus("expired");
                                }
                            }

                            reservations.add(reservation);
                        }
                    }
                    callback.onSuccess(reservations);
                })
                .addOnFailureListener(e -> callback.onFailure("Failed to load reservations: " + e.getMessage()));
    }
    public void updateReservationStatus(String reservationId, String newStatus) {
        db.collection("users")
                .document(userId)
                .collection("reservations")
                .document(reservationId)
                .update("status", newStatus)
                .addOnFailureListener(e -> Log.e("ReservationRepo", "Error updating status", e));
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
