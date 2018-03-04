package com.app.appathon.blooddonateapp.activities;

import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.app.appathon.blooddonateapp.BuildConfig;
import com.app.appathon.blooddonateapp.config.Config;
import com.app.appathon.blooddonateapp.R;
import com.app.appathon.blooddonateapp.fragments.LocatingDonors;
import com.app.appathon.blooddonateapp.model.User;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.onesignal.OSPermissionSubscriptionState;
import com.onesignal.OneSignal;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class MainActivity extends AppCompatActivity
        implements ValueEventListener, SearchView.OnQueryTextListener {

    private FragmentTransaction fragmentTransaction;
    public MaterialSpinner spinner;
    private Fragment fragment;
    private FirebaseUser firebaseUser;
    private TextView headerText;
    public FragmentCommunicator fragmentCommunicator;
    public int someIntValue = 1;

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
        setContentView(R.layout.activity_main);

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Handle Toolbar
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        //final Typeface ThemeFont = Typeface.createFromAsset(getAssets(), "fonts/Arkhip_font.ttf");

        //Initializing Firebase
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").addValueEventListener(this);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        //Setting The Tag for sending notification
        OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
        String userID = status.getSubscriptionStatus().getUserId();
        OneSignal.setSubscription(true);
        mDatabase.child("users").child(firebaseUser.getUid()).child("sendNotification").setValue(userID);

        //Inflating NavHeader
        View navHeader = View.inflate(this, R.layout.navbar_head, null);
        headerText = (TextView) navHeader.findViewById(R.id.user_name);
        //headerText.setTypeface(ThemeFont);

        //Initializing Navigation Drawer
        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHeader(navHeader)
                .withTranslucentStatusBar(true)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("Find Donors")
                                .withIcon(GoogleMaterial.Icon.gmd_search)
                                .withIconColor(Color.GRAY)
                                .withTextColor(Color.GRAY)
                                .withSelectedTextColor(Color.DKGRAY)
                                .withIdentifier(1),
                        new PrimaryDrawerItem()
                                .withName("Inbox")
                                .withIcon(GoogleMaterial.Icon.gmd_inbox)
                                .withIconColor(Color.GRAY)
                                .withTextColor(Color.GRAY)
                                .withSelectedTextColor(Color.DKGRAY)
                                .withIdentifier(2),
                        new SectionDrawerItem().withName("More")
                                .withTextColor(Color.GRAY),
                        new PrimaryDrawerItem()
                                .withName("My Profile")
                                .withIcon(GoogleMaterial.Icon.gmd_account)
                                .withIconColor(Color.GRAY)
                                .withTextColor(Color.GRAY)
                                .withSelectedTextColor(Color.DKGRAY)
                                .withIdentifier(3),
                        new PrimaryDrawerItem()
                                .withName("Sign Out")
                                .withIcon(FontAwesome.Icon.faw_sign_out)
                                .withIconColor(Color.GRAY)
                                .withTextColor(Color.GRAY)
                                .withSelectedTextColor(Color.DKGRAY)
                                .withIdentifier(5)
                ) // add the items we want to use with our Drawer
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {

                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem != null) {
                            if (drawerItem.getIdentifier() == 1) {
                                fragment = LocatingDonors.newInstance();
                                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                                fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right);
                                fragmentTransaction.replace(R.id.fragment_container, fragment).commit();
                                toolbar.setSubtitle("Find Donors");
                            } else if (drawerItem.getIdentifier() == 3) {
                                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                                finish();
                            } else if (drawerItem.getIdentifier() == 5) {
                                FirebaseAuth.getInstance().signOut();
                                OneSignal.setSubscription(false);
                                startActivity(new Intent(MainActivity.this, SignInActivity.class));
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                                finish();
                            } else if (drawerItem.getIdentifier() == 2) {
                                startActivity(new Intent(MainActivity.this, InboxActivity.class));
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                                finish();
                            }
                        }
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .withSelectedItem(1)
                // build only the view of the Drawer (don't inflate it automatically in our layout which is done with .build())
                .build();
        result.setSelection(1, true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_map:
                startActivity(new Intent(MainActivity.this, NearbyDonorActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                finish();
                return true;
            case R.id.menu_search:
                return true;
            case R.id.rate:
                rateMyApp();
                return true;
            case R.id.about:
                aboutMyApp();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            User uData = snapshot.getValue(User.class);
            if (uData != null) {
                if (snapshot.getKey().equals(firebaseUser.getUid())) {
                    String displayName = uData.getName();
                    headerText.setText(displayName);
                    Config config = new Config();
                    config.setName(uData.getName());
                    config.setPhone(uData.getPhone());
                }
            }
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        fragmentCommunicator.passDataToFragment(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    public interface FragmentCommunicator {
        void passDataToFragment(String value);
    }

    private void aboutMyApp() {
        MaterialDialog.Builder bulder = new MaterialDialog.Builder(this)
                .title(R.string.app_name)
                .customView(R.layout.about, true)
                .backgroundColor(getResources().getColor(R.color.dialog_color))
                .titleColorRes(android.R.color.white)
                .positiveText("MORE APPS")
                .icon(getResources().getDrawable(R.mipmap.ic_launcher))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Uri uri = Uri.parse("market://search?q=pub:" + "NerdGeeks");
                        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                        try {
                            startActivity(goToMarket);
                        } catch (ActivityNotFoundException e) {
                            startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("http://play.google.com/store/search?q=pub:" + "NerdGeeks")));
                        }
                    }
                });

        MaterialDialog materialDialog = bulder.build();

        TextView versionCode = (TextView) materialDialog.findViewById(R.id.version_code);
        TextView versionName = (TextView) materialDialog.findViewById(R.id.version_name);
        versionCode.setText(String.valueOf("vCode : " + BuildConfig.VERSION_CODE));
        versionName.setText("vName : " + BuildConfig.VERSION_NAME);

        materialDialog.show();
    }

    private void rateMyApp() {
        Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName())));
        }
    }
}
