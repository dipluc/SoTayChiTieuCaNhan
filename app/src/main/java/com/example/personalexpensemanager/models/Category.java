package com.example.personalexpensemanager.models;

public class Category {
    private int catId;
    private String catName;
    private int catType; // 0 = Chi, 1 = Thu
    private String iconName;

    public Category(int catId, String catName, int catType, String iconName) {
        this.catId = catId;
        this.catName = catName;
        this.catType = catType;
        this.iconName = iconName;
    }

    public int getCatId() { return catId; }
    public void setCatId(int catId) { this.catId = catId; }
    public String getCatName() { return catName; }
    public void setCatName(String catName) { this.catName = catName; }
    public int getCatType() { return catType; }
    public void setCatType(int catType) { this.catType = catType; }
    public String getIconName() { return iconName; }
    public void setIconName(String iconName) { this.iconName = iconName; }

    @Override
    public String toString() {
        return catName; // Useful for Spinner adapter default display
    }
}