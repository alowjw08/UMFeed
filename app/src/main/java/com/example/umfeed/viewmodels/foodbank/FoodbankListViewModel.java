package com.example.umfeed.viewmodels.foodbank;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.umfeed.models.foodbank.FoodBank;
import com.example.umfeed.models.foodbank.FoodBankService;

import java.util.List;

public class FoodbankListViewModel extends ViewModel {

    private final MutableLiveData<List<FoodBank>> foodBankList;
    private final FoodBankService foodBankService;

    public FoodbankListViewModel() {
        foodBankList = new MutableLiveData<>();
        foodBankService = new FoodBankService();
        loadFoodBanks();
    }

    public LiveData<List<FoodBank>> getFoodBankList() {
        return foodBankList;
    }

    private void loadFoodBanks() {
        foodBankService.fetchFoodBanks(new FoodBankService.FoodBankDataCallback() {
            @Override
            public void onFoodBanksFetched(List<FoodBank> foodBanks) {
                // Once the data is fetched, update the LiveData
                foodBankList.setValue(foodBanks);
            }
        });
    }
}
