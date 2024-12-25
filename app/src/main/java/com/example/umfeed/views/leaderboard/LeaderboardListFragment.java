package com.example.umfeed.views.leaderboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.umfeed.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.example.umfeed.models.user.User;
import com.example.umfeed.adapters.UserAdapter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class LeaderboardListFragment extends Fragment {

    private TextView TVDonorsSum, TVTotalDonations, TVRecipientsSum;
    private RecyclerView leaderboardRecyclerView;
    private ProgressBar progressBar;
    private FirebaseFirestore db;
    private UserAdapter leaderboardAdapter;

    public LeaderboardListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_leaderboard_list, container, false);

        // Initialize views
        TVDonorsSum = rootView.findViewById(R.id.TVDonorsSum);
        TVTotalDonations = rootView.findViewById(R.id.TVTotalDonations);
        TVRecipientsSum = rootView.findViewById(R.id.TVRecipientsSum);
        leaderboardRecyclerView = rootView.findViewById(R.id.LeaderboardRecyclerView);
        progressBar = rootView.findViewById(R.id.progressBar);

        // Set up RecyclerView
        leaderboardRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        leaderboardAdapter = new UserAdapter(getContext());
        leaderboardRecyclerView.setAdapter(leaderboardAdapter);

        // Set OnItemClickListener for the adapter
        leaderboardAdapter.setOnItemClickListener(user -> {
            // When a user card is clicked, navigate to the LeaderboardProfileFragment
            Bundle bundle = new Bundle();
            bundle.putString("email", user.getEmail());  // Pass the user email
            Navigation.findNavController(rootView).navigate(R.id.action_leaderboard_to_leaderboard_profile, bundle);
        });

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        loadLeaderboardData();

        return rootView;
    }

    private void loadLeaderboardData() {
        // Show the progress bar while loading data
        progressBar.setVisibility(View.VISIBLE);

        // Query to get all users
        db.collection("users").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<User> users = new ArrayList<>();
                        int totalDonations = 0;
                        int donorCount = 0;
                        Set<String> recipients = new HashSet<>();

                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            User user = document.toObject(User.class);
                            if (user != null) {
                                // Update total donations
                                totalDonations += user.getTotalDonations();

                                // If user is a donor, update donor count
                                if (user.isDonor()) {
                                    donorCount++;
                                    users.add(user);
                                }

                                // Get recipients with b40Status=true and at least 1 reservation
                                if (user.isB40Status() && hasReservation(user)) {
                                    recipients.add(user.getEmail());
                                }
                            }
                        }

                        // Update UI with the calculated values
                        TVDonorsSum.setText(String.valueOf(donorCount));
                        TVTotalDonations.setText(String.valueOf(totalDonations));
                        TVRecipientsSum.setText(String.valueOf(recipients.size()));

                        // Sort the list by totalDonations in descending order
                        users.sort(Comparator.comparingInt(User::getTotalDonations).reversed());

                        // Assign ranks after sorting
                        for (int i = 0; i < users.size(); i++) {
                            users.get(i).setRank(i + 1); // Set rank after sorting
                        }

                        // Update RecyclerView with the users
                        leaderboardAdapter.submitList(users);
                    }

                    // Hide progress bar after loading data
                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    // Handle failure here
                    Log.e("Leaderboard", "Failed to load leaderboard data", e);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Failed to load data.", Toast.LENGTH_SHORT).show();
                });
    }


    // Check if the user has at least 1 reservation with b40Status=true
    private boolean hasReservation(User user) {
        // Atomic Boolean is thread-safe ie can be safely used by multiple threads concurrently :D
        AtomicBoolean hasReservation = new AtomicBoolean(false);

        // Query the users collection to find the user's document by their email
        db.collection("users")  // Access the 'users' collection
                .whereEqualTo("email", user.getEmail())  // Query for the user by their email in the 'users' collection
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // If the user is found in the 'users' collection, proceed to count their reservations
                        String userDocId = queryDocumentSnapshots.getDocuments().get(0).getId();  // Get the document ID (the UID)

                        // Now, query the 'reservations' subcollection under this user's document
                        db.collection("users")  // Access the 'users' collection
                                .document(userDocId)  // Use the document ID to access this specific user document
                                .collection("reservations")  // Access the 'reservations' subcollection
                                .whereEqualTo("userEmail", user.getEmail())  // Filter by email
                                .whereEqualTo("b40Status", true)  // Check for b40Status
                                .get()
                                .addOnSuccessListener(reservationQuerySnapshot -> {
                                    int reservationCount = reservationQuerySnapshot.size();  // Count the number of reservations
                                    Log.d("Leaderboard", "Number of reservations: " + reservationCount);
                                    callback.onCheckCompleted(hasReservation.get());  // Notify the result
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Leaderboard", "Error checking reservations", e);
                                    callback.onCheckCompleted(false);  // Handle failure case
                                });
                    } else {
                        callback.onCheckCompleted(false);  // User not found in the 'users' collection
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Leaderboard", "Error querying users", e);
                    callback.onCheckCompleted(false);  // Handle failure case
                });

        return hasReservation.get();
    }

    public interface callback {
        static void onCheckCompleted(boolean hasReservation) {

        }
    }
}



