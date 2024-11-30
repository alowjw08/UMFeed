package com.example.umfeed.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.umfeed.R;
import com.example.umfeed.models.menu.MenuRahmah;

public class FeaturedMenuAdapter extends ListAdapter<MenuRahmah, FeaturedMenuAdapter.MenuViewHolder> {

    private final OnMenuClickListener clickListener;

    public FeaturedMenuAdapter(OnMenuClickListener clickListener) {
        super(DIFF_CALLBACK);
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.featured_menus_recycler_view, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        MenuRahmah menu = getItem(position);
        holder.bind(menu, clickListener);
    }

    static class MenuViewHolder extends RecyclerView.ViewHolder {
        private final ImageView menuImage;
        private final TextView menuName;
        private final TextView menuPrice;
        private final TextView restaurantName;

        MenuViewHolder(View itemView) {
            super(itemView);
            menuImage = itemView.findViewById(R.id.menu_image);
            menuName = itemView.findViewById(R.id.menu_name);
            menuPrice = itemView.findViewById(R.id.menu_price);
            restaurantName = itemView.findViewById(R.id.restaurant_name);
        }

        void bind(MenuRahmah menu, OnMenuClickListener listener) {
            menuName.setText(menu.getName());
            menuPrice.setText(String.format("RM%.2f", menu.getPrice()));
            restaurantName.setText(menu.getStall().getName());

            Glide.with(itemView.getContext())
                    .load(menu.getImageUrl())
                    .centerCrop()
                    .into(menuImage);

            itemView.setOnClickListener(v -> listener.onMenuClick(menu.getId()));
        }
    }

    public interface OnMenuClickListener {
        void onMenuClick(String menuId);
    }

    private static final DiffUtil.ItemCallback<MenuRahmah> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<MenuRahmah>() {
                @Override
                public boolean areItemsTheSame(@NonNull MenuRahmah oldItem,
                                               @NonNull MenuRahmah newItem) {
                    return oldItem.getId().equals(newItem.getId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull MenuRahmah oldItem,
                                                  @NonNull MenuRahmah newItem) {
                    return oldItem.equals(newItem);
                }
            };
}