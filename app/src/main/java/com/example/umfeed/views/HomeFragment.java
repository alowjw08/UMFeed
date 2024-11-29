package com.example.umfeed.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.umfeed.R;
import com.example.umfeed.adapters.MenuRahmahAdapter;
import com.example.umfeed.viewmodels.MainViewModel;

public class HomeFragment extends Fragment {
    private MainViewModel viewModel;
    private MenuRahmahAdapter menuRahmahAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        setupRecyclerView(view);
        setupClickListeners(view);
        observeViewModel();

        return view;
    }

    private void setupRecyclerView(View view) {
        RecyclerView menuRecyclerView = view.findViewById(R.id.menu_rahmah_recycler_view);
        menuRahmahAdapter = new MenuRahmahAdapter();
        menuRecyclerView.setAdapter(menuRahmahAdapter);
    }

    private void setupClickListeners(View view) {
        view.findViewById(R.id.donation_button).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_home_to_donation));

        view.findViewById(R.id.leaderboard_button).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_home_to_leaderboard));

        view.findViewById(R.id.recipes_button).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_home_to_recipes));
    }

    private void observeViewModel() {
        viewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
        });

        viewModel.getFeaturedMenus().observe(getViewLifecycleOwner(), menus -> {
            menuRahmahAdapter.submitList(menus);
        });
    }
}
