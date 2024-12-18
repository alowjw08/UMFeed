package com.example.umfeed.views.donation;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.umfeed.adapters.FoodBankAdapter;
import com.example.umfeed.R;
import com.example.umfeed.viewmodels.foodbank.FoodbankListViewModel;

public class DonationListFragment extends Fragment {

    private FoodbankListViewModel viewModel;
    private RecyclerView recyclerView;
    private FoodBankAdapter foodBankAdapter;

    public static DonationListFragment newInstance() {
        return new DonationListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for the fragment
        View rootView = inflater.inflate(R.layout.fragment_donation_list, container, false);

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

        NavController navController = NavHostFragment.findNavController(this);

        // Initialize Adapter
        foodBankAdapter = new FoodBankAdapter(null, requireContext(), foodBank -> {
            // Navigate to the DonationFragment
            Bundle bundle = new Bundle();
            bundle.putString("foodBankName", foodBank.getName());
//            Navigation.findNavController(requireView()).navigate(R.id.action_donation_list_to_donation_fragment);
            navController.navigate(R.id.action_donation_list_to_donation_fragment, bundle);
        });
        recyclerView.setAdapter(foodBankAdapter);

        // Observe LiveData
        viewModel.getFoodBankList().observe(getViewLifecycleOwner(), foodBanks -> {
            if (foodBanks != null) {
                foodBankAdapter.setFoodBankList(foodBanks);
                foodBankAdapter.notifyDataSetChanged();
            }
        });
    }
}