package com.example.umfeed.adapters;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.umfeed.R;

public class DonationAdapter {

    private Context context;
    private View view;

    public DonationAdapter(Context context, View view) {
        this.context = context;
        this.view = view;
    }

    public void setupCategorySpinner() {
        Spinner categorySpinner = view.findViewById(R.id.categorySpinner);
        String[] categories = context.getResources().getStringArray(R.array.categories);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        NothingSelectedSpinnerAdapter nothingSelectedAdapter = new NothingSelectedSpinnerAdapter(adapter, R.layout.spinner_nothing_selected, context);
        categorySpinner.setAdapter(nothingSelectedAdapter);
    }

//    public void setupVegetarianSpinner() {
//        Spinner vegetarianSpinner = view.findViewById(R.id.vegetarianSpinner);
//        String[] vegetarian = context.getResources().getStringArray(R.array.Boolean);
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, vegetarian);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        NothingSelectedSpinnerAdapter nothingSelectedAdapter = new NothingSelectedSpinnerAdapter(adapter, R.layout.spinner_nothing_selected, context);
//        vegetarianSpinner.setAdapter(nothingSelectedAdapter);
//    }

    public void setupLocationSpinner() {
        Spinner locationSpinner = view.findViewById(R.id.locationSpinner);
        String[] location = context.getResources().getStringArray(R.array.foodBanks);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, location);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        NothingSelectedSpinnerAdapter nothingSelectedAdapter = new NothingSelectedSpinnerAdapter(adapter, R.layout.spinner_nothing_selected, context);
        locationSpinner.setAdapter(adapter);
    }
}