package com.example.umfeed.models.menu_rahmah;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.google.firebase.firestore.DocumentId;

import java.util.ArrayList;
import java.util.List;

public class MenuRahmah {

    @DocumentId
    private String id;  // Firestore Document ID
    private String menuName;
    private String restaurantName;
    private String address;
    private String website;
    private String socialMedia;
    private String contactNumber;
    private List<String> allergens;
    private boolean vegetarianStatus;
    private boolean halalStatus;
    private String imageUrl;

    // Default constructor required for Firestore deserialization
    public MenuRahmah() {}

    // Constructor with all fields
    public MenuRahmah(String menuName, String restaurantName, String address, String website, String socialMedia,
                      String contactNumber, List<String> allergens, boolean vegetarianStatus, boolean halalStatus, String imageUrl) {
        this.menuName = menuName;
        this.restaurantName = restaurantName;
        this.address = address;
        this.website = website;
        this.socialMedia = socialMedia;
        this.contactNumber = contactNumber;
        this.allergens = allergens;
        this.vegetarianStatus = vegetarianStatus;
        this.halalStatus = halalStatus;
        this.imageUrl = imageUrl;
    }

    // Getter and Setter for the fields
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getSocialMedia() {
        return socialMedia;
    }

    public void setSocialMedia(String socialMedia) {
        this.socialMedia = socialMedia;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public List<String> getAllergens() {
        return allergens;
    }

    public void setAllergens(List<String> allergens) {
        if (allergens == null) {
            this.allergens = new ArrayList<>();  // Default to an empty list if null
        } else {
            this.allergens = allergens;
        }
    }

    public boolean getVegetarianStatus() {
        return vegetarianStatus;
    }

    public void setVegetarianStatus(boolean vegetarianStatus) {
        this.vegetarianStatus = vegetarianStatus;
    }

    public boolean getHalalStatus() {
        return halalStatus;
    }

    public void setHalalStatus(boolean halalStatus) {
        this.halalStatus = halalStatus;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public static final DiffUtil.ItemCallback<MenuRahmah> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<MenuRahmah>() {
                @Override
                public boolean areItemsTheSame(@NonNull MenuRahmah oldItem, @NonNull MenuRahmah newItem) {
                    return oldItem.getId().equals(newItem.getId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull MenuRahmah oldItem, @NonNull MenuRahmah newItem) {
                    return oldItem.equals(newItem);
                }
            };
}