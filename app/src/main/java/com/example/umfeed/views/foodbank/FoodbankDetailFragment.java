package com.example.umfeed.views.foodbank;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.umfeed.R;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.umfeed.adapters.FoodBankInventoryAdapter;
import com.example.umfeed.models.foodbank.FoodBank;
import com.example.umfeed.viewmodels.foodbank.FoodbankDetailViewModel;
import com.example.umfeed.viewmodels.reservation.ReservationViewModel;
import com.example.umfeed.views.pin.DialogSuccessFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FoodbankDetailFragment extends Fragment {

    private FoodbankDetailViewModel viewModel;
    private ReservationViewModel reservationViewModel;
    private RecyclerView recyclerView;
    private FoodBankInventoryAdapter adapter;
    private ProgressBar progressBar;
    private TextView noInventoryMessage;
    private TextView nameTextView;
    private ImageView imageView;
    private String foodBankId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(FoodbankDetailViewModel.class);
        reservationViewModel = new ViewModelProvider(this).get(ReservationViewModel.class);

        // Retrieve passed foodbank ID
        if (getArguments() != null) {
            foodBankId = FoodbankDetailFragmentArgs.fromBundle(getArguments()).getFoodBankId();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate layout
        return inflater.inflate(R.layout.fragment_foodbank_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        nameTextView = view.findViewById(R.id.TVFoodBankName);
        imageView = view.findViewById(R.id.foodbankDetailsImage);
        recyclerView = view.findViewById(R.id.inventoryRecyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        noInventoryMessage = view.findViewById(R.id.no_inventory_message);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FoodBankInventoryAdapter(null, requireContext());
        recyclerView.setAdapter(adapter);

        // Set the reserve button listener
        adapter.setOnReserveClickListener(item -> {
            // Call the dialog method here
            showReserveFoodDialog(
                    item.getQuantity(),
                    foodBankId,
                    item.getId(),
                    item.getCategory()
            );
        });

        // Load data using foodBankId
        if (foodBankId != null) {
            viewModel.loadFoodBankDetails(foodBankId);
            viewModel.loadFoodBankInventory(foodBankId);
        }

        setupObservers();
    }

    private void setupObservers() {
        // Observe foodbank details
        viewModel.getSelectedFoodBank().observe(getViewLifecycleOwner(), foodBank -> {
            if (foodBank != null) {
                nameTextView.setText(foodBank.getName());

                Glide.with(requireContext())
                        .load(foodBank.getImageUrl())
                        .placeholder(R.drawable.food_placeholder)
                        .error(R.drawable.error_cross)
                        .into(imageView);
            }
        });

        // Observe inventory
        viewModel.getFoodBankInventory().observe(getViewLifecycleOwner(), inventoryItems -> {
            if (inventoryItems != null && !inventoryItems.isEmpty()) {
                adapter.setInventoryList(inventoryItems);
                recyclerView.setVisibility(View.VISIBLE);
                noInventoryMessage.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.GONE);
                noInventoryMessage.setVisibility(View.VISIBLE);
            }
            progressBar.setVisibility(View.GONE);
        });

        // Observe inventory loading state
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                recyclerView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
                noInventoryMessage.setVisibility(View.GONE);
            }
        });

        // Observe inventory errors
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                showError(errorMessage);
            }
        });

        // Observe reservation success
        reservationViewModel.getSuccessMessage().observe(getViewLifecycleOwner(), message -> {
            Log.d("FoodbankDetailFragment", "Success Message: " + message);
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            viewModel.loadFoodBankInventory(foodBankId); // Refresh inventory
        });

        // Observe reservation errors
        reservationViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        });

        // Observe success dialog trigger
        reservationViewModel.getShowSuccessDialog().observe(getViewLifecycleOwner(), show -> {
            Log.d("FoodbankDetailFragment", "Showing success dialog");
            if (Boolean.TRUE.equals(show)) {
                DialogSuccessFragment dialogFragment = DialogSuccessFragment.newInstance();
                dialogFragment.onReserve();
                dialogFragment.show(getChildFragmentManager(), DialogSuccessFragment.TAG);
                reservationViewModel.clearDialogFlags(); // Reset dialog flag
            }
        });

        // Observe error dialog trigger
        reservationViewModel.getShowErrorDialog().observe(getViewLifecycleOwner(), show -> {
            if (Boolean.TRUE.equals(show)) {
                DialogSuccessFragment dialogFragment = DialogSuccessFragment.newInstance();
                dialogFragment.onReservationError();
                dialogFragment.show(getChildFragmentManager(), DialogSuccessFragment.TAG);
                reservationViewModel.clearDialogFlags(); // Reset dialog flag
            }
        });

    }

    private void showError(String errorMessage) {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        noInventoryMessage.setVisibility(View.VISIBLE);
        noInventoryMessage.setText(errorMessage);
    }

    private void showReserveFoodDialog(int availableQuantity, String foodBankId, String inventoryId, String category) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_reserve_food, null);
        builder.setView(dialogView);

        TextView tvFoodName = dialogView.findViewById(R.id.TVReserveTitle);
        TextView tvQuantity = dialogView.findViewById(R.id.tv_quantity);
        Button btnDecrement = dialogView.findViewById(R.id.btn_decrement);
        Button btnIncrement = dialogView.findViewById(R.id.btn_increment);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnConfirm = dialogView.findViewById(R.id.btn_confirm);

        tvFoodName.setText("Enter the quantity of " + category + " you want to reserve\n(Maximum 3 units per day)");
        final int[] quantity = {1};

        btnDecrement.setOnClickListener(v -> {
            if (quantity[0] > 1) {
                quantity[0]--;
                tvQuantity.setText(String.valueOf(quantity[0]));
            }
        });

        btnIncrement.setOnClickListener(v -> {
            if (quantity[0] < 3 && quantity[0] < availableQuantity) {
                quantity[0]++;
                tvQuantity.setText(String.valueOf(quantity[0]));
            }
        });

        AlertDialog dialog = builder.create();

        // Apply full-screen attributes
        dialog.setOnShowListener(dialogInterface -> {
            if (dialog.getWindow() != null) {
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                // Center the dialog on the screen
                WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
                layoutParams.gravity = Gravity.CENTER;
                // Add some window margins if needed
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialog.getWindow().setAttributes(layoutParams);
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            Log.d("FoodbankDetailFragment", "Confirm button clicked");
            reservationViewModel.reserveFood(foodBankId, inventoryId, category, quantity[0]);
            dialog.dismiss();
        });

        // Create and show dialog
        dialog.show();
    }
}