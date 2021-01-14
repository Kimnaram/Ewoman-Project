package com.naram.ewoman_project;

import android.graphics.Bitmap;

public class ListOrder {
    private int item_no;
    private String name;
    private Bitmap image;
    private int price;
    private String class_name;
    private int class_price;
    private int count;
    private int deliv_price;

    public ListOrder(int item_no, String name, Bitmap image, int price, String class_name, int class_price, int count, int deliv_price) {
        this.item_no = item_no;
        this.name = name;
        this.image = image;
        this.price = price;
        this.class_name = class_name;
        this.class_price = class_price;
        this.count = count;
        this.deliv_price = deliv_price;
    }

    public ListOrder(int item_no, String class_name, int count) {
        this.item_no = item_no;
        this.class_name = class_name;
        this.count = count;
    }

    public int getItem_no() {
        return item_no;
    }

    public void setItem_no(int item_no) {
        this.item_no = item_no;
    }

    public String getClass_name() {
        return class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
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

    public int getClass_price() {
        return class_price;
    }

    public void setClass_price(int class_price) {
        this.class_price = class_price;
    }

    public int getDeliv_price() {
        return deliv_price;
    }

    public void setDeliv_price(int deliv_price) {
        this.deliv_price = deliv_price;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

}
