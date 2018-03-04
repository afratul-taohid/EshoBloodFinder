package com.app.appathon.blooddonateapp.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.app.appathon.blooddonateapp.R;
import com.app.appathon.blooddonateapp.helper.InterstitialAdsHelper;
import com.app.appathon.blooddonateapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SplashActivity extends Activity {

    private FirebaseAuth mAuth;
    private DatabaseReference firebaseDatabase;
    private InterstitialAdsHelper interAds;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Arkhip_font.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_splash);

        View mSplashImage = findViewById(R.id.splash);
        View mSplashText = findViewById(R.id.splashText);
        Animation splashAnimImage = AnimationUtils.loadAnimation(this, R.anim.splash_anim_img);
        splashAnimImage.setInterpolator(new AccelerateDecelerateInterpolator());
        Animation splashAnimText = AnimationUtils.loadAnimation(this, R.anim.splash_anim);
        splashAnimText.setInterpolator(new AccelerateDecelerateInterpolator());
        mSplashText.startAnimation(splashAnimText);
        mSplashImage.startAnimation(splashAnimImage);

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase= FirebaseDatabase.getInstance().getReference();

        interAds=new InterstitialAdsHelper(this);

        int SPLASH_DISPLAY_LENGTH = 2000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                final FirebaseUser user = mAuth.getCurrentUser();
                //Check auth on Activity start
                if (user != null) {
                    firebaseDatabase.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot data: dataSnapshot.getChildren()){
                                if (data.exists()) {
                                    interAds.launchInter();
                                    interAds.loadInterstitial();
                                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);
                                    finish();
                                    break;
                                } else {
                                    startActivity(new Intent(SplashActivity.this, SignUpActivity.class));
                                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);
                                    finish();
                                }
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                    startActivity(new Intent(SplashActivity.this, SignInActivity.class));
                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);
                    finish();
                }
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
