package com.app.appathon.blooddonateapp.config;

/**
 * Created by IMRAN on 9/27/2017.
 */

public class Config {
    public static String CURRENT_USERNAME = "";
    public static String CURRENT_USER_PHONE = "";

    public static void setTestName(String Name){
        Config.CURRENT_USERNAME = Name;
    }

    public void setName(String name){
        setTestName(name);
    }

    public static void setTestPhone(String Phone){
        Config.CURRENT_USER_PHONE = Phone;
    }

    public void setPhone(String phone){
        setTestPhone(phone);
    }
}
