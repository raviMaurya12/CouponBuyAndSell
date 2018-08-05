package com.example.lenovo.buyandsell;

public class Coupons {

    String caterer;
    String date;
    String meal;
    String negotiable;
    String price;
    String remark;
    String seller;
    String state;
    long timestamp;

    public Coupons(String caterer, String date, String meal, String negotiable, String price, String remark, String seller, String state, long timestamp) {
        this.caterer = caterer;
        this.date = date;
        this.meal = meal;
        this.negotiable = negotiable;
        this.price = price;
        this.remark = remark;
        this.seller = seller;
        this.state = state;
        this.timestamp = timestamp;
    }

    public Coupons() {
    }

    public String getCaterer() {
        return caterer;
    }

    public void setCaterer(String caterer) {
        this.caterer = caterer;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMeal() {
        return meal;
    }

    public void setMeal(String meal) {
        this.meal = meal;
    }

    public String getNegotiable() {
        return negotiable;
    }

    public void setNegotiable(String negotiable) {
        this.negotiable = negotiable;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
