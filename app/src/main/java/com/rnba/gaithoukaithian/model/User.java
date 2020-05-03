package com.rnba.gaithoukaithian.model;

public class User {

    public User() {
    }
    private String id, fullName, phone, email, address, timeNdate, role;
    private Boolean admin;
    private Long timestamp;

    public User(String id, String fullName, String phone, String email, String address, String timeNdate, String role, Boolean admin, Long timestamp) {
        this.id = id;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.timeNdate = timeNdate;
        this.role = role;
        this.admin = admin;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTimeNdate() {
        return timeNdate;
    }

    public void setTimeNdate(String timeNdate) {
        this.timeNdate = timeNdate;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
