package com.example.personalexpensemanager.models;

public class Transaction {
    private int transId;
    private long amount;
    private int catId;
    private String transDate;
    private String note;
    private String imageUri;
    private int userId;

    // Additional display fields joined from Categories
    private String catName;
    private String iconName;
    private int catType; // 0 = Chi, 1 = Thu

    public Transaction(int transId, long amount, int catId, String transDate, String note, String imageUri, int userId) {
        this.transId = transId;
        this.amount = amount;
        this.catId = catId;
        this.transDate = transDate;
        this.note = note;
        this.imageUri = imageUri;
        this.userId = userId;
    }

    public int getTransId() { return transId; }
    public void setTransId(int transId) { this.transId = transId; }
    public long getAmount() { return amount; }
    public void setAmount(long amount) { this.amount = amount; }
    public int getCatId() { return catId; }
    public void setCatId(int catId) { this.catId = catId; }
    public String getTransDate() { return transDate; }
    public void setTransDate(String transDate) { this.transDate = transDate; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public String getImageUri() { return imageUri; }
    public void setImageUri(String imageUri) { this.imageUri = imageUri; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getCatName() { return catName; }
    public void setCatName(String catName) { this.catName = catName; }
    public String getIconName() { return iconName; }
    public void setIconName(String iconName) { this.iconName = iconName; }
    public int getCatType() { return catType; }
    public void setCatType(int catType) { this.catType = catType; }

    public void setDisplayInfo(String catName, String iconName, int catType) {
        this.catName = catName;
        this.iconName = iconName;
        this.catType = catType;
    }
}