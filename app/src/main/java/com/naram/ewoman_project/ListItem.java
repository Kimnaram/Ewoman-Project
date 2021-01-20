package com.naram.ewoman_project;

import android.graphics.Bitmap;

public class ListItem {

    private int item_no;
    private Bitmap image;
    private String name;
    private String price;
    private int wishlist;

    public ListItem(int item_no, Bitmap image, String name, String price, int wishlist) {
        this.item_no = item_no;
        this.image = image;
        this.name = name;
        this.price = price;
        this.wishlist = wishlist;
    }

    public int getItem_no() {
        return item_no;
    }

    public void setItem_no(int item_no) {
        this.item_no = item_no;
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

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}