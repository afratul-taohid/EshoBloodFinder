package com.app.appathon.blooddonateapp.helper;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.app.appathon.blooddonateapp.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

/**
 * Created by TAOHID on 10/18/2017.
 */

public class InterstitialAdsHelper {

    private Activity context;

    public InterstitialAdsHelper(Activity context) {
        this.context = context;
    }

    private InterstitialAd interstitialAd;
    boolean exitApp = false;

    public void launchInter(){
        interstitialAd =new InterstitialAd(context);
        interstitialAd.setAdUnitId(context.getString(R.string.Interstitial));
        //Set the adListener
        interstitialAd.setAdListener(new AdListener() {

            public void onAdLoaded() {
                showAdInter();
            }
            public void onAdFailedToLoad(int errorCode) {
                String message = String.format("onAdFailedToLoad(%s)", getErrorReason(errorCode));

            }
            @Override
            public void onAdClosed() {
                if (exitApp)
                    context.finish();
            }
        });

    }

    private void showAdInter(){
        if(interstitialAd.isLoaded()){
            interstitialAd.show();
        } else{
            Log.d("", "ad was not ready to shown");
        }
    }

    public void loadInterstitial(){
        AdRequest adRequest= new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("INSERT_YOUR_HASH_DEVICE_ID")
                .build();
        //Load this Interstitial ad
        interstitialAd.loadAd(adRequest);
    }

    //Get a string error
    private String getErrorReason(int errorCode){

        String errorReason="";
        switch(errorCode){
            case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                errorReason="Internal Error";
                break;
            case AdRequest.ERROR_CODE_INVALID_REQUEST:
                errorReason="Invalid Request";
                break;
            case AdRequest.ERROR_CODE_NETWORK_ERROR:
                errorReason="Network Error";
                break;
            case AdRequest.ERROR_CODE_NO_FILL:
                errorReason="No Fill";
                break;
        }
        return errorReason;
    }
}
