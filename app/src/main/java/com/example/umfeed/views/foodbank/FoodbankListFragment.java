package com.example.umfeed.views.foodbank;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.umfeed.R;
import com.example.umfeed.adapters.FoodBankAdapter;
import com.example.umfeed.viewmodels.foodbank.FoodbankListViewModel;

public class FoodbankListFragment extends Fragment {

    private FoodbankListViewModel viewModel;
    private RecyclerView recyclerView;
    private FoodBankAdapter foodBankAdapter;

    public static FoodbankListFragment newInstance() {
        return new FoodbankListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for the fragment
        View rootView = inflater.inflate(R.layout.fragment_foodbank_list, container, false);

        // Initialize RecyclerView
        recyclerView = rootView.findViewById(R.id.foodBankRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2)); // 2 items per row

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize the ViewModel
        viewModel = new ViewModelProvider(this).get(FoodbankListViewModel.class);

        // Observe the food bank list and update the adapter
        viewModel.getFoodBankList().observe(getViewLifecycleOwner(), foodBanks -> {
            if (foodBanks != null) {
                // Initialize or update the adapter with the new data
                if (foodBankAdapter == null) {
                    foodBankAdapter = new FoodBankAdapter(foodBanks, getContext());
                    recyclerView.setAdapter(foodBankAdapter);
                } else {
                    foodBankAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}