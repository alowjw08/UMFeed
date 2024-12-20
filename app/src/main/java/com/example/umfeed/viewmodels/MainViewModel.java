package com.example.umfeed.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.umfeed.repositories.MenuRahmahRepository;
import com.example.umfeed.repositories.UserRepository;
import com.example.umfeed.models.user.User;

import com.google.firebase.firestore.QuerySnapshot;

public class MainViewModel extends ViewModel {
    private final LiveData<User> currentUser;
    private final MenuRahmahRepository menuListRepository;
    private final MutableLiveData<QuerySnapshot> MenuRahmahList = new MutableLiveData<QuerySnapshot>();
    private final MutableLiveData<Boolean> loadingState = new MutableLiveData<>();
    private final MutableLiveData<Boolean> emptyState = new MutableLiveData<>();
    private final MutableLiveData<String> errorState = new MutableLiveData<>();

    public MainViewModel(UserRepository userRepository, MenuRahmahRepository menuListRepository) {
        this.currentUser = userRepository.getCurrentUser();
        this.menuListRepository = menuListRepository;
        loadMenuList();
    }

    public LiveData<User> getCurrentUser() {
        return currentUser;
    }

    // Fetch the list of menu items from the repository
    private void loadMenuList() {
        loadingState.setValue(true); // Indicate that the loading process has started
        menuListRepository.getMenuList()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot menus = task.getResult(); // Get the result of the task
                        MenuRahmahList.setValue(menus); // Set the value of LiveData with the fetched list
                        emptyState.setValue(menus.isEmpty()); // Set empty state if no menus are fetched
                    } else {
                        MenuRahmahList.setValue(null); // Set LiveData to null if fetching fails
                        emptyState.setValue(true); // Indicate empty state
                        errorState.setValue("Failed to fetch menu list: " + task.getException().getMessage()); // Set error message
                    }
                    loadingState.setValue(false); // Indicate loading finished
                });
    }

    // Returns the LiveData for the menu list
    public LiveData<QuerySnapshot> getMenuList() {
        return MenuRahmahList;
    }

    // Returns the LiveData for the loading state
    public LiveData<Boolean> getLoadingState() {
        return loadingState;
    }

    // Returns the LiveData for the empty state
    public LiveData<Boolean> getEmptyState() {
        return emptyState;
    }

    // Returns the LiveData for error state
    public LiveData<String> getErrorState() {
        return errorState;
    }
}


