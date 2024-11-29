package com.example.umfeed.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.umfeed.models.menu.MenuRahmah;
import com.example.umfeed.models.user.User;
import com.example.umfeed.repositories.MenuRepository;
import com.example.umfeed.repositories.UserRepository;

import java.util.List;

public class MainViewModel extends ViewModel {
    private final LiveData<User> currentUser;
    private final LiveData<List<MenuRahmah>> featuredMenus;

    public MainViewModel(UserRepository userRepository, MenuRepository menuRepository) {
        this.currentUser = userRepository.getCurrentUser();
        this.featuredMenus = menuRepository.getFeaturedMenus();
    }

    public LiveData<User> getCurrentUser() {
        return currentUser;
    }

    public LiveData<List<MenuRahmah>> getFeaturedMenus() {
        return featuredMenus;
    }
}