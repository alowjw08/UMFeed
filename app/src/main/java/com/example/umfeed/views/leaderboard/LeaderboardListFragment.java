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
import com.example.umfeed.repositories.LeaderboardRepository;

import com.google.firebase.firestore.FirebaseFirestore;

import com.example.umfeed.adapters.UserAdapter;

public class LeaderboardListFragment extends Fragment {

    private TextView TVDonorsSum, TVTotalDonations, TVRecipientsSum, loadingText;
    private RecyclerView leaderboardRecyclerView;
    private ProgressBar progressBar;
    private FirebaseFirestore db;
    private UserAdapter leaderboardAdapter;

    public View loadingView;

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
        loadingText = rootView.findViewById(R.id.loadingText);
        loadingView = rootView.findViewById(R.id.loadingView);

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

        // Show loading views
        showLoading(loadingText, progressBar, loadingView);

        // Load leaderboard data
        loadLeaderboardData();

        return rootView;
    }

    private void showLoading(TextView loadingText, ProgressBar progressBar, View loadingView) {
        loadingText.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        loadingView.setVisibility(View.VISIBLE);
    }

    private void hideLoading(TextView loadingText, ProgressBar progressBar, View loadingView) {
        loadingText.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        loadingView.setVisibility(View.GONE);
    }

    private void loadLeaderboardData() {
        LeaderboardRepository repository = new LeaderboardRepository();

        repository.fetchLeaderboardData(data -> {
            TVDonorsSum.setText(String.valueOf(data.getDonorCount()));
            TVTotalDonations.setText(String.valueOf(data.getTotalDonations()));
            TVRecipientsSum.setText(String.valueOf(data.getRecipientCount()));

            leaderboardAdapter.submitList(data.getUsers());
            hideLoading(loadingText, progressBar, loadingView);
        }, error -> {
            Log.e("Leaderboard", "Failed to load leaderboard data", error);
            Toast.makeText(getContext(), "Failed to load data.", Toast.LENGTH_SHORT).show();
        });
    }

}






