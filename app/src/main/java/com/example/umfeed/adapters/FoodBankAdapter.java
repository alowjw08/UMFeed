package com.example.umfeed.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.umfeed.R;
import com.example.umfeed.models.foodbank.FoodBank;

import java.util.List;

public class FoodBankAdapter extends RecyclerView.Adapter<FoodBankAdapter.FoodBankViewHolder> {

    private List<FoodBank> foodBankList;
    private Context context;
    private OnFoodBankClickListener clickListener;

    public FoodBankAdapter(List<FoodBank> foodBankList, Context context, OnFoodBankClickListener clickListener) {
        this.foodBankList = foodBankList;
        this.context = context;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public FoodBankViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_foodbank_card, parent, false);
        return new FoodBankViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodBankViewHolder holder, int position) {
        FoodBank foodBank = foodBankList.get(position);
        holder.foodBankName.setText(foodBank.getName());
        Glide.with(context)
                .load(foodBank.getImageUrl())
                .into(holder.foodBankImage);

        holder.itemView.setOnClickListener(view -> {
            if (clickListener != null) {
                clickListener.onFoodBankClick(foodBank);  // Pass the clicked foodBank object to the listener
            }
        });
    }

    @Override
    public int getItemCount() {
        return foodBankList == null ? 0 : foodBankList.size();  // Safely return the list size
    }

    // Method to update the list of FoodBanks
    public void setFoodBankList(List<FoodBank> foodBankList) {
        if (foodBankList != null) {
            this.foodBankList = foodBankList;
            notifyDataSetChanged();  // Notify RecyclerView to update the UI
        }
    }

    // Interface for the click listener
    public interface OnFoodBankClickListener {
        void onFoodBankClick(FoodBank foodBank);  // Callback for click events
    }

    // ViewHolder class for caching views
    public static class FoodBankViewHolder extends RecyclerView.ViewHolder {
        public TextView foodBankName;
        public ImageView foodBankImage;

        public FoodBankViewHolder(View itemView) {
            super(itemView);
            foodBankName = itemView.findViewById(R.id.foodBankName);
            foodBankImage = itemView.findViewById(R.id.foodBankImage);
        }
    }
}

