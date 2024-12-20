package com.example.umfeed.views.menu_rahmah;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.umfeed.R;
import com.example.umfeed.models.menu_rahmah.MenuRahmah;
import com.example.umfeed.repositories.MenuRahmahRepository;
import com.example.umfeed.viewmodels.menu_rahmah.MenuRahmahViewModel;

public class MenuRahmahDetailFragment extends Fragment {

    private ImageView menuImageView;
    private TextView menuNameTextView, restaurantNameTextView, vegetarianStatusTextView, halalStatusTextView, allergensTextView, addressTextView, contactNumberTextView;
    private Button websiteButton, socialMediaButton;
    private MenuRahmahViewModel viewModel;
    private String menuId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu_rahmah_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI components
        initializeUIComponents(view);

        // Get menuId from arguments
        if (getArguments() != null) {
            menuId = getArguments().getString("menu_id");
        }

        // Initialize ViewModel and fetch menu details
        viewModel = new ViewModelProvider(this).get(MenuRahmahViewModel.class);
        if (menuId != null) {
            fetchMenuDetails(menuId);
        }
    }

    private void initializeUIComponents(View view) {
        menuImageView = view.findViewById(R.id.DIVMenu);
        menuNameTextView = view.findViewById(R.id.DTVMenuName);
        restaurantNameTextView = view.findViewById(R.id.DTVRestaurantName);
        vegetarianStatusTextView = view.findViewById(R.id.DTVVegetarianStatus);
        halalStatusTextView = view.findViewById(R.id.DTVHalalStatus);
        allergensTextView = view.findViewById(R.id.DTVAllergensStatement);
        addressTextView = view.findViewById(R.id.TVAddress);
        contactNumberTextView = view.findViewById(R.id.TVPhoneNumber);
        websiteButton = view.findViewById(R.id.BtnWebsite);
        socialMediaButton = view.findViewById(R.id.BtnSocialMedia);

        setupButtonListeners();
    }

    private void fetchMenuDetails(String menuId) {
        viewModel.getMenuRahmahById(menuId).observe(getViewLifecycleOwner(), this::populateMenuDetails);
    }

    private void populateMenuDetails(@Nullable MenuRahmah menuRahmah) {
        if (menuRahmah == null) {
            Toast.makeText(requireContext(), "Failed to load menu details", Toast.LENGTH_SHORT).show();
            return;
        }

        menuNameTextView.setText(menuRahmah.getMenuName());
        restaurantNameTextView.setText(menuRahmah.getRestaurantName());
        vegetarianStatusTextView.setText(menuRahmah.getVegetarianStatus() ? "Vegetarian" : "Non-Vegetarian");
        halalStatusTextView.setText(menuRahmah.getHalalStatus() ? "Halal" : "Non-Halal");
        allergensTextView.setText(menuRahmah.getAllergens() != null && !menuRahmah.getAllergens().isEmpty()
                ? "Allergens: " + String.join(", ", menuRahmah.getAllergens())
                : "No allergens identified.");
        contactNumberTextView.setText(menuRahmah.getContactNumber());
        addressTextView.setText(menuRahmah.getAddress());

        // Load the image using Glide with placeholder and error handling
        Glide.with(requireContext())
                .load(menuRahmah.getImageUrl())
                .placeholder(R.drawable.placeholder_recipe)
                .error(R.drawable.error_recipe)
                .into(menuImageView);

        websiteButton.setTag(menuRahmah.getWebsite());
        socialMediaButton.setTag(menuRahmah.getSocialMedia());
    }

    private void setupButtonListeners() {
        websiteButton.setOnClickListener(v -> openLink((String) websiteButton.getTag(), "Website link is not available"));
        socialMediaButton.setOnClickListener(v -> openLink((String) socialMediaButton.getTag(), "Social media link is not available"));
    }

    private void openLink(String url, String errorMessage) {
        if (url != null && url.startsWith("http")) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        } else {
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
        }
    }
}


