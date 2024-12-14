package com.example.umfeed.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.umfeed.R;
import com.example.umfeed.models.foodbank.FoodBank;

import java.util.List;
import androidx.navigation.Navigation;

public class FoodBankAdapter extends RecyclerView.Adapter<FoodBankAdapter.FoodBankViewHolder> {

    private List<FoodBank> foodBankList;
    private Context context;

    public FoodBankAdapter(List<FoodBank> foodBankList, Context context) {
        this.foodBankList = foodBankList;
        this.context = context;
    }

    @NonNull
    @Override
    public FoodBankViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_foodbank_card, parent, false);
        return new FoodBankViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FoodBankViewHolder holder, int position) {
        FoodBank foodBank = foodBankList.get(position);
        holder.foodBankName.setText(foodBank.getName());
        holder.foodBankImage.setImageResource(foodBank.getImageResId());

        // Set onClickListener to navigate to the detail fragment
        holder.itemView.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString("foodBankName", foodBank.getName());
            bundle.putInt("foodBankImageResId", foodBank.getImageResId());

//            TODO: Setup Nav logic
//            Navigation.findNavController(view)
//                    .navigate(R.id.action_foodbankListFragment_to_foodbankDetailFragment, bundle);
        });
    }

    @Override
    public int getItemCount() {
        return foodBankList == null ? 0 : foodBankList.size();
    }

//    NO NEED THIS (?)
//    public void updateFoodBankList(List<FoodBank> newList) {
//        this.foodBankList = newList;
//        notifyDataSetChanged();
//    }

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