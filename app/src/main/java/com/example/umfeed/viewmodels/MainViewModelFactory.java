package com.example.umfeed.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.umfeed.repositories.MenuRahmahRepository;
import com.example.umfeed.repositories.UserRepository;

public class MainViewModelFactory implements ViewModelProvider.Factory {
    private final UserRepository userRepository;
    private final MenuRahmahRepository menuRahmahRepository;

    public MainViewModelFactory(UserRepository userRepository, MenuRahmahRepository menuRahmahRepository) {
        this.userRepository = userRepository;
        this.menuRahmahRepository = menuRahmahRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainViewModel.class)) {
            return (T) new MainViewModel(userRepository, menuRahmahRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}