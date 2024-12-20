package com.example.umfeed.views.menu_rahmah;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.umfeed.R;
import com.example.umfeed.adapters.MenuRahmahAdapter;
import com.example.umfeed.models.menu_rahmah.MenuRahmah;
import com.example.umfeed.viewmodels.menu_rahmah.MenuRahmahViewModel;

import java.util.ArrayList;
import java.util.List;

public class MenuRahmahListFragment extends Fragment {

    private RecyclerView menuRecyclerView;
    private MenuRahmahAdapter adapter;
    private MenuRahmahViewModel viewModel;
    private ProgressBar progressBar;
    private TextView emptyStateView;
    private SwitchCompat switchHalalStatus, switchVegetarianStatus;
    private TextView allergenFilterTextView;

    private List<String> selectedAllergens = new ArrayList<>();

    public MenuRahmahListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu_rahmah_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI components
        menuRecyclerView = view.findViewById(R.id.MenuRahmahRecyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        emptyStateView = view.findViewById(R.id.emptyStateView);
        switchHalalStatus = view.findViewById(R.id.SwitchHalalStatus);
        switchVegetarianStatus = view.findViewById(R.id.SwitchVegetarianStatus);
        allergenFilterTextView = view.findViewById(R.id.allergenFilter);

        setupRecyclerView();
        setupViewModel();
        setupObservers();
        setupFilters();
    }

    private void setupRecyclerView() {
        menuRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new MenuRahmahAdapter(requireContext());
        menuRecyclerView.setAdapter(adapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(MenuRahmahViewModel.class);
    }

    private void setupObservers() {
        // Observe the list of menus
        viewModel.getFilteredMenuList().observe(getViewLifecycleOwner(), menus -> {
            if (menus != null && !menus.isEmpty()) {
                updateMenuList(menus);
            } else {
                displayEmptyState(true);
            }
        });

        // Observe loading state to show or hide a loading indicator
        viewModel.getLoadingState().observe(getViewLifecycleOwner(), isLoading -> {
            displayLoading(isLoading); // Show or hide ProgressBar based on loading state
        });

        // Observe empty state to show empty view or a message if no items are found
        viewModel.getEmptyState().observe(getViewLifecycleOwner(), isEmpty -> {
            displayEmptyState(isEmpty); // Show empty view or message
        });

        // Observe error state to display an error message (e.g., Toast)
        viewModel.getErrorState().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                // Display error message
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        // Navigation to MenuRahmahDetailFragment based on user action
        adapter.setOnItemClickListener(menuId -> {
            // Create a Bundle to pass the menuId to the next fragment
            Bundle bundle = new Bundle();
            bundle.putString("menu_id", menuId);

            // Navigate to the MenuRahmahDetailFragment with the menuId
            Navigation.findNavController(requireView()).navigate(R.id.action_menuRahmahListFragment_to_menuRahmahDetailFragment, bundle);
        });
    }

    private void setupFilters() {
        switchHalalStatus.setOnCheckedChangeListener((buttonView, isChecked) -> filterMenus());
        switchVegetarianStatus.setOnCheckedChangeListener((buttonView, isChecked) -> filterMenus());
        allergenFilterTextView.setOnClickListener(v -> {
            showAllergenDialog();  // Show allergen selection dialog
        });
    }

    private void updateMenuList(List<MenuRahmah> menuRahmahList) {
        adapter.submitList(menuRahmahList);
    }

    private void displayLoading(Boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    private void displayEmptyState(Boolean isEmpty) {
        if (isEmpty) {
            emptyStateView.setVisibility(View.VISIBLE);
            menuRecyclerView.setVisibility(View.GONE);
        } else {
            emptyStateView.setVisibility(View.GONE);
            menuRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void filterMenus() {
        boolean isHalalChecked = switchHalalStatus.isChecked();
        boolean isVegetarianChecked = switchVegetarianStatus.isChecked();
        viewModel.updateFilters(isHalalChecked, isVegetarianChecked, selectedAllergens);
    }

    private void updateAllergenFilter(List<String> selectedAllergens) {
        this.selectedAllergens = selectedAllergens;

        // Update the UI with the selected allergens
        String allergensText = selectedAllergens.isEmpty() ? "No allergens selected" : String.join(", ", selectedAllergens);
        allergenFilterTextView.setText(allergensText);
    }

    private void showAllergenDialog() {
        // List of allergen options
        final String[] allergenOptions = {"prawn", "gluten", "carrots", "dairy", "peanuts"};

        // This array will hold the state of each checkbox (whether it's checked or not)
        final boolean[] selectedAllergensBooleans = new boolean[allergenOptions.length];

        // Pre-populate the state of the checkboxes based on the selected allergens
        for (int i = 0; i < allergenOptions.length; i++) {
            selectedAllergensBooleans[i] = selectedAllergens.contains(allergenOptions[i]);
        }

        // Create the dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Allergens")
                .setMultiChoiceItems(allergenOptions, selectedAllergensBooleans, (dialog, which, isChecked) -> {
                    // Handle checkbox changes
                    if (isChecked) {
                        selectedAllergens.add(allergenOptions[which]); // Add allergen if checked
                    } else {
                        selectedAllergens.remove(allergenOptions[which]); // Remove allergen if unchecked
                    }
                })
                .setPositiveButton("OK", (dialog, which) -> {
                    // Update the filter with the selected allergens
                    updateAllergenFilter(selectedAllergens);
                    filterMenus(); // Apply the filter
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // Handle the cancel action (no change to the filter)
                });

        // Create and show the dialog
        builder.create().show();
    }


}


