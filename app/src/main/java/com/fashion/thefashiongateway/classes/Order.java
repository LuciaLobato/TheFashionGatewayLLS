package com.fashion.thefashiongateway.classes;

import com.google.firebase.Timestamp;

import java.util.List;

public class Order {
    Timestamp timestamp;
    String userId;
    String id;
    List<Product> products;

    public Order(Timestamp timestamp, String userId, String id, List<Product> products) {
        this.timestamp = timestamp;
        this.userId = userId;
        this.id = id;
        this.products = products;
    }

    public Order() {
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
