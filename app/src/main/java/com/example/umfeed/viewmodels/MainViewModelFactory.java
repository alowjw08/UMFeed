package com.example.umfeed.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.umfeed.repositories.MenuRepository;
import com.example.umfeed.repositories.UserRepository;

public class MainViewModelFactory implements ViewModelProvider.Factory {
    private final UserRepository userRepository;
    private final MenuRepository menuRepository;

    public MainViewModelFactory(UserRepository userRepository, MenuRepository menuRepository) {
        this.userRepository = userRepository;
        this.menuRepository = menuRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainViewModel.class)) {
            return (T) new MainViewModel(userRepository, menuRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}