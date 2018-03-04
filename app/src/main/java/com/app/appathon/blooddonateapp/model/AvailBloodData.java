package com.app.appathon.blooddonateapp.model;

/**
 * Created by Sunny on 10/24/2016.
 */

public class AvailBloodData {

    String name, email, phone, area, district, bldtype, lastDonate;

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

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getBldtype() {
        return bldtype;
    }

    public void setBldtype(String bldtype) {
        this.bldtype = bldtype;
    }

    public String getLastDonate() {
        return lastDonate;
    }

    public void setLastDonate(String lastDonate) {
        this.lastDonate = lastDonate;
    }
}
