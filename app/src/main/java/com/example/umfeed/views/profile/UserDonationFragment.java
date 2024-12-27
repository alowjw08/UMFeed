package com.example.umfeed.views.profile;
import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.umfeed.R;
import com.example.umfeed.adapters.UserDonationAdapter;
import com.example.umfeed.models.user.Donation;
import com.example.umfeed.viewmodels.donation.DonationViewModel;
import com.example.umfeed.viewmodels.donation.UserDonationViewModel;

import java.util.ArrayList;
import java.util.List;

public class UserDonationFragment extends Fragment {

    private UserDonationViewModel viewModel;
    private RecyclerView recyclerView;
    private TextView noDonationsMessage;
    private UserDonationAdapter adapter;
    private Button buttonCategory, buttonDate, buttonLocation;
    private List<String> selectedCategories = new ArrayList<>();
    private List<String> selectedLocations = new ArrayList<>();
    private String selectedDateRange = "";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_user_donation, container, false);
        viewModel = new ViewModelProvider(this).get(UserDonationViewModel.class);

        buttonCategory = root.findViewById(R.id.buttonCategory);
        buttonLocation = root.findViewById(R.id.buttonLocation);
        buttonDate = root.findViewById(R.id.buttonDate);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.userDonationRecyclerView);
        noDonationsMessage = view.findViewById(R.id.noDonationsMessage);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        viewModel = new ViewModelProvider(this).get(UserDonationViewModel.class);
        viewModel.loadUserDonations();

        viewModel.getDonations().observe(getViewLifecycleOwner(), donations -> updateUI(donations));
        buttonCategory.setOnClickListener(v -> showCategoryDialog());
        buttonLocation.setOnClickListener(v -> showLocationDialog());
        buttonDate.setOnClickListener(v -> showDateDialog());
    }

    private void updateUI(List<Donation> donations) {
        if (donations == null || donations.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            noDonationsMessage.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            noDonationsMessage.setVisibility(View.GONE);

            if (adapter == null) {
                adapter = new UserDonationAdapter(donations, requireContext());
                recyclerView.setAdapter(adapter);
            } else {
                adapter.updateDonations(donations); // Make sure this method is in your adapter
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void showCategoryDialog() {
        // List of category options
        final String[] categoryOptions = getResources().getStringArray(R.array.categories);

        // This array will hold the state of each checkbox (whether it's checked or not)
        final boolean[] selectedCategoriesBooleans = new boolean[categoryOptions.length];

        // Pre-populate the state of the checkboxes based on the selected categories
        for (int i = 0; i < categoryOptions.length; i++) {
            selectedCategoriesBooleans[i] = selectedCategories.contains(categoryOptions[i]);
        }

        // Create the dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Categories")
                .setMultiChoiceItems(categoryOptions, selectedCategoriesBooleans, (dialog, which, isChecked) -> {
                    // Handle checkbox changes
                    if (isChecked) {
                        selectedCategories.add(categoryOptions[which]); // Add category if checked
                    } else {
                        selectedCategories.remove(categoryOptions[which]); // Remove category if unchecked
                    }
                })
                .setPositiveButton("OK", (dialog, which) -> {
                    // Update the filter with the selected categories
                    viewModel.updateCategoryFilter(selectedCategories);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // Handle the cancel action (no change to the filter)
                });

        // Create and show the dialog
        builder.create().show();
    }

    private void showLocationDialog() {
        // List of location options
        final String[] locationOptions = getResources().getStringArray(R.array.foodBanks);

        // This array will hold the state of each checkbox (whether it's checked or not)
        final boolean[] selectedLocationsBooleans = new boolean[locationOptions.length];

        // Pre-populate the state of the checkboxes based on the selected locations
        for (int i = 0; i < locationOptions.length; i++) {
            selectedLocationsBooleans[i] = selectedLocations.contains(locationOptions[i]);
        }

        // Create the dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Locations")
                .setMultiChoiceItems(locationOptions, selectedLocationsBooleans, (dialog, which, isChecked) -> {
                    // Handle checkbox changes
                    if (isChecked) {
                        selectedLocations.add(locationOptions[which]); // Add location if checked
                    } else {
                        selectedLocations.remove(locationOptions[which]); // Remove location if unchecked
                    }
                })
                .setPositiveButton("OK", (dialog, which) -> {
                    // Update the filter with the selected locations
                    viewModel.updateLocationFilter(selectedLocations);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // Handle the cancel action (no change to the filter)
                });

        // Create and show the dialog
        builder.create().show();
    }

    private void showDateDialog() {
        // List of date range options
        final String[] dateOptions = {"Last 7 days", "This Month", "This Year"};

        // Create the dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Date Range")
                .setSingleChoiceItems(dateOptions, -1, (dialog, which) -> {
                    // Handle radio button selection
                    selectedDateRange = dateOptions[which]; // Save the selected date range
                })
                .setPositiveButton("OK", (dialog, which) -> {
                    // Update the filter with the selected date range
                    viewModel.updateDateFilter(selectedDateRange);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // Handle the cancel action (no change to the filter)
                });

        // Create and show the dialog
        builder.create().show();
    }



}
