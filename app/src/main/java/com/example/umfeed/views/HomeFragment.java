package com.example.umfeed.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.umfeed.R;
import com.example.umfeed.adapters.FeaturedMenuAdapter;
import com.example.umfeed.viewmodels.MainViewModel;
import com.example.umfeed.utils.TimeUtils;

public class HomeFragment extends Fragment {
    private MainViewModel viewModel;
    private TextView greetingText;
    private FeaturedMenuAdapter featuredMenuAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        featuredMenuAdapter = new FeaturedMenuAdapter(menuId ->
                Navigation.findNavController(requireView())
                        .navigate(MainActivityDirections.actionHomeToMenuList())
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initializeViews(view);
        setupClickListeners(view);
        observeViewModel();

        return view;
    }

    private void initializeViews(View view) {
        greetingText = view.findViewById(R.id.greeting_text);

        // Setup featured menus RecyclerView
        RecyclerView featuredMenusRecyclerView = view.findViewById(R.id);
        featuredMenusRecyclerView.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        featuredMenusRecyclerView.setAdapter(featuredMenuAdapter);
    }

    private void setupClickListeners(View view) {
        // Action button clicks
        view.findViewById(R.id.donation_card).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_home_to_donation));

        view.findViewById(R.id.leaderboard_card).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_home_to_leaderboard));

        view.findViewById(R.id.healthy_recipe_card).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_home_to_recipes));

        // All menu button click
        view.findViewById(R.id.all_menu_button).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_home_to_menu_list));
    }

    private void observeViewModel() {
        // Observe current user for greeting
        viewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                String firstName = user.getFirstName() != null ? user.getFirstName() : "";
                String lastName = user.getLastName() != null ? user.getLastName() : "";
                String fullName = String.format("%s %s", firstName, lastName).trim();

                String greeting = TimeUtils.getGreeting();
                greetingText.setText(String.format("%s,\n%s!", greeting, fullName));
            } else {
                greetingText.setText(TimeUtils.getGreeting());
            }
        });

        viewModel.getFeaturedMenus().observe(getViewLifecycleOwner(), menus -> {
            featuredMenuAdapter.submitList(menus);
        });
    }
}