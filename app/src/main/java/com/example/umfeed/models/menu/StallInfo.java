package com.example.umfeed.models.menu;

import java.util.List;
import java.util.Objects;

public class StallInfo {
    private String name;
    private List<String> website;
    private String address;
    private String phone;

    public StallInfo() {}

    public StallInfo(String name, List<String> website,
                     String address, String phone) {
        this.name = name;
        this.website = website;
        this.address = address;
        this.phone = phone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StallInfo stallInfo = (StallInfo) o;
        return Objects.equals(name, stallInfo.name) &&
                Objects.equals(website, stallInfo.website) &&
                Objects.equals(address, stallInfo.address) &&
                Objects.equals(phone, stallInfo.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, website, address, phone);
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getWebsite() {
        return website;
    }

    public void setWebsite(List<String> website) {
        this.website = website;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}