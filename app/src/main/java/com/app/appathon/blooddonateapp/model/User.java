package com.app.appathon.blooddonateapp.model;

/**
 * Created by IMRAN on 7/24/2017.
 */

public class User {

    public String id;
    public String sendNotification;
    public String name;
    public String email;
    public String phone;
    public String address;
    public String bloodGroup;
    public String lastDonate;
    public String gender;
    public double lat;
    public double lng;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String id, String name, String email, String phone, String address,
                String bloodGroup, String lastDonate, String gender, double lat, double lng, String sendNotification) {
        this.id = id;
        this.lat = lat;
        this.lng = lng;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.bloodGroup = bloodGroup;
        this.lastDonate = lastDonate;
        this.gender = gender;
        this.sendNotification= sendNotification;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public String getNotificationId() {
        return sendNotification;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getLastDonate() {
        return lastDonate;
    }

    public void setLastDonate(String lastDonate) {
        this.lastDonate = lastDonate;
    }
}
