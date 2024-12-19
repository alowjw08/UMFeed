package com.example.umfeed.views.foodbank;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.umfeed.R;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

    public FoodbankDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_foodbank_detail, container, false);

        // Initialize views
        recyclerView = view.findViewById(R.id.inventoryRecyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        noInventoryMessage = view.findViewById(R.id.no_inventory_message);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(FoodbankDetailViewModel.class);

        // Setup RecyclerView and Adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FoodBankInventoryAdapter(null, getContext());
        recyclerView.setAdapter(adapter);

        // Get selected foodbank from arguments (or wherever you store the selected foodbank)
        FoodBank selectedFoodBank = getArguments().getParcelable("selectedFoodBank");
        if (selectedFoodBank != null) {
            viewModel.setSelectedFoodBank(selectedFoodBank);
            viewModel.loadFoodBankInventory(selectedFoodBank.getId());
        }

        // Observe data from ViewModel
        viewModel.getFoodBankInventory().observe(getViewLifecycleOwner(), inventoryItems -> {
            if (inventoryItems != null && !inventoryItems.isEmpty()) {
                adapter.setInventoryList(inventoryItems);
                recyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                noInventoryMessage.setVisibility(View.GONE);
            } else {
                // Show the "No inventory" message when no data is available
                recyclerView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                noInventoryMessage.setVisibility(View.VISIBLE);
            }
        });

        // Observe loading state
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                recyclerView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
            }
        });

        // Observe error messages
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                showError(errorMessage);
            }
        });

        return view;
    }

    private void showError(String errorMessage) {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
    }
}