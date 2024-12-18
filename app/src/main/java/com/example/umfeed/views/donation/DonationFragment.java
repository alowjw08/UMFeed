package com.example.umfeed.views.donation;

import android.app.DatePickerDialog;
import android.graphics.Color;
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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.umfeed.R;
import com.example.umfeed.adapters.DonationAdapter;
import com.example.umfeed.viewmodels.donation.DonationViewModel;
import com.example.umfeed.views.pin.PinVerificationDialogFragment;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;

public class DonationFragment extends Fragment {

    private DonationViewModel viewModel;
    private EditText foodNameEditText;
    private TextView dateTextView;
    Calendar calendar = null;
    private Spinner categorySpinner, vegetarianSpinner, locationSpinner;
    private NumberPicker quantityPicker;
    private Button submitButton;
    private String foodBankId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_donation, container, false);

        // Initialize the ViewModel
        viewModel = new ViewModelProvider(this).get(DonationViewModel.class);

        // Bind views
//        foodNameEditText = root.findViewById(R.id.foodNameEditText);
        categorySpinner = root.findViewById(R.id.categorySpinner);
//        vegetarianSpinner = root.findViewById(R.id.vegetarianSpinner);
//        dateTextView = root.findViewById(R.id.dateTextView);
        locationSpinner = root.findViewById(R.id.locationSpinner);
        quantityPicker = root.findViewById(R.id.quantityPicker);
        submitButton = root.findViewById(R.id.submitButton);

        // Setting up adapter for spinner
        DonationAdapter donationAdapter = new DonationAdapter(requireContext(), root);
        donationAdapter.setupCategorySpinner();
//        donationAdapter.setupVegetarianSpinner();
        donationAdapter.setupLocationSpinner();

        quantityPicker.setMinValue(1);
        quantityPicker.setMaxValue(100);
        quantityPicker.setWrapSelectorWheel(false);
        quantityPicker.setVisibility(View.VISIBLE);
//        dateTextView.setOnClickListener(v -> {
//            // Get the current date
//            calendar = Calendar.getInstance();
//            int year = calendar.get(Calendar.YEAR);
//            int month = calendar.get(Calendar.MONTH);
//            int day = calendar.get(Calendar.DAY_OF_MONTH);
//
//            // Create and show the DatePickerDialog
//            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
//                @Override
//                public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
//                    // Set the selected date in the TextView
//                    calendar.set(selectedYear, selectedMonth, selectedDay);
//                    // Set to midnight
//                    calendar.set(Calendar.HOUR_OF_DAY, 0);
//                    calendar.set(Calendar.MINUTE, 0);
//                    calendar.set(Calendar.SECOND, 0);
//                    calendar.set(Calendar.MILLISECOND, 0);
//                    String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
//                    dateTextView.setText(selectedDate);
//                    dateTextView.setTextColor(Color.BLACK);
//                }
//            }, year, month, day);
//            datePickerDialog.show();
//        });

        // Setup submit button click listener
        submitButton.setOnClickListener(v -> submitDonation());


        String foodBankName = null;
        // Retrieve the foodBankId from the arguments
        foodBankName = getArguments() != null ? getArguments().getString("foodBankName") : null;

        if (foodBankName != null && locationSpinner != null) {
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) locationSpinner.getAdapter();
            Log.d("locationSpinner", "setAdapter");
            if (adapter != null) {
                Log.d("locationSpinner", "setFixed");
                int position = adapter.getPosition(foodBankName); // Find the position of foodBankId in the spinner's data
                if (position >= 0) {
                    locationSpinner.setSelection(position); // Set the selected item
                    locationSpinner.setEnabled(false);
                }
            }
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

// Retrieve the foodBankId based on the foodBankName selected in the locationSpinner
        String selectedFoodBankName = locationSpinner.getSelectedItem().toString();

// Query the Firestore collection to get the foodBankId
        db.collection("foodBanks")
                .whereEqualTo("name", selectedFoodBankName)  // Assuming 'name' is the field in Firestore
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Retrieve the first document from the query result (assuming foodBankName is unique)
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        String foodBankId = document.getId();

                        // Now you can use the foodBankId as needed
                        Log.d("FoodBankId", "FoodBankId for " + selectedFoodBankName + ": " + foodBankId);

                        this.foodBankId = foodBankId;

                    } else {
                        Log.d("FoodBankId", "No matching food bank found.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseError", "Error getting food bank ID", e);
                });


        return root;
    }

        // Handle submit button click
    private void submitDonation() {
        // Retrieve data from the views
//        String foodName = foodNameEditText.getText().toString().trim();
        String category = categorySpinner.getSelectedItem() != null ? categorySpinner.getSelectedItem().toString() : null;
        String location = locationSpinner.getSelectedItem() != null ? locationSpinner.getSelectedItem().toString() : null;

//        Boolean vegetarian = null;
//        if (vegetarianSpinner.getSelectedItem() != null) {
//            String vegetarianString = vegetarianSpinner.getSelectedItem().toString();
//            vegetarian = "Yes".equalsIgnoreCase(vegetarianString);
//        }

        Date date = null;
        Timestamp expiryDate = null;
        if (calendar != null) {
            date = calendar.getTime();
            expiryDate = new Timestamp(date);
        }
        int quantity = quantityPicker.getValue();

        // Call the ViewModel to handle the data
        if (validateInput(category, quantity, location)) {
            PinVerificationDialogFragment pinVerificationDialog = PinVerificationDialogFragment.newInstance();
            // Create a Bundle and add the foodBankId
            Bundle args = new Bundle();
            args.putString("foodBankId", foodBankId); // Pass the foodBankId
//            args.putString("foodName", foodName);
            args.putString("category", category);
            args.putString("location", location);
//            args.putBoolean("vegetarian", vegetarian);
            args.putInt("quantity", quantity);

            pinVerificationDialog.setArguments(args);
            pinVerificationDialog.show(getChildFragmentManager(), "PinVerificationDialogFragment");
            Log.d("isValid", "Validated");
        } else {
            // Handle validation error (e.g., show a Toast)
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            Log.d("isValid", "Not Valid");
        }
    }

    // Input validation
    private boolean validateInput(String category, int quantity, String location) {
        return !(category == null ||
                location == null || quantity <= 0);

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
                }
//                else if (spinnerId == R.id.vegetarianSpinner) {
//                    // Handle vegetarian selection
//                    boolean isVegetarian = selectedItem.equals("Yes");
//                    Toast.makeText(requireContext(), "Vegetarian: " + (isVegetarian ? "Yes" : "No"), Toast.LENGTH_SHORT).show();
//                }
                else if (spinnerId == R.id.locationSpinner) {
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
