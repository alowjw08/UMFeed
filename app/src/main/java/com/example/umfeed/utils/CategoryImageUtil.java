package com.example.umfeed.utils;

import com.example.umfeed.R;
public class CategoryImageUtil {
    public static int getImageResourceByCategory(String category) {
        switch (category) {
            case "Biscuits and Snacks":
                return R.drawable.biscuits_and_snacks;
            case "Canned and Preserved Foods":
                return R.drawable.canned_foods;
            case "Beverages":
                return R.drawable.beverages;
            case "Rice and Grains":
                return R.drawable.rice_and_grain;
            case "Dehydrated Foods":
                return R.drawable.dehydrated_food;
            case "Nuts and Seeds":
                return R.drawable.nuts_and_seeds;
            case "Proteins":
                return R.drawable.protein;
            case "Condiments and Seasonings":
                return R.drawable.condiments_and_seasonings;
            case "Powdered Food":
                return R.drawable.powdered_food;
            case "Pasta and Noodles":
                return R.drawable.pasta_and_nooodles;
            default:
                return R.drawable.food_placeholder; // Default image
        }
    }
}
