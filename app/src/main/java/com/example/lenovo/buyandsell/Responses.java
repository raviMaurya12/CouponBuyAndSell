package com.example.lenovo.buyandsell;

public class Responses {

    private String seller;
    private String state;

    public Responses() {
    }

    public Responses(String seller, String state) {
        this.seller = seller;
        this.state = state;
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
}
