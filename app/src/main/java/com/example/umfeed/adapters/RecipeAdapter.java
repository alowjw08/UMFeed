package com.example.umfeed.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.umfeed.R;
import com.example.umfeed.databinding.ItemRecipeCardBinding;
import com.example.umfeed.models.recipe.Recipe;



public class RecipeAdapter extends ListAdapter<Recipe, RecipeAdapter.RecipeViewHolder> {
    private final OnRecipeClickListener listener;

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe); // Changed from OnRecipeClick to onRecipeClick
    }

    public RecipeAdapter(OnRecipeClickListener listener) {
        super(new DiffUtil.ItemCallback<Recipe>(){
            @Override
            public boolean areItemsTheSame(@NonNull Recipe oldItem, @NonNull Recipe newItem) {
                return oldItem.getId().equals(newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Recipe oldItem, @NonNull Recipe newItem) {
                return oldItem.equals(newItem);
            }
        });
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position){
        ItemRecipeCardBinding binding = ItemRecipeCardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new RecipeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = getItem(position);
        holder.bind(recipe, listener);
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        private final ItemRecipeCardBinding binding;

        public RecipeViewHolder(@NonNull ItemRecipeCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Recipe recipe, OnRecipeClickListener listener) {
            if (recipe != null) {
                binding.recipeName.setText(recipe.getName());
                binding.caloriesText.setText(
                        itemView.getContext().getString(R.string.calories_format, recipe.getCalories())
                );

                // Load image using Glide with null check
                if (recipe.getImageUrl() != null) {
                    Glide.with(itemView.getContext())
                            .load(recipe.getImageUrl())
                            .placeholder(R.drawable.placeholder_recipe)
                            .error(R.drawable.error_recipe)
                            .into(binding.recipeImage);
                }

                // Set click listener with null check
                if (listener != null) {
                    itemView.setOnClickListener(v -> listener.onRecipeClick(recipe));
                }
            }
        }
    }

}
