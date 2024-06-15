package com.example.trainbites.CustomerFoodPanel;

public class CartItemModel {
    private String dishName;
    private String price;
    private String imageUrl;
    private String key;
    private int quantity;

    // Default constructor required for Firebase
    public CartItemModel() {
        // Initialize quantity with 1
        this.quantity = 1;
    }

    // Constructor with descriptive parameter names
    public CartItemModel(String dishName, String price, String imageUrl, String key) {
        this.dishName = dishName;
        this.price = price;
        this.imageUrl = imageUrl;
        this.key = key;
        // Initialize quantity with 1
        this.quantity = 1;
    }

    // Getters and Setters for all fields

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public double getPriceAsDouble() {
        try {
            return Double.parseDouble(price);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
