package com.app.appathon.blooddonateapp.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.app.appathon.blooddonateapp.R;
import com.app.appathon.blooddonateapp.database.FirebaseDatabaseHelper;
import com.app.appathon.blooddonateapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener,
        ValueEventListener, FirebaseDatabaseHelper.RequestToUser {

    private String phone;
    private ArrayList<User> userArrayList = new ArrayList<>();
    private String uId;
    private TextView nameView, addressView, phoneView, bloodView, donateView, genderView;
    private String bloodGroup, name, gender, nId;
    private View snackView;
    private FloatingActionButton buttonCall;
    private TextView ago;

    private static final int REQUEST_PHONE_CALL = 1;

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
        setContentView(R.layout.activity_user_profile);

        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        uId = intent.getStringExtra("id");

        //Initializing Firebase Database Reference
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        //Initializing layout components
        nameView = (TextView) findViewById(R.id.profile_name);
        addressView = (TextView) findViewById(R.id.user_area);
        phoneView = (TextView) findViewById(R.id.user_phone);
        bloodView = (TextView) findViewById(R.id.user_blood);
        donateView = (TextView) findViewById(R.id.donate_date);
        genderView = (TextView) findViewById(R.id.user_gender);
        buttonCall = (FloatingActionButton) findViewById(R.id.call_btn);
        snackView = findViewById(R.id.user_profile);
        ago = (TextView) findViewById(R.id.ago);

        mDatabase.child("users").addValueEventListener(this);
        buttonCall.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.call_btn:
                if (gender.compareTo("Male")==0){
                    showDialogWindow("Him");
                } else {
                    showDialogWindow("Her");
                }
                break;
            default:
                break;
        }
    }

    private void showDialogWindow(String s) {
        new MaterialDialog.Builder(this)
                .title(phone)
                .icon(ContextCompat.getDrawable(this, R.drawable.ic_phone_round))
                .positiveText("Call " + s)
                .backgroundColorRes(R.color.dialog_color)
                .titleColorRes(android.R.color.white)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        try {
                            Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                            phoneIntent.setData(Uri.parse("tel:" + phone));
                            if (ContextCompat.checkSelfPermission(UserProfileActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(UserProfileActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
                            } else {
                                startActivity(phoneIntent);
                            }
                        } catch (android.content.ActivityNotFoundException | SecurityException ex) {
                            Toast.makeText(UserProfileActivity.this,
                                    "Call failed, please try again later!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PHONE_CALL: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                        phoneIntent.setData(Uri.parse("tel:" + phone));
                        startActivity(phoneIntent);

                    } catch (android.content.ActivityNotFoundException | SecurityException ex) {
                        Toast.makeText(UserProfileActivity.this,
                                "Call failed, please try again later!", Toast.LENGTH_SHORT).show();
                    }
                } else {

                }
                return;
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if (userArrayList.size() > 0) {
            userArrayList.clear();
        }

        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            User user = snapshot.getValue(User.class);
            assert user != null;
            String id = "" + user.getId();
            if (id.equals(uId)) {
                //get user name
                name = user.getName();
                nameView.setText(name);
                //get user address
                addressView.setText(user.getAddress());
                //get user blood group
                bloodGroup = user.getBloodGroup();
                bloodView.setText(bloodGroup);
                //get user last donate date
                try {
                    String dDate = user.getLastDonate();
                    if (dDate.compareTo("Never")==0){
                        ago.setText("");
                        donateView.setText(dDate);
                    } else {
                        String user_donate = differenceBetweenDates(user.getLastDonate());
                        donateView.setText(user_donate.split("\\s")[0]);
                        ago.setText("" + user_donate.split("\\s")[1] + " " + user_donate.split("\\s")[2]);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //user gender
                gender = user.getGender();
                genderView.setText(gender);
                //user phone
                phone = user.getPhone();
                phoneView.setText(phone);
                //notification id
                nId = user.getNotificationId();

                if (snapshot.hasChild("security")) {
                    DataSnapshot data = snapshot.child("security");
                    boolean isHidden = Boolean.parseBoolean(data.child("phoneHidden").getValue().toString());
                    if (isHidden) {
                        buttonCall.setEnabled(false);
                        phoneView.setText(R.string.private_mode);
                    } else {
                        buttonCall.setEnabled(true);
                    }
                }
            }
        }
    }

    private String differenceBetweenDates(String prev_date) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date p_date = simpleDateFormat.parse(prev_date);
        Date now = new Date(System.currentTimeMillis());

        //difference between dates
        long difference = Math.abs(p_date.getTime() - now.getTime());
        long differenceDates = difference / (24 * 60 * 60 * 1000);

        int month = (int) differenceDates/30;

        if (month >= 12){
            int year = month / 12;

            if (year == 1){
                return "" + year + " year ago";
            } else {
                return "" + year + " years ago";
            }
        } else {
            if (month == 1){
                return "" + month + " month ago";
            } else {
                return "" + month + " month ago";
            }
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        showSnackMessage(databaseError.getMessage());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_send:
                sendRequest();
                return true;
            default:
                return false;
        }
    }

    private void sendRequest() {
        FirebaseDatabaseHelper firebaseDatabaseHelper =
                new FirebaseDatabaseHelper(this, this);
        firebaseDatabaseHelper.SendRequestMsgToUser(uId, nId, name, bloodGroup);
    }

    private void showSnackMessage(String message) {
        int color = Color.RED;
        int TIME_OUT = Snackbar.LENGTH_SHORT;

        Snackbar snackbar = Snackbar
                .make(snackView, message, TIME_OUT);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }

    @Override
    public void SendRequestMsgToUser(String userId, String email, String name, String blood) {

    }
}
