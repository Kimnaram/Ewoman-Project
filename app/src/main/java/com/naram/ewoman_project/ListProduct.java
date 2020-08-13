package com.naram.ewoman_project;

import android.graphics.drawable.Drawable;

public class ListProduct {

    private Drawable image;
    private String name;
    private int price;
    private int wishlist;

    public ListProduct(Drawable image, String name, int price, int wishlist) {
        this.image = image;
        this.name = name;
        this.price = price;
        this.wishlist = wishlist;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getWishlist() {
        return wishlist;
    }

    public void setWishlist(int wishlist) {
        this.wishlist = wishlist;
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }
}
