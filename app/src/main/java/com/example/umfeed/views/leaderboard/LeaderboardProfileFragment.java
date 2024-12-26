package com.example.umfeed.views.leaderboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.umfeed.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.example.umfeed.models.user.User;
import com.example.umfeed.repositories.LeaderboardRepository;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class LeaderboardProfileFragment extends Fragment {

    private TextView tvUserName, tvCurrentRank, tvSumItemsDonated, tvDateJoined;
    private ImageView ivBronzeBadge, ivSilverBadge, ivGoldBadge, ivPlatBadge;
    private ProgressBar progressBar;
    private String email;

    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leaderboard_profile, container, false);

        tvUserName = view.findViewById(R.id.TVUserName);
        tvCurrentRank = view.findViewById(R.id.CurrentRank);
        tvSumItemsDonated = view.findViewById(R.id.SumItemsDonated);
        tvDateJoined = view.findViewById(R.id.DateJoined);

        ivBronzeBadge = view.findViewById(R.id.IVbronzeBadge);
        ivSilverBadge = view.findViewById(R.id.IVsilverBadge);
        ivGoldBadge = view.findViewById(R.id.IVgoldBadge);
        ivPlatBadge = view.findViewById(R.id.IVplatBadge);

        progressBar = view.findViewById(R.id.progressBar);

        db = FirebaseFirestore.getInstance();

        if (getArguments() != null) {
            email = getArguments().getString("email");
            Log.d("LeaderboardProfile", "Email received: " + email);  // to check if the email is correct
        }

        loadUserProfile();
        return view;
    }

    private void loadUserProfile() {
        if (email == null) return;

        // Show the loading screen
        progressBar.setVisibility(View.VISIBLE);

        LeaderboardRepository leaderboardRepository = new LeaderboardRepository();
        // Fetch all leaderboard users
        leaderboardRepository.fetchLeaderboardData(leaderboardData -> {
            // Find the current user from the leaderboard data
            User currentUser = null;
            for (User user : leaderboardData.getUsers()) {
                if (user.getEmail().equals(email)) {
                    currentUser = user;
                    break;
                }
            }

            // If the user is found in the leaderboard, use that data
            if (currentUser != null) {
                updateUI(currentUser);
            } else {
                // Fetch the user's profile from another data source (non-donors data)
                fetchNonDonorProfile(email);
            }

            // Hide the loading screen
            progressBar.setVisibility(View.GONE);

        }, error -> {
            Log.e("LeaderboardProfile", "Error loading leaderboard data", error);
            progressBar.setVisibility(View.GONE);
        });
    }

    private void fetchNonDonorProfile(String email) {
        // Simulate fetching a non-donor user profile (for example, from Firebase or local storage)
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users") // Assuming non-donors are in the 'users' collection
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        User nonDonor = document.toObject(User.class);
                        if (nonDonor != null) {
                            updateUI(nonDonor);  // Update the UI with non-donor data
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("LeaderboardProfile", "Error loading non-donor profile", e);
                });
    }


    private void updateUI(User user) {
        tvUserName.setText(String.format("%s %s",
                user.getFirstName() != null ? user.getFirstName() : "",
                user.getLastName() != null ? user.getLastName() : ""));
        // Unranked leaderboard profile shown if unranked user views their own profile from profile page shortcut
        tvCurrentRank.setText(user.getRank() != 0 ? "#" + user.getRank() : "Unranked");
        tvSumItemsDonated.setText(String.valueOf(user.getTotalDonations()));

        // Format and set the date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(user.getCreatedAt().toDate());
        tvDateJoined.setText(formattedDate);

        // Set badges visibility
        ivBronzeBadge.setVisibility(user.getDonorLevel().ordinal() >= User.DonorLevel.BRONZE.ordinal() ? View.VISIBLE : View.GONE);
        ivSilverBadge.setVisibility(user.getDonorLevel().ordinal() >= User.DonorLevel.SILVER.ordinal() ? View.VISIBLE : View.GONE);
        ivGoldBadge.setVisibility(user.getDonorLevel().ordinal() >= User.DonorLevel.GOLD.ordinal() ? View.VISIBLE : View.GONE);
        ivPlatBadge.setVisibility(user.getDonorLevel().ordinal() >= User.DonorLevel.PLATINUM.ordinal() ? View.VISIBLE : View.GONE);

    }
}

