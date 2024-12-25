package com.example.umfeed.adapters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.umfeed.R;
import com.example.umfeed.models.foodbank.FoodBankInventoryItem;
import com.example.umfeed.utils.CategoryImageUtil;

import java.util.List;

public class FoodBankInventoryAdapter extends RecyclerView.Adapter<FoodBankInventoryAdapter.InventoryViewHolder> {

    private List<FoodBankInventoryItem> inventoryList;
    private Context context;
    private OnReserveClickListener onReserveClickListener;
    private boolean isB40;


    public interface OnReserveClickListener {
        void onReserveClick(FoodBankInventoryItem item);
    }

    public void setB40Status(boolean isB40) {
        this.isB40 = isB40;
        notifyDataSetChanged(); // Refresh the list
    }

    public void setOnReserveClickListener(OnReserveClickListener listener) {
        this.onReserveClickListener = listener;
    }

    public FoodBankInventoryAdapter(List<FoodBankInventoryItem> inventoryList, Context context) {
        this.inventoryList = inventoryList;
        this.context = context;
    }

    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food_bank_inventory, parent, false);
        return new InventoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryViewHolder holder, int position) {
        FoodBankInventoryItem inventoryItem = inventoryList.get(position);

        // Show or hide the "Reserve" button based on B40 status
        holder.reserveButton.setVisibility(isB40 ? View.VISIBLE : View.GONE);

        holder.reserveButton.setOnClickListener(v -> {
            if (onReserveClickListener != null) {
                onReserveClickListener.onReserveClick(inventoryItem);
            }
        });

        holder.foodCategory.setText(inventoryItem.getCategory());
        holder.foodQuantity.setText(String.valueOf(inventoryItem.getQuantity()));

        int imageResId = CategoryImageUtil.getImageResourceByCategory(inventoryItem.getCategory());
        holder.foodImage.setImageResource(imageResId);

        holder.reserveButton.setOnClickListener(v -> {
            if (onReserveClickListener != null) {
                onReserveClickListener.onReserveClick(inventoryItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return inventoryList == null ? 0 : inventoryList.size();
    }

    // Method to update the inventory list
    public void setInventoryList(List<FoodBankInventoryItem> inventoryList) {
        this.inventoryList = inventoryList;
        notifyDataSetChanged();
    }

    // Helper method to map food categories to drawable resources
    private int getImageResourceByCategory(String category) {
        switch (category) {
            case "Biscuits and Snacks":
                return R.drawable.biscuits_and_snacks;
            case "Canned and Preserved Foods":
                return R.drawable.canned_foods;
            case "Beverages":
                return R.drawable.beverages;
            case "Rice and Grains":
                return R.drawable.rice_and_grain;
            case "Dehydrated Foods":
                return R.drawable.dehydrated_food;
            case "Nuts and Seeds":
                return R.drawable.nuts_and_seeds;
            case "Proteins":
                return R.drawable.protein;
            case "Condiments and Seasonings":
                return R.drawable.condiments_and_seasonings;
            case "Powdered Food":
                return R.drawable.powdered_food;
            case "Pasta and Noodles":
                return R.drawable.pasta_and_nooodles;
            default:
                return R.drawable.food_placeholder; // Default image
        }
    }

    // ViewHolder class for caching views
    public static class InventoryViewHolder extends RecyclerView.ViewHolder {
        public TextView foodCategory;
        public TextView foodQuantity;
        public ImageView foodImage;
        Button reserveButton;

        public InventoryViewHolder(@NonNull View itemView) {
            super(itemView);
            foodCategory = itemView.findViewById(R.id.category_text);
            foodQuantity = itemView.findViewById(R.id.quantity_text);
            foodImage = itemView.findViewById(R.id.foodbankDetailsImage);
            reserveButton = itemView.findViewById(R.id.reserve_button);
        }
    }
}
