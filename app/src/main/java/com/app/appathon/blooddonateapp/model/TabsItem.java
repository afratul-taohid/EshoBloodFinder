package com.app.appathon.blooddonateapp.model;

import android.support.v4.app.Fragment;

/**
 * Created by hp on 6/28/2016.
 */
public class TabsItem {
    private final CharSequence mTitle;
    private final Fragment mFragment;

    public TabsItem(CharSequence title, Fragment fragment) {
        this.mTitle = title;
        this.mFragment = fragment;
    }

    public Fragment getFragment() {
        return mFragment;
    }

    public CharSequence getTitle() {
        return mTitle;
    }
}
