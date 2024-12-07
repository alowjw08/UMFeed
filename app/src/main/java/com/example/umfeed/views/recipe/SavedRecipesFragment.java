package com.example.umfeed.views.recipe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.umfeed.adapters.RecipeAdapter;
import com.example.umfeed.databinding.FragmentSavedRecipesBinding;
import com.example.umfeed.viewmodels.recipe.SavedRecipeViewModel;
import com.google.android.material.snackbar.Snackbar;

public class SavedRecipesFragment extends Fragment {
    private SavedRecipeViewModel viewModel;
    private RecipeAdapter adapter;
    private FragmentSavedRecipesBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(SavedRecipeViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSavedRecipesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        observeViewModel();
    }

    private void setupRecyclerView() {
        adapter = new RecipeAdapter(recipe -> {
            NavDirections action = SavedRecipesFragmentDirections.actionSavedRecipesToRecipeDetail(recipe.getId());
            Navigation.findNavController(requireView()).navigate(action);
        });

        binding.savedRecipesRecyclerView.setAdapter(adapter);
        binding.savedRecipesRecyclerView.setLayoutManager(
                new GridLayoutManager(requireContext(),1, GridLayoutManager.VERTICAL, false)
        );
    }

    private void observeViewModel() {
        viewModel.getSavedRecipes().observe(getViewLifecycleOwner(), recipes -> {
            adapter.submitList(recipes);
            binding.emptyStateText.setVisibility(
                    recipes.isEmpty() ? View.VISIBLE : View.GONE
            );
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE));

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Snackbar.make(binding.getRoot(), error, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.loadSavedRecipes();
    }
}