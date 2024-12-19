package com.example.umfeed.views.foodbank;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.umfeed.R;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.umfeed.adapters.FoodBankInventoryAdapter;
import com.example.umfeed.models.foodbank.FoodBank;
import com.example.umfeed.viewmodels.foodbank.FoodbankDetailViewModel;

public class FoodbankDetailFragment extends Fragment {

    private FoodbankDetailViewModel viewModel;
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

        // Observe loading state
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                recyclerView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
                noInventoryMessage.setVisibility(View.GONE);
            }
        });

        // Observe errors
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                showError(errorMessage);
            }
        });
    }

    private void showError(String errorMessage) {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        noInventoryMessage.setVisibility(View.VISIBLE);
        noInventoryMessage.setText(errorMessage);
    }
}