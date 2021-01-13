package com.naram.ewoman_project;

public class ListOrder {
    private int item_no;
    private String class_name;
    private int count;

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

}
