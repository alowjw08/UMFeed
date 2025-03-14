package com.example.umfeed.views.foodbank;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.umfeed.R;
import com.example.umfeed.adapters.FoodBankAdapter;
import com.example.umfeed.viewmodels.foodbank.FoodbankListViewModel;

import java.util.ArrayList;

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
        recyclerView.setAdapter(foodBankAdapter);
        // Observe the food bank list and update the adapter
        viewModel.getFoodBankList().observe(getViewLifecycleOwner(), foodBanks -> {
            if (foodBanks != null) {
                if (foodBankAdapter == null) {
                    foodBankAdapter = new FoodBankAdapter(foodBanks, getContext(), foodBank -> {
                        String foodBankId = foodBank.getId();
                        if (foodBankId != null) {
                            try {
                                Log.d("FoodbankListFragment", "Foodbank id pass to deeplink " + foodBankId);
                                FoodbankListFragmentDirections.ActionFoodBankListFragmentToFoodBankDetailFragment action =
                                        FoodbankListFragmentDirections.actionFoodBankListFragmentToFoodBankDetailFragment(foodBankId);
                                NavHostFragment.findNavController(this).navigate(action);
                            } catch (Exception e) {
                                Log.e("FoodbankListFragment", "Navigation failed", e);
                                // Optionally show error to user
                            }
                        } else {
                            Log.e("FoodbankListFragment", "FoodBank ID is null");
                            // Optionally show error to user
                        }
                    });
                    recyclerView.setAdapter(foodBankAdapter);
                } else {
                    foodBankAdapter.notifyDataSetChanged();
                }
            } else {
                Log.d("FoodBankListGragmnet", "foodbank list is null");
            }
        });
    }
}