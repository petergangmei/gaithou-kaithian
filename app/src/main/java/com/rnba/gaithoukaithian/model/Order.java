package com.rnba.gaithoukaithian.model;

public class Order {
    public Order() {
    }

    private String orderId, orderBy, shippingAddress, productID, CartID, status, productName, productImage, payment;
    private long totalPrice, deliveryfee, quantity, timestamp;
    private boolean ordercanceled;
    private String timeNdate;

    public Order(String orderId, String orderBy, String shippingAddress, String productID, String cartID, String status, String productName, String productImage, String payment, long totalPrice, long deliveryfee, long quantity, long timestamp, boolean ordercanceled, String timeNdate) {
        this.orderId = orderId;
        this.orderBy = orderBy;
        this.shippingAddress = shippingAddress;
        this.productID = productID;
        CartID = cartID;
        this.status = status;
        this.productName = productName;
        this.productImage = productImage;
        this.payment = payment;
        this.totalPrice = totalPrice;
        this.deliveryfee = deliveryfee;
        this.quantity = quantity;
        this.timestamp = timestamp;
        this.ordercanceled = ordercanceled;
        this.timeNdate = timeNdate;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getCartID() {
        return CartID;
    }

    public void setCartID(String cartID) {
        CartID = cartID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public long getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(long totalPrice) {
        this.totalPrice = totalPrice;
    }

    public long getDeliveryfee() {
        return deliveryfee;
    }

    public void setDeliveryfee(long deliveryfee) {
        this.deliveryfee = deliveryfee;
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

    public boolean isOrdercanceled() {
        return ordercanceled;
    }

    public void setOrdercanceled(boolean ordercanceled) {
        this.ordercanceled = ordercanceled;
    }

    public String getTimeNdate() {
        return timeNdate;
    }

    public void setTimeNdate(String timeNdate) {
        this.timeNdate = timeNdate;
    }
}
