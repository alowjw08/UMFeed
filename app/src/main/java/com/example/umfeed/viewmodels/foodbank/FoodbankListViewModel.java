package com.example.umfeed.viewmodels.foodbank;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.umfeed.models.foodbank.FoodBank;
import com.example.umfeed.R;

import java.util.ArrayList;
import java.util.List;

public class FoodbankListViewModel extends ViewModel {

    private final MutableLiveData<List<FoodBank>> foodBankList;

    public FoodbankListViewModel() {
        foodBankList = new MutableLiveData<>();
        loadFoodBanks();
    }

    public LiveData<List<FoodBank>> getFoodBankList() {
        return foodBankList;
    }

    private void loadFoodBanks() {
        List<FoodBank> initialList = new ArrayList<>();
        initialList.add(new FoodBank("KK1", R.drawable.kk_kk1));
        initialList.add(new FoodBank("KK2", R.drawable.kk_kk2));
        initialList.add(new FoodBank("KK3", R.drawable.kk_kk3));
        initialList.add(new FoodBank("KK4", R.drawable.kk_kk4));
        initialList.add(new FoodBank("KK5", R.drawable.kk_kk5));
        initialList.add(new FoodBank("KK6", R.drawable.kk_kk6));
        initialList.add(new FoodBank("KK7", R.drawable.kk_kk7));
        initialList.add(new FoodBank("KK8", R.drawable.kk_kk8));
        initialList.add(new FoodBank("KK9", R.drawable.kk_kk9));
        initialList.add(new FoodBank("KK10", R.drawable.kk_kk10));
        initialList.add(new FoodBank("KK11", R.drawable.kk_kk11));
        initialList.add(new FoodBank("KK12", R.drawable.kk_kk12));
        initialList.add(new FoodBank("KK13", R.drawable.kk_kk13));
        initialList.add(new FoodBank("International House", R.drawable.kk_international_house));

        foodBankList.setValue(initialList);
    }
}