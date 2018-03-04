package com.app.appathon.blooddonateapp.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.app.appathon.blooddonateapp.model.TabsItem;

import java.util.List;

/**
 * Created by hp on 6/28/2016.
 */
public class TabsAdapter extends FragmentStatePagerAdapter {
    private List<TabsItem> mTabs;
    public TabsAdapter(FragmentManager fm, List<TabsItem> tabs) {
        super(fm);
        this.mTabs = tabs;
    }

    public void setDataSource(List<TabsItem> dataSource){
        mTabs = dataSource;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return mTabs.get(position).getFragment();
    }

    @Override
    public int getCount() {
        return mTabs.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabs.get(position).getTitle();
    }
}
