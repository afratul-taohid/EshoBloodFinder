package com.app.appathon.blooddonateapp.model;

/**
 * Created by IMRAN on 7/30/2017.
 */

public class ProfileSecurity {
    private boolean isPhoneHidden;

    public ProfileSecurity(){

    }

    public ProfileSecurity(boolean isPhoneHidden) {
        this.isPhoneHidden = isPhoneHidden;
    }

    public boolean isPhoneHidden() {
        return isPhoneHidden;
    }
}
