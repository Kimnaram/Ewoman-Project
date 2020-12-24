package com.naram.ewoman_project;

import android.graphics.Bitmap;

public class ListCart {

    private int item_no;
    private String name;
    private Bitmap image;
    private String date;
    private int count;
    private String price;

    public ListCart(int item_no, String name, String date, Bitmap image, int count, String price) {
        this.item_no = item_no;
        this.name = name;
        this.date = date;
        this.count = count;
        this.price = price;
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

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

}
