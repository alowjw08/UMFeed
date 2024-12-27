package com.example.umfeed.adapters;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.umfeed.R;
import com.example.umfeed.models.user.Donation;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class UserDonationAdapter extends RecyclerView.Adapter<UserDonationAdapter.DonationViewHolder> {
    private List<Donation> donationList;
    private final Context context;

    public UserDonationAdapter(List<Donation> donationList, Context context) {
        this.donationList = donationList;
        this.context = context;
    }

    @NonNull
    @Override
    public DonationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_food_donated, parent, false);
        return new DonationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DonationViewHolder holder, int position) {
        Donation donation = donationList.get(position);
        holder.category.setText(donation.getCategory());
        holder.quantity.setText(String.valueOf(donation.getQuantity()) + " pack");
        fetchFoodBankName(donation.getFoodBankId(), holder.foodBankName);
        int imageResId = getImageForCategory(donation.getCategory());
        holder.image.setImageResource(imageResId);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        holder.donationDate.setText("Donated: " + dateFormat.format(donation.getDonationDate()));
    }

    @Override
    public int getItemCount() {
        return donationList.size();
    }

    private void fetchFoodBankName(String foodBankId, TextView textView) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("foodBanks").document(foodBankId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String foodBankName = documentSnapshot.getString("name");
                        textView.setText(foodBankName != null ? "Food Bank: " + foodBankName : "Unknown Food Bank");
                    } else {
                        textView.setText("Unknown Food Bank");
                    }
                })
                .addOnFailureListener(e -> textView.setText("Error loading Food Bank"));
    }

    public int getImageForCategory(String category) {
        switch (category) {
            case "Rice and Grains":
                return R.drawable.rice_and_grain;
            case "Canned and Preserved Foods":
                return R.drawable.canned_foods;
            case "Dehydrated Foods":
                return R.drawable.dehydrated_food;
            case "Nuts and Seeds":
                return R.drawable.nuts_and_seeds;
            case "Proteins":
                return R.drawable.protein;
            case "Condiments and Seasonings":
                return R.drawable.condiments_and_seasonings;
            case "Biscuits and Snacks":
                return R.drawable.biscuits_and_snacks;
            case "Beverages":
                return R.drawable.beverages;
            case "Powdered Food":
                return R.drawable.powdered_food;
            case "Pasta and Noodles":
                return R.drawable.pasta_and_nooodles;
        }
        return 0;
    }

    public void updateDonations(List<Donation> newDonations) {
        this.donationList = newDonations;
        notifyDataSetChanged();
    }

    public static class DonationViewHolder extends RecyclerView.ViewHolder {
        TextView category, quantity, foodBankName, donationDate;
        ImageView image;

        public DonationViewHolder(@NonNull View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.donationFoodCategory);
            quantity = itemView.findViewById(R.id.donationFoodQuantity);
            foodBankName = itemView.findViewById(R.id.donationFoodBank);
            donationDate = itemView.findViewById(R.id.donationDateText);
            image = itemView.findViewById(R.id.donationFoodImage);
        }
    }
}
