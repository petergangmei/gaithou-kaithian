package com.rnba.gaithoukaithian.model;

public class Product {
    public Product() {
    }


    private String id;
    private String title;
    private String description;
    private String imageURL;
    private int mPrice, dPrice;
    private int deliveryFee;
    private int stocks;
    private String sallerLocation;
    private String  availableIn;
    private boolean cod;
    private long timestamp;
    private String ctime, cdate;

    public Product(String id, String title, String description, String imageURL, int mPrice, int dPrice, int deliveryFee, int stocks, String sallerLocation, String availableIn, boolean cod, long timestamp, String ctime, String cdate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageURL = imageURL;
        this.mPrice = mPrice;
        this.dPrice = dPrice;
        this.deliveryFee = deliveryFee;
        this.stocks = stocks;
        this.sallerLocation = sallerLocation;
        this.availableIn = availableIn;
        this.cod = cod;
        this.timestamp = timestamp;
        this.ctime = ctime;
        this.cdate = cdate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public int getmPrice() {
        return mPrice;
    }

    public void setmPrice(int mPrice) {
        this.mPrice = mPrice;
    }

    public int getdPrice() {
        return dPrice;
    }

    public void setdPrice(int dPrice) {
        this.dPrice = dPrice;
    }

    public int getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(int deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public int getStocks() {
        return stocks;
    }

    public void setStocks(int stocks) {
        this.stocks = stocks;
    }

    public String getSallerLocation() {
        return sallerLocation;
    }

    public void setSallerLocation(String sallerLocation) {
        this.sallerLocation = sallerLocation;
    }

    public String getAvailableIn() {
        return availableIn;
    }

    public void setAvailableIn(String availableIn) {
        this.availableIn = availableIn;
    }

    public boolean isCod() {
        return cod;
    }

    public void setCod(boolean cod) {
        this.cod = cod;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getCtime() {
        return ctime;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }

    public String getCdate() {
        return cdate;
    }

    public void setCdate(String cdate) {
        this.cdate = cdate;
    }
}
