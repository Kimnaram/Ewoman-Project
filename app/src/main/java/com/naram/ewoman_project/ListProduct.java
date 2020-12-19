package com.naram.ewoman_project;

import android.graphics.drawable.Drawable;

public class ListProduct {

    private int pdnumber;
    private Drawable image;
    private String name;
    private String price;
    private int wishlist;

    public ListProduct(int pdnumber, Drawable image, String name, String price, int wishlist) {
        this.pdnumber = pdnumber;
        this.image = image;
        this.name = name;
        this.price = price;
        this.wishlist = wishlist;
    }

    public int getPdnumber() {
        return pdnumber;
    }

    public void setPdnumber(int pdnumber) {
        this.pdnumber = pdnumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
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
