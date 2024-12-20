package com.example.umfeed.repositories;

import static com.example.umfeed.views.pin.DialogSuccessFragment.TAG;

import android.util.Log;

import com.example.umfeed.models.recipe.Recipe;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.tasks.Task;
import com.example.umfeed.models.menu_rahmah.MenuRahmah;

import java.util.ArrayList;
import java.util.List;

public class MenuRahmahRepository {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Fetch all menu items and return them as a list of MenuRahmah objects
    public Task<QuerySnapshot> getMenuList() {
        return db.collection("menu").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                // For debugging
                Log.d(TAG, "Document Data: " + document.getData());

                MenuRahmah menuRahmah = document.toObject(MenuRahmah.class);
                menuRahmah.setId(document.getId()); // Set ID immediately when converting to object
                // Update the document in the snapshot
                document.getReference().set(menuRahmah);
            }
        });
    }

    public Task<MenuRahmah> getMenuDetail(String menuId) {
        return db.collection("menu")
                .document(menuId)
                .get()
                .continueWith(task -> task.getResult().toObject(MenuRahmah.class)); // Convert document to MenuRahmah object
    }

    public Task<DocumentSnapshot> getMenuById(String menuId) {
        return FirebaseFirestore.getInstance()
                .collection("menu")
                .document(menuId)
                .get();
    }

}