package com.example.umfeed.views.donation;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.umfeed.R;
import com.example.umfeed.adapters.DonationAdapter;
import com.example.umfeed.viewmodels.donation.DonationViewModel;
import com.example.umfeed.views.pin.PinVerificationDialogFragment;

import java.util.Calendar;
import java.util.Date;

public class DonationFragment extends Fragment {

    private DonationViewModel viewModel;

    private EditText foodNameEditText;
    private TextView dateTextView;
    Calendar calendar = Calendar.getInstance();
    private Spinner categorySpinner, vegetarianSpinner, locationSpinner;
    private NumberPicker quantityPicker;
    private Button submitButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_donation, container, false);

        // Initialize the ViewModel
        viewModel = new ViewModelProvider(this).get(DonationViewModel.class);

        // Bind views
        foodNameEditText = root.findViewById(R.id.foodNameEditText);
        categorySpinner = root.findViewById(R.id.categorySpinner);
        vegetarianSpinner = root.findViewById(R.id.vegetarianSpinner);
        dateTextView = root.findViewById(R.id.dateTextView);
        locationSpinner = root.findViewById(R.id.locationSpinner);
        quantityPicker = root.findViewById(R.id.quantityPicker);
        submitButton = root.findViewById(R.id.submitButton);

        // Setting up adapter for spinner
        DonationAdapter donationAdapter = new DonationAdapter(requireContext(), root);
        donationAdapter.setupCategorySpinner();
        donationAdapter.setupVegetarianSpinner();
        donationAdapter.setupLocationSpinner();

        quantityPicker.setMinValue(1);
        quantityPicker.setMaxValue(100);
        quantityPicker.setWrapSelectorWheel(false);
        quantityPicker.setVisibility(View.VISIBLE);
        dateTextView.setOnClickListener(v -> {
            // Get the current date
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Create and show the DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                    // Set the selected date in the TextView
                    String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    dateTextView.setText(selectedDate);
                }
            }, year, month, day);
            datePickerDialog.show();
        });
        // Setup submit button click listener
        submitButton.setOnClickListener(v -> submitDonation());

        return root;
    }

    // Handle submit button click
    private void submitDonation() {
        // Retrieve data from the views
        String foodName = foodNameEditText.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString();
        boolean vegetarian = vegetarianSpinner.getSelectedItem().toString().equals("Yes");
        Date date = calendar.getTime();
        com.google.firebase.Timestamp expiryDate = new com.google.firebase.Timestamp(date);
        String location = locationSpinner.getSelectedItem().toString();
        int quantity = quantityPicker.getValue();

        // Call the ViewModel to handle the data
        if (!validateInput(foodName, category, vegetarian, quantity, expiryDate, location)) {
            viewModel.submitDonation(foodName, category, vegetarian, quantity, expiryDate, location);
            Log.d("isValid", "Validated");
        } else {
            // Handle validation error (e.g., show a Toast)
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            Log.d("isValid", "Not Valid");
        }
        PinVerificationDialogFragment pinVerificationDialog = PinVerificationDialogFragment.newInstance();
        pinVerificationDialog.show(getChildFragmentManager(), "PinVerificationDialogFragment");
    }

    // Input validation
    private boolean validateInput(String foodName, String category, Boolean vegetarian, int quantity, com.google.firebase.Timestamp expiryDate, String location) {
        return !(foodName == null || foodName.isEmpty() &&
                category == null || category.isEmpty() &&
                expiryDate == null || vegetarian == null &&
                location == null || location.isEmpty()) &&
                quantity <= 0;
    }

    public void setupCategorySpinner() {
        String[] categories = getResources().getStringArray(R.array.categories);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categories);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
    }
    private void setupVegetarianSpinner() {
        String[] vegetarian = getResources().getStringArray(R.array.Boolean);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, vegetarian);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vegetarianSpinner.setAdapter(adapter);
    }
    private void setupLocationSpinner() {
        String[] location = getResources().getStringArray(R.array.foodBanks);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, location);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(adapter);
    }
    private void setupSpinners() {
        AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int spinnerId = parent.getId();
                String selectedItem = parent.getItemAtPosition(position).toString();

                if (spinnerId == R.id.categorySpinner) {
                    // Handle category selection
                    Toast.makeText(requireContext(), "Category selected: " + selectedItem, Toast.LENGTH_SHORT).show();
                } else if (spinnerId == R.id.vegetarianSpinner) {
                    // Handle vegetarian selection
                    boolean isVegetarian = selectedItem.equals("Yes");
                    Toast.makeText(requireContext(), "Vegetarian: " + (isVegetarian ? "Yes" : "No"), Toast.LENGTH_SHORT).show();
                } else if (spinnerId == R.id.locationSpinner) {
                    // Handle location selection
                    Toast.makeText(requireContext(), "Location selected: " + selectedItem, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Optional: Handle no selection
            }
        };

    }
}
