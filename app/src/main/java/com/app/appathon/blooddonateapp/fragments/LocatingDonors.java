package com.app.appathon.blooddonateapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.app.appathon.blooddonateapp.R;
import com.app.appathon.blooddonateapp.adapter.TabsAdapter;
import com.app.appathon.blooddonateapp.model.TabsItem;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;

public class LocatingDonors extends Fragment implements MaterialTabListener {

    private MaterialTabHost tabHost;
    private ViewPager viewPager;
    private List<TabsItem> mTabs = new ArrayList<>();
    private AdView mAdView;

    public LocatingDonors() {
        // Required empty public constructor
        createTabsItem();
    }

    public static LocatingDonors newInstance(){
        LocatingDonors fragment = new LocatingDonors();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private void createTabsItem() {
        mTabs.add(new TabsItem("Available", AvailableDonors.newInstance()));
        mTabs.add(new TabsItem("All", AllDonors.newInstance()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_locating_donors, container, false);

        final CardView cardView = (CardView) rootView.findViewById(R.id.card);
        final Animation slide_down = AnimationUtils.loadAnimation(getContext(),
                R.anim.slide_down);

        mAdView = (AdView) rootView.findViewById(R.id.adView);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int i) {
                cardView.setVisibility(View.GONE);
            }

            @Override
            public void onAdLoaded() {
                cardView.setVisibility(View.VISIBLE);
                cardView.setAnimation(slide_down);
                viewPager.setAnimation(slide_down);
            }
        });
        mAdView.loadAd(new AdRequest.Builder().build());
        viewPager = (ViewPager) rootView.findViewById(R.id.viewPager);

        //Adding TabHost
        tabHost = (MaterialTabHost) rootView.findViewById(R.id.materialTabHost);

         //Set an Adapter for the View Pager
        TabsAdapter tAdapter = new TabsAdapter(getChildFragmentManager(), mTabs);
        viewPager.setOffscreenPageLimit(mTabs.size());
        viewPager.setAdapter(tAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // when user do a swipe the selected tab change
                tabHost.setSelectedNavigationItem(position);
            }
        });

        // insert all tabs from pagerAdapter data
        for (int i = 0; i < tAdapter.getCount(); i++) {
            tabHost.addTab(
                    tabHost.newTab()
                            .setText(tAdapter.getPageTitle(i))
                            .setTabListener(this)
            );
        }
        return rootView;
    }

    @Override
    public void onTabSelected(MaterialTab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(MaterialTab tab) {

    }

    @Override
    public void onTabUnselected(MaterialTab tab) {

    }
}
