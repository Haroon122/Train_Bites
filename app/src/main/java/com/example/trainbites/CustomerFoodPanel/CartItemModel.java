package com.example.trainbites.CustomerFoodPanel;

import android.os.Parcel;
import android.os.Parcelable;

public class CartItemModel implements Parcelable {
    private String dishName;
    private String price;
    private String imageUrl;
    private String key;
    private int quantity;

    // Default constructor required for Firebase
    public CartItemModel() {
        this.quantity = 1;
    }

    public CartItemModel(String dishName, String price, String imageUrl, String key) {
        this.dishName = dishName;
        this.price = price;
        this.imageUrl = imageUrl;
        this.key = key;
        this.quantity = 1;
    }

    protected CartItemModel(Parcel in) {
        dishName = in.readString();
        price = in.readString();
        imageUrl = in.readString();
        key = in.readString();
        quantity = in.readInt();
    }

    public static final Creator<CartItemModel> CREATOR = new Creator<CartItemModel>() {
        @Override
        public CartItemModel createFromParcel(Parcel in) {
            return new CartItemModel(in);
        }

        @Override
        public CartItemModel[] newArray(int size) {
            return new CartItemModel[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(dishName);
        dest.writeString(price);
        dest.writeString(imageUrl);
        dest.writeString(key);
        dest.writeInt(quantity);
    }
}
