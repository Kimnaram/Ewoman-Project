package com.naram.ewoman_project;

import android.graphics.Bitmap;

public class ListCart {

    private int item_no;
    private String name;
    private Bitmap image;
    private String date;
    private int count;
    private int price;
    private String class_name;

    public ListCart(int item_no, String name, String date, Bitmap image, int count, int price) {
        this.item_no = item_no;
        this.name = name;
        this.date = date;
        this.image = image;
        this.count = count;
        this.price = price;
    }

    public ListCart(int item_no, String name, String date, Bitmap image, int count, int price, String class_name) {
        this.item_no = item_no;
        this.name = name;
        this.date = date;
        this.image = image;
        this.count = count;
        this.price = price;
        this.class_name = class_name;
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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getClass_name() {
        return class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }

}
