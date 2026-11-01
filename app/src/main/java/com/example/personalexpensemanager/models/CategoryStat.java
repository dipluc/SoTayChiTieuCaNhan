package com.example.personalexpensemanager.models;

public class CategoryStat {
    private String catName;
    private String iconName;
    private long total;
    private double percent;

    public CategoryStat(String catName, String iconName, long total, double percent) {
        this.catName = catName;
        this.iconName = iconName;
        this.total = total;
        this.percent = percent;
    }

    public String getCatName() { return catName; }
    public String getIconName() { return iconName; }
    public long getTotal() { return total; }
    public double getPercent() { return percent; }
}