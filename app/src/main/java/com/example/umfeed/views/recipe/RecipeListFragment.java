package com.example.umfeed.views.recipe;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.appcompat.widget.SearchView;

import com.example.umfeed.adapters.RecipeAdapter;
import com.example.umfeed.databinding.FragmentRecipeListBinding;
import com.example.umfeed.viewmodels.recipe.RecipeViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;


public class RecipeListFragment extends Fragment {
    private RecipeViewModel viewModel;
    private RecipeAdapter adapter;
    private FragmentRecipeListBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(RecipeViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRecipeListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        setupSearchView();
        setupFilterChips();
        observeViewModel();
    }

    private void setupRecyclerView() {
        adapter = new RecipeAdapter(recipe -> {
            // Navigate to recipe detail
            NavDirections action = RecipeListFragmentDirections
                    .actionRecipeListToDetail(recipe.getId());
            Navigation.findNavController(requireView()).navigate(action);
        });

        binding.recipesRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.recipesRecyclerView.setAdapter(adapter);
        binding.recipesRecyclerView.setVisibility(View.VISIBLE);
    }

    private void setupSearchView() {
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Optional: Enable real-time search
                // performSearch(newText);
                return false;
            }
        });

        // Add clear button listener
        binding.searchView.setOnCloseListener(() -> {
            viewModel.loadRecipes(); // Reset to show all recipes
            return false;
        });
    }

    private void performSearch(String query) {
        // Show loading state
        binding.progressBar.setVisibility(View.VISIBLE);

        // Hide keyboard after search
        InputMethodManager imm = (InputMethodManager) requireContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(binding.searchView.getWindowToken(), 0);

        // Perform search
        viewModel.searchRecipes(query);
    }

    private void setupFilterChips() {
        // Add filter chips dynamically
        String[] categories = {"Low Carb", "High Protein", "Vegetarian", "Vegan"};
        for (String category : categories) {
            Chip chip = new Chip(requireContext());
            chip.setText(category);
            chip.setCheckable(true);
            binding.filterChipGroup.addView(chip);
        }

        binding.filterChipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Chip chip = group.findViewById(checkedId);
            if (chip != null) {
                viewModel.filterRecipesByCategory(chip.getText().toString());
            }
        });
    }

    private void observeViewModel() {
        viewModel.getRecipes().observe(getViewLifecycleOwner(), recipes -> {
            // Add debug logging
            Log.d("RecipeListFragment", "Received " + (recipes != null ? recipes.size() : 0) + " recipes");

            if (recipes != null && !recipes.isEmpty()) {
                binding.recipesRecyclerView.setVisibility(View.VISIBLE);
                binding.emptyStateView.setVisibility(View.GONE);
                binding.progressBar.setVisibility(View.GONE);
                adapter.submitList(recipes);
            } else {
                binding.recipesRecyclerView.setVisibility(View.GONE);
                binding.emptyStateView.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);
            }
        });

        // Add error handling
        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), "Error: " + error, Toast.LENGTH_LONG).show();
                binding.progressBar.setVisibility(View.GONE);
            }
        });

        // Show loading state
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}