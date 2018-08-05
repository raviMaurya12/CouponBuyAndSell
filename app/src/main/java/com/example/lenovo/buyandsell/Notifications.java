package com.example.lenovo.buyandsell;

public class Notifications {

    String coupon_id;
    String coupon_details;
    String responder_id;
    String state;
    long timestamp;
    String seller;

    public Notifications() {
    }

    public Notifications(String coupon_id, String coupon_details, String responder_id, String state, long timestamp, String seller) {
        this.coupon_id = coupon_id;
        this.coupon_details = coupon_details;
        this.responder_id = responder_id;
        this.state = state;
        this.timestamp = timestamp;
        this.seller = seller;
    }

    public String getCoupon_id() {
        return coupon_id;
    }

    public void setCoupon_id(String coupon_id) {
        this.coupon_id = coupon_id;
    }

    public String getCoupon_details() {
        return coupon_details;
    }

    public void setCoupon_details(String coupon_details) {
        this.coupon_details = coupon_details;
    }

    public String getResponder_id() {
        return responder_id;
    }

    public void setResponder_id(String responder_id) {
        this.responder_id = responder_id;
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

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }
}
