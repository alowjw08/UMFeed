package com.example.umfeed.views;

import android.content.Intent;
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
import com.example.umfeed.adapters.MenuRahmahAdapter;

import com.example.umfeed.repositories.MenuRahmahRepository;
import com.example.umfeed.repositories.UserRepository;
import com.example.umfeed.viewmodels.MainViewModel;
import com.example.umfeed.utils.TimeUtils;
import com.example.umfeed.viewmodels.MainViewModelFactory;
import com.example.umfeed.views.auth.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

public class HomeFragment extends Fragment {
    private MainViewModel viewModel;
    private TextView greetingText;
    private MenuRahmahAdapter menuRahmahAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        DailyPinWorkManager.triggerWorkerManually(getContext());

        // Create repositories
        UserRepository userRepository = new UserRepository();
        MenuRahmahRepository menuRepository = new MenuRahmahRepository();

        // Create factory
        MainViewModelFactory factory = new MainViewModelFactory(userRepository, menuRepository);

        // Get ViewModel using factory
        viewModel = new ViewModelProvider(this, factory).get(MainViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initializeViews(view);
        setupClickListeners(view);
        observeViewModel();

        view.findViewById(R.id.temp_sign_out_button).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(requireActivity(), LoginActivity.class));
            requireActivity().finish();
        });

        return view;
    }

    private void initializeViews(View view) {
        greetingText = view.findViewById(R.id.greeting_text);

        // Initialize adapter with empty list
        menuRahmahAdapter = new MenuRahmahAdapter(getContext());

        // Setup featured menus RecyclerView
        RecyclerView featuredMenusRecyclerView = view.findViewById(R.id.featured_menus_recycler_view);
        featuredMenusRecyclerView.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        featuredMenusRecyclerView.setAdapter(menuRahmahAdapter);
    }

    private void setupClickListeners(View view) {
        // Action button clicks
        view.findViewById(R.id.donation_card).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_home_to_donationMain));

        view.findViewById(R.id.leaderboard_card).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_home_to_leaderboard));

        view.findViewById(R.id.healthy_recipe_card).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_home_to_recipes));

        // All menu button click
        view.findViewById(R.id.all_menu_button).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_home_to_menuRahmahList));
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

        // Observe the menu list and update the adapter's data
        viewModel.getMenuList().observe(getViewLifecycleOwner(), menus -> {
            if (menus != null) {
                menuRahmahAdapter.updateMenuList(menus);  // Use custom method to update the list
            }
        });

        // Set the item click listener for menu items
        menuRahmahAdapter.setOnItemClickListener(menuId -> {
            // Create a Bundle to pass the menuId to the next fragment
            Bundle bundle = new Bundle();
            bundle.putString("menu_id", menuId);

            // Navigate to the MenuRahmahDetailFragment with the menuId
            Navigation.findNavController(requireView()).navigate(R.id.action_homeFragment_to_menuRahmahDetailFragment, bundle);
        });
    }
}