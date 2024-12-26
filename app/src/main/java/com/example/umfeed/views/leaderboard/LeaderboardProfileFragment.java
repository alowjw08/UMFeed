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
import com.google.firebase.firestore.FirebaseFirestore;

import com.example.umfeed.models.user.User;

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

                        // Now fetch the current user's profile
                        db.collection("users")
                                .whereEqualTo("email", email)
                                .get()
                                .addOnSuccessListener(userQuery -> {
                                    if (!userQuery.isEmpty()) {
                                        User user = userQuery.getDocuments().get(0).toObject(User.class);
                                        if (user != null) {
                                            updateUI(user);
                                        }
                                    } else {
                                        Log.d("LeaderboardProfile", "No user found with email: " + email);
                                    }

                                    // Hide the loading screen
                                    progressBar.setVisibility(View.GONE);
                                });
   }


    private void updateUI(User user) {
        tvUserName.setText(String.format("%s %s",
                user.getFirstName() != null ? user.getFirstName() : "",
                user.getLastName() != null ? user.getLastName() : ""));
        // Unranked leaderboard profile shown if unranked user views their own profile from profile page shortcut
        tvCurrentRank.setText(user.getRank() != -1 ? "#" + user.getRank() : "Unranked");
        tvSumItemsDonated.setText(String.valueOf(user.getTotalDonations()));

        // Format and set the date
        String formattedDate = user.getCreatedAt().toDate().toString(); // Replace with appropriate formatting
        tvDateJoined.setText(formattedDate);

        // Set badges visibility
        ivBronzeBadge.setVisibility(user.getDonorLevel().ordinal() >= User.DonorLevel.BRONZE.ordinal() ? View.VISIBLE : View.GONE);
        ivSilverBadge.setVisibility(user.getDonorLevel().ordinal() >= User.DonorLevel.SILVER.ordinal() ? View.VISIBLE : View.GONE);
        ivGoldBadge.setVisibility(user.getDonorLevel().ordinal() >= User.DonorLevel.GOLD.ordinal() ? View.VISIBLE : View.GONE);
        ivPlatBadge.setVisibility(user.getDonorLevel().ordinal() >= User.DonorLevel.PLATINUM.ordinal() ? View.VISIBLE : View.GONE);

    }
}

