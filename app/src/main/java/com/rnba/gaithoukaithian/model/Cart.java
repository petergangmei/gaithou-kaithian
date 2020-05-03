package com.rnba.gaithoukaithian.model;

public class Cart {
    public Cart() {
    }

    private String id, productid, purchaserid;
    private long price, deliveryFee, quantity, timestamp;

    public Cart(String id, String productid, String purchaserid, long price, long deliveryFee, long quantity, long timestamp) {
        this.id = id;
        this.productid = productid;
        this.purchaserid = purchaserid;
        this.price = price;
        this.deliveryFee = deliveryFee;
        this.quantity = quantity;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductid() {
        return productid;
    }

    public void setProductid(String productid) {
        this.productid = productid;
    }

    public String getPurchaserid() {
        return purchaserid;
    }

    public void setPurchaserid(String purchaserid) {
        this.purchaserid = purchaserid;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public long getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(long deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
