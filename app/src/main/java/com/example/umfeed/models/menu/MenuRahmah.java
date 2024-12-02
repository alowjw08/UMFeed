package com.example.umfeed.models.menu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import java.util.List;
import java.util.Objects;

public class MenuRahmah {
    private String id;          // For navigation/reference
    private String name;
    private String imageUrl;
    private boolean isVegetarian;
    private boolean isHalal;
    private List<String> allergens;
    private double price;
    private StallInfo stall;

    // Default constructor needed for Firestore
    public MenuRahmah() {}

    public MenuRahmah(String id, String name, String imageUrl, boolean isVegetarian,
                      boolean isHalal, List<String> allergens,
                      double price, StallInfo stall) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.isVegetarian = isVegetarian;
        this.isHalal = isHalal;
        this.allergens = allergens;
        this.price = price;
        this.stall = stall;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MenuRahmah that = (MenuRahmah) o;
        return isVegetarian == that.isVegetarian &&
                isHalal == that.isHalal &&
                Double.compare(that.price, price) == 0 &&
                Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(imageUrl, that.imageUrl) &&
                Objects.equals(allergens, that.allergens) &&
                Objects.equals(stall, that.stall);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, imageUrl, isVegetarian, isHalal,
                allergens, price, stall);
    }
    public static final DiffUtil.ItemCallback<MenuRahmah> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<MenuRahmah>() {
                @Override
                public boolean areItemsTheSame(@NonNull MenuRahmah oldItem, @NonNull MenuRahmah newItem) {
                    return oldItem.getId().equals(newItem.getId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull MenuRahmah oldItem, @NonNull MenuRahmah newItem) {
                    return oldItem.getName().equals(newItem.getName()) &&
                            oldItem.isVegetarian() == newItem.isVegetarian() &&
                            oldItem.isHalal() == newItem.isHalal() &&
                            oldItem.getAllergens().equals(newItem.getAllergens()) &&
                            oldItem.getPrice() == newItem.getPrice() &&
                            oldItem.getStall().equals(newItem.getStall());
                }
            };

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isVegetarian() {
        return isVegetarian;
    }

    public void setVegetarian(boolean vegetarian) {
        isVegetarian = vegetarian;
    }

    public boolean isHalal() {
        return isHalal;
    }

    public void setHalal(boolean halal) {
        isHalal = halal;
    }

    public List<String> getAllergens() {
        return allergens;
    }

    public void setAllergens(List<String> allergens) {
        this.allergens = allergens;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public StallInfo getStall() {
        return stall;
    }

    public void setStall(StallInfo stall) {
        this.stall = stall;
    }
}