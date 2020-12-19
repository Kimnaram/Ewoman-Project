package com.naram.ewoman_project;

public class ListCart {

    private int pdnumber;
    private String name;
    private String date;
    private int count;
    private String price;
    private String category;

    public ListCart(int pdnumber, String name, String date, int count, String price, String category) {
        this.pdnumber = pdnumber;
        this.name = name;
        this.date = date;
        this.count = count;
        this.price = price;
        this.category = category;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

}
