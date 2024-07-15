package com.example.trainbites.ChefFoodPanel;

public class chefProjectModel {

    private String key; // Unique key for each item
    private String DishName, Description, Quantity, Price, PostImage;

    public chefProjectModel() {
    }

    public chefProjectModel(String key, String dishName, String description, String quantity, String price, String postImage) {
        this.key = key;
        DishName = dishName;
        Description = description;
        Quantity = quantity;
        Price = price;
        PostImage = postImage;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDishName() {
        return DishName;
    }

    public void setDishName(String dishName) {
        DishName = dishName;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getPostImage() {
        return PostImage;
    }

    public void setPostImage(String postImage) {
        PostImage = postImage;
    }
}
