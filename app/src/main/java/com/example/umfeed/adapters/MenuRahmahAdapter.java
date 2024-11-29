package com.example.umfeed.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.umfeed.R;
import com.example.umfeed.models.menu.MenuRahmah;

public class MenuRahmahAdapter extends ListAdapter<MenuRahmah, MenuRahmahAdapter.MenuViewHolder> {

    public MenuRahmahAdapter() {
        super(MenuRahmah.DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_menu_card, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MenuViewHolder holder, int position) {
        MenuRahmah menu = getItem(position);
        holder.bind(menu);
    }

    static class MenuViewHolder extends RecyclerView.ViewHolder {
        // ViewHolder implementation
        public MenuViewHolder(View itemView) {
            super(itemView);
        }

        public void bind(MenuRahmah menu) {
            // Bind menu data to views
        }
    }
}