package com.example.umfeed.viewmodels.foodbank;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.umfeed.models.foodbank.FoodBank;
import com.example.umfeed.models.foodbank.FoodBankInventoryItem;
import com.example.umfeed.repositories.FoodBankRepository;

import java.util.List;

public class FoodbankDetailViewModel extends ViewModel {

    private final FoodBankRepository repository;
    private final MutableLiveData<FoodBank> selectedFoodBank;
    private final MutableLiveData<List<FoodBankInventoryItem>> foodBankInventory;
    private final MutableLiveData<Boolean> isLoading;
    private final MutableLiveData<String> errorMessage;

    public FoodbankDetailViewModel() {
        repository = new FoodBankRepository();
        selectedFoodBank = new MutableLiveData<>(); //Holds the details of the selected foodbank (name and image).
        foodBankInventory = new MutableLiveData<>(); //Holds the list of foodbank inventory items.
        isLoading = new MutableLiveData<>(false);
        errorMessage = new MutableLiveData<>();
    }

    public LiveData<FoodBank> getSelectedFoodBank() {
        return selectedFoodBank;
    }

    public LiveData<List<FoodBankInventoryItem>> getFoodBankInventory() {
        return foodBankInventory;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    // Set the selected foodbank
    public void setSelectedFoodBank(FoodBank foodBank) {
        selectedFoodBank.setValue(foodBank);
    }

    // Load the inventory of the selected foodbank
    public void loadFoodBankInventory(String foodBankId) {
        isLoading.setValue(true); // Show loading state

        repository.getFoodBankInventory(
                foodBankId,
                inventoryItems -> {
                    isLoading.setValue(false); // Hide loading state
                    foodBankInventory.setValue(inventoryItems); // Update inventory
                },
                error -> {
                    isLoading.setValue(false); // Hide loading state
                    errorMessage.setValue("Failed to load inventory: " + error.getMessage());
                }
        );
    }
}
