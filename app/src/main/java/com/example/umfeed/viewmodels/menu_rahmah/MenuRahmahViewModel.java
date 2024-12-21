package com.example.umfeed.viewmodels.menu_rahmah;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.umfeed.models.menu_rahmah.MenuRahmah;
import com.example.umfeed.repositories.MenuRahmahRepository;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MenuRahmahViewModel extends ViewModel {
    private final MenuRahmahRepository repository;
    private final MutableLiveData<List<MenuRahmah>> menuList;
    private final MutableLiveData<Boolean> isHalalFilter;
    private final MutableLiveData<Boolean> isVegetarianFilter;
    private final MediatorLiveData<List<MenuRahmah>> filteredMenuList;

    private final MutableLiveData<MenuRahmah> selectedMenu = new MutableLiveData<>();

    private final MutableLiveData<Boolean> loadingState; // Track loading progress
    private final MutableLiveData<Boolean> emptyState;   // Track empty state
    private final MutableLiveData<String> errorState;    // Track error state

    public MenuRahmahViewModel() {
        repository = new MenuRahmahRepository();
        menuList = new MutableLiveData<>();
        isHalalFilter = new MutableLiveData<>(false);
        isVegetarianFilter = new MutableLiveData<>(false);
        filteredMenuList = new MediatorLiveData<>();
        loadingState = new MutableLiveData<>();
        emptyState = new MutableLiveData<>();
        errorState = new MutableLiveData<>();

        // Combine sources to dynamically update filteredMenuList
        filteredMenuList.addSource(menuList, this::applyFilters);
        filteredMenuList.addSource(isHalalFilter, value -> applyFilters(menuList.getValue()));
        filteredMenuList.addSource(isVegetarianFilter, value -> applyFilters(menuList.getValue()));

        loadMenuList(); // Initial load
    }

    public LiveData<MenuRahmah> getMenuRahmahById(String menuId) {
        loadingState.setValue(true);
        repository.getMenuById(menuId)
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        MenuRahmah menu = documentSnapshot.toObject(MenuRahmah.class);
                        selectedMenu.setValue(menu); // Update the selected menu LiveData
                    } else {
                        selectedMenu.setValue(null); // No menu found
                    }
                })
                .addOnFailureListener(e -> {
                    selectedMenu.setValue(null); // Set null on failure
                    errorState.setValue("Failed to fetch menu: " + e.getMessage());
                    e.printStackTrace();
                })
                .addOnCompleteListener(task -> loadingState.setValue(false));
        return selectedMenu; // Return LiveData<MenuRahmah>
    }


    // Fetches the list of menus from the repository
    public void loadMenuList() {
        loadingState.setValue(true);
        repository.getMenuList()
                .addOnSuccessListener(querySnapshot -> {
                    List<MenuRahmah> menus = new ArrayList<>();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                            MenuRahmah menu = documentSnapshot.toObject(MenuRahmah.class);
                            if (menu != null) menus.add(menu);
                        }
                    }
                    menuList.setValue(menus);
                    emptyState.setValue(menus.isEmpty());
                })
                .addOnFailureListener(e -> {
                    menuList.setValue(new ArrayList<>());
                    emptyState.setValue(true);
                    errorState.setValue("Failed to fetch menu list: " + e.getMessage());
                })
                .addOnCompleteListener(task -> loadingState.setValue(false));
    }

    // Applies filters to the menu list based on current filter states
    private void applyFilters(List<MenuRahmah> menus) {
        if (menus == null) return;

        boolean halalFilter = isHalalFilter.getValue() != null && isHalalFilter.getValue();
        boolean vegetarianFilter = isVegetarianFilter.getValue() != null && isVegetarianFilter.getValue();

        List<MenuRahmah> filtered = new ArrayList<>();
        for (MenuRahmah menu : menus) {
            boolean matchesHalal = !halalFilter || menu.getHalalStatus();
            boolean matchesVegetarian = !vegetarianFilter || menu.getVegetarianStatus();

            if (matchesHalal && matchesVegetarian) {
                filtered.add(menu);
            }
        }

        filteredMenuList.setValue(filtered);
        emptyState.setValue(filtered.isEmpty());
    }

    public void updateFilters(boolean halal, boolean vegetarian, List<String> allergens) {
        // Update the Halal and Vegetarian filters
        isHalalFilter.setValue(halal);
        isVegetarianFilter.setValue(vegetarian);

        // Apply allergen filter if any allergens are selected
        List<MenuRahmah> filteredList = menuList.getValue() != null ? new ArrayList<>(menuList.getValue()) : new ArrayList<>();

        // Apply allergen filter if allergens are provided
        if (allergens != null && !allergens.isEmpty()) {
            filteredList = filterByAllergens(filteredList, allergens);
        }

        // Apply Halal and Vegetarian filters on the existing filtered list
        filteredList = filterByHalal(filteredList, halal);
        filteredList = filterByVegetarian(filteredList, vegetarian);

        // Update the filtered menu list LiveData
        filteredMenuList.setValue(filteredList);

        // Update empty state
        emptyState.setValue(filteredList.isEmpty());
    }

    // Filter method for Halal
    private List<MenuRahmah> filterByHalal(List<MenuRahmah> menuList, boolean halalChecked) {
        List<MenuRahmah> filteredList = new ArrayList<>();
        for (MenuRahmah menu : menuList) {
            if (!halalChecked || menu.getHalalStatus()) {
                filteredList.add(menu);
            }
        }
        return filteredList;
    }

    // Filter method for Vegetarian
    private List<MenuRahmah> filterByVegetarian(List<MenuRahmah> menuList, boolean vegetarianChecked) {
        List<MenuRahmah> filteredList = new ArrayList<>();
        for (MenuRahmah menu : menuList) {
            if (!vegetarianChecked || menu.getVegetarianStatus()) {
                filteredList.add(menu);
            }
        }
        return filteredList;
    }

    private List<MenuRahmah> filterByAllergens(List<MenuRahmah> menuList, List<String> allergens) {
        List<MenuRahmah> filteredList = new ArrayList<>();
        for (MenuRahmah menu : menuList) {
            // If no allergens are selected, no filtering needs to be done
            if (allergens == null || allergens.isEmpty()) {
                filteredList.add(menu);
                continue;
            }

            boolean containsAllergen = false;

            // Loop through allergens and check if the menu contains any of the selected allergens
            for (String allergen : allergens) {
                if (menu.getAllergens() != null && menu.getAllergens().contains(allergen)) {
                    containsAllergen = true;
                    break;  // No need to check further if an allergen is found
                }
            }

            // Only add the menu if it does not contain any of the selected allergens
            if (!containsAllergen) {
                filteredList.add(menu);
            }
        }
        return filteredList;
    }


    // Getters for LiveData
    public LiveData<List<MenuRahmah>> getFilteredMenuList() {
        return filteredMenuList;
    }

    public LiveData<Boolean> getLoadingState() {
        return loadingState;
    }

    public LiveData<Boolean> getEmptyState() {
        return emptyState;
    }

    public LiveData<String> getErrorState() {
        return errorState;
    }
}





