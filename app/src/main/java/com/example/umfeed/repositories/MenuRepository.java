package com.example.umfeed.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.example.umfeed.models.menu.MenuRahmah;
import java.util.List;
import java.util.ArrayList;

public class MenuRepository {
    private final FirebaseFirestore db;

    public MenuRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public LiveData<List<MenuRahmah>> getFeaturedMenus() {
        MutableLiveData<List<MenuRahmah>> menusLiveData = new MutableLiveData<>();

        // Query menu collection ordered by price
        db.collection("menuRahmah")
                .orderBy("price", Query.Direction.ASCENDING)
                .limit(3)  // Limit to 3 featured items
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        return;
                    }

                    List<MenuRahmah> menuList = new ArrayList<>();

                    if (snapshots != null && !snapshots.isEmpty()) {
                        for (DocumentSnapshot document : snapshots) {
                            MenuRahmah menu = document.toObject(MenuRahmah.class);
                            if (menu != null) {
                                menuList.add(menu);
                            }
                        }
                    }

                    menusLiveData.setValue(menuList);
                });

        return menusLiveData;
    }
}