package com.example.umfeed.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.umfeed.R;
import com.example.umfeed.databinding.ItemRecipeCardBinding;
import com.example.umfeed.models.recipe.Recipe;
import com.example.umfeed.viewmodels.recipe.SavedRecipeViewModel;
import com.google.android.material.snackbar.Snackbar;


public class RecipeAdapter extends ListAdapter<Recipe, RecipeAdapter.RecipeViewHolder> {
    private final OnRecipeClickListener listener;
    private final SavedRecipeViewModel savedRecipeViewModel;
    private static final String TAG = "RecipeAdapter";

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    public RecipeAdapter(OnRecipeClickListener listener) {
        super(new DiffUtil.ItemCallback<Recipe>() {
            @Override
            public boolean areItemsTheSame(@NonNull Recipe oldItem, @NonNull Recipe newItem) {
                if (oldItem == null || newItem == null) return false;
                if (oldItem.getId() == null || newItem.getId() == null) {
                    return oldItem == newItem;
                }
                return oldItem.getId().equals(newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Recipe oldItem, @NonNull Recipe newItem) {
                if (oldItem == null || newItem == null) return false;
                return oldItem.equals(newItem);
            }
        });
        this.listener = listener;
        this.savedRecipeViewModel = new SavedRecipeViewModel();
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position){
        ItemRecipeCardBinding binding = ItemRecipeCardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new RecipeViewHolder(binding, savedRecipeViewModel);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = getItem(position);
        Log.d("RecipeAdapter", "Binding recipe: " + recipe.getName());
        holder.bind(recipe, clickedRecipe -> {
            Log.d("RecipeAdapter", "Recipe clicked: " + clickedRecipe.getName());
            listener.onRecipeClick(clickedRecipe);
        });

        // Observe saved states map for this specific recipe
        savedRecipeViewModel.getSavedStates().observe((LifecycleOwner) holder.itemView.getContext(), states -> {
            if (states.containsKey(recipe.getId())) {
                holder.updateSaveButtonState(states.get(recipe.getId()));
            }
        });

        // Observe loading and error states
        savedRecipeViewModel.getIsLoading().observe((LifecycleOwner) holder.itemView.getContext(), isLoading -> {
            if (!isLoading) {
                holder.binding.saveButton.setEnabled(true);
            }
        });

        savedRecipeViewModel.getError().observe((LifecycleOwner) holder.itemView.getContext(), error -> {
            if (error != null) {
                Snackbar.make(holder.binding.getRoot(),
                        "Failed to update saved status: " + error,
                        Snackbar.LENGTH_LONG).show();
                holder.binding.saveButton.setEnabled(true);
            }
        });
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        private final ItemRecipeCardBinding binding;
        private final SavedRecipeViewModel savedRecipeViewModel;
        private Recipe currentRecipe;

        public RecipeViewHolder(@NonNull ItemRecipeCardBinding binding, SavedRecipeViewModel savedRecipeViewModel) {
            super(binding.getRoot());
            this.binding = binding;
            this.savedRecipeViewModel = savedRecipeViewModel;
        }

        public void bind(Recipe recipe, OnRecipeClickListener listener) {
            if (recipe != null) {
                Log.d(TAG, "Binding recipe: " + recipe.getName());
                currentRecipe = recipe;

                binding.recipeName.setText(recipe.getName());
                binding.caloriesText.setText(
                        itemView.getContext().getString(R.string.calories_format, recipe.getCalories())
                );

                savedRecipeViewModel.checkIsSaved(recipe.getId());

                updateSaveButtonState(savedRecipeViewModel.getSavedState(recipe.getId()));

                binding.saveButton.setOnClickListener(v -> {
                    if (recipe.getId() != null) {
                        binding.saveButton.setEnabled(false);
                        savedRecipeViewModel.toggleSaveRecipe(recipe.getId());
                    }
                });

                // Load image
                Glide.with(itemView.getContext())
                        .load(recipe.getImageUrl())
                        .placeholder(R.drawable.placeholder_recipe)
                        .error(R.drawable.error_recipe)
                        .into(binding.recipeImage);

                // Set click listener for the whole card
                binding.getRoot().setOnClickListener(v -> {
                    Log.d(TAG, "Recipe clicked: " + recipe.getName());
                    listener.onRecipeClick(recipe);
                });
            }
        }

        void updateSaveButtonState(Boolean isSaved) {
            if (isSaved != null) {
                binding.saveButton.setIcon(ContextCompat.getDrawable(itemView.getContext(),
                        isSaved ? R.drawable.ic_bookmark_filled : R.drawable.ic_bookmark_border));
                binding.saveButton.setEnabled(true);
            }
        }
    }
}
