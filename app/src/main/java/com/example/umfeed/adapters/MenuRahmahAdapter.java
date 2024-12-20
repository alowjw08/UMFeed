package com.example.umfeed.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.umfeed.R;
import com.example.umfeed.models.menu_rahmah.MenuRahmah;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MenuRahmahAdapter extends ListAdapter<MenuRahmah, MenuRahmahAdapter.MenuRahmahViewHolder> {
    private final Context context;
    private OnItemClickListener onItemClickListener;

    public MenuRahmahAdapter(Context context) {
        super(MenuRahmah.DIFF_CALLBACK);
        this.context = context;
    }

    @NonNull
    @Override
    public MenuRahmahViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_menu_rahmah_card, parent, false);
        return new MenuRahmahViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuRahmahViewHolder holder, int position) {
        MenuRahmah menuRahmah = getItem(position);
        holder.bind(menuRahmah);
    }

    public void updateMenuList(QuerySnapshot newMenuList) {
        List<MenuRahmah> menuList = new ArrayList<>();
        // Iterate through the documents in the QuerySnapshot
        for (DocumentSnapshot documentSnapshot : newMenuList.getDocuments()) {
            MenuRahmah menu = documentSnapshot.toObject(MenuRahmah.class);
            if (menu != null) {
                menuList.add(menu);
            }
        }
        // Submit the list of MenuRahmah objects to the adapter
        submitList(menuList);
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    class MenuRahmahViewHolder extends RecyclerView.ViewHolder {
        private final ImageView menuImageView;
        private final TextView menuNameTextView;
        private final TextView restaurantNameTextView;
        private final TextView halalStatusTextView;
        private final TextView vegetarianStatusTextView;
        private final TextView allergensTextView;

        public MenuRahmahViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize the views
            menuImageView = itemView.findViewById(R.id.IVMenu);
            menuNameTextView = itemView.findViewById(R.id.TVMenuName);
            restaurantNameTextView = itemView.findViewById(R.id.TVRestaurantName);
            halalStatusTextView = itemView.findViewById(R.id.TVHalalStatus);
            vegetarianStatusTextView = itemView.findViewById(R.id.TVVegetarianStatus);
            allergensTextView = itemView.findViewById(R.id.TVAllergens);

            itemView.setOnClickListener(v -> {
                if (onItemClickListener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    MenuRahmah menuRahmah = getItem(getAdapterPosition());
                    onItemClickListener.onItemClick(menuRahmah.getId());
                }
            });
        }

        public void bind(MenuRahmah menuRahmah) {
            menuNameTextView.setText(menuRahmah.getMenuName());
            restaurantNameTextView.setText(menuRahmah.getRestaurantName());

            Glide.with(context)
                    .load(menuRahmah.getImageUrl())
                    .placeholder(R.drawable.placeholder_recipe)
                    .error(R.drawable.error_recipe)
                    .into(menuImageView);

            // Set the text for Halal, Vegetarian, and Allergens status
            halalStatusTextView.setText(menuRahmah.getHalalStatus() ? "Halal" : "Non-Halal");
            vegetarianStatusTextView.setText(menuRahmah.getVegetarianStatus() ? "Vegetarian" : "Non-Vegetarian");
            if (menuRahmah.getAllergens() != null && !menuRahmah.getAllergens().isEmpty()) {
                String allergens = "Contains allergens: " + String.join(", ", menuRahmah.getAllergens());
                allergensTextView.setText(allergens);
            } else {
                allergensTextView.setText("No allergens identified");
            }

        }
    }

    public interface OnItemClickListener {
        void onItemClick(String menuId);
    }
}




