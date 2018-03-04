package com.app.appathon.blooddonateapp.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.app.appathon.blooddonateapp.R;
import com.app.appathon.blooddonateapp.adapter.PlacesAutoCompleteAdapter;
import com.app.appathon.blooddonateapp.model.ProfileSecurity;
import com.app.appathon.blooddonateapp.model.User;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ProfileActivity extends AppCompatActivity implements ValueEventListener {

    private FirebaseAuth mAuth;
    private ArrayList<User> userArrayList = new ArrayList<>();
    private TextView name, email, bloodType, phone, address, lastDonate, state, gender;
    private FirebaseUser firebaseUser;
    private Switch mSwitch;
    private View snackView;
    private String user_phone, user_area;
    private String user_donate;
    private ArrayList<Integer> month = new ArrayList<>();
    private EditText phoneEdit, donateET;
    private AutoCompleteTextView auto;
    private TextView ago;
    private ImageView calender;

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
        setContentView(R.layout.activity_profile);

        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        for(int i=0; i<13; i++){
            month.add(i);
        }

        //Initializing Firebase Database Reference
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        //Custom font
        Typeface Helvetica = Typeface.createFromAsset(getAssets(), "fonts/HelveticaNeue.ttf");

        //Initializing Layout Components
        name = (TextView) findViewById(R.id.profile_name);
        email = (TextView) findViewById(R.id.user_email);
        bloodType = (TextView) findViewById(R.id.user_blood);
        phone = (TextView) findViewById(R.id.user_phone);
        lastDonate = (TextView) findViewById(R.id.donate_date);
        address = (TextView) findViewById(R.id.user_area);
        mSwitch = (Switch) findViewById(R.id.security_switch);
        snackView = findViewById(R.id.activity_profile);
        state = (TextView) findViewById(R.id.state);
        gender = (TextView) findViewById(R.id.user_gender);
        ago = (TextView) findViewById(R.id.ago);

        name.setTypeface(Helvetica);
        email.setTypeface(Helvetica);
        bloodType.setTypeface(Helvetica);
        phone.setTypeface(Helvetica);
        lastDonate.setTypeface(Helvetica);
        address.setTypeface(Helvetica);
        gender.setTypeface(Helvetica);

        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ProfileSecurity security = new ProfileSecurity(isChecked);
                    mDatabase.child("users").child(firebaseUser.getUid()).child("security").setValue(security);
                    state.setText("Private");
                } else {
                    ProfileSecurity security = new ProfileSecurity(isChecked);
                    mDatabase.child("users").child(firebaseUser.getUid()).child("security").setValue(security);
                    state.setText("Public");
                }
            }
        });

        mDatabase.child("users").addValueEventListener(this);

    }
    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        startActivity(new Intent(ProfileActivity.this, MainActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ProfileActivity.this, MainActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        finish();
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if (!userArrayList.isEmpty()){
            userArrayList.clear();
        }

        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
            User user = snapshot.getValue(User.class);
            if (snapshot.getKey().equals(firebaseUser.getUid())){
                assert user != null;
                user_phone = user.getPhone();
                try {
                    String dDate = user.getLastDonate();
                    if (dDate.compareTo("Never")==0){
                        lastDonate.setText(dDate);
                        ago.setText("");
                    } else {
                        user_donate = differenceBetweenDates(user.getLastDonate());
                        lastDonate.setText(user_donate.split("\\s")[0]);
                        ago.setText("" + user_donate.split("\\s")[1] + " " + user_donate.split("\\s")[2]);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                user_area = user.getAddress();
                name.setText(user.getName());
                email.setText(user.getEmail());
                bloodType.setText(user.getBloodGroup());
                phone.setText(user.getPhone());
                address.setText(user.getAddress());
                gender.setText(user.getGender());

                if (snapshot.hasChild("security")){
                    DataSnapshot data = snapshot.child("security");
                    boolean isVisible = Boolean.parseBoolean(data.child("phoneHidden").getValue().toString());
                    if (isVisible){
                        mSwitch.setChecked(true);
                    } else {
                        mSwitch.setChecked(false);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_edit:
                initUpdateLayout();
                return true;
            default:
                return false;
        }
    }

    private void initUpdateLayout() {
        MaterialDialog.Builder builder  = new MaterialDialog.Builder(this)
                .title("Edit Profile")
                .customView(R.layout.edit_profile, true)
                .titleColorRes(android.R.color.white)
                .backgroundColorRes(R.color.dialog_color)
                .positiveText("Update")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        updateProfile();
                    }
                });

        MaterialDialog dialog = builder.build();
        phoneEdit = (EditText) dialog.findViewById(R.id.input_phone);
        phoneEdit.setText(user_phone);
        calender = (ImageView) dialog.findViewById(R.id.calender);

        donateET = (EditText) dialog.findViewById(R.id.donateDate);
        donateET.setText(String.valueOf(user_donate));
        donateET.setEnabled(false);

        calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });


        auto = (AutoCompleteTextView) dialog.findViewById(R.id.input_area);
        auto.setText(user_area);
        PlacesAutoCompleteAdapter adapter = new PlacesAutoCompleteAdapter(this, R.layout.autocomplete_list_item);
        adapter.notifyDataSetChanged();
        auto.setAdapter(adapter);
        dialog.show();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    private void showDatePicker() {
        CalendarDatePickerDialogFragment dialog = new CalendarDatePickerDialogFragment()
                .setOnDateSetListener(dateSetListener)
                .setThemeDark();
        dialog.show(getSupportFragmentManager(), "DATE_PICKER_TAG");
    }

    CalendarDatePickerDialogFragment.OnDateSetListener dateSetListener = new CalendarDatePickerDialogFragment.OnDateSetListener() {
        @Override
        public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
            // Set date from user input.
            monthOfYear = monthOfYear +1;
            String date_of_birth = dayOfMonth + "/" + monthOfYear + "/" + year;
            donateET.setText(date_of_birth);
        }
    };

    private void updateProfile() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        assert user != null;
        String userId = user.getUid();

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        //update phone
        String phone = phoneEdit.getText().toString();
        database.child("users").child(userId).child("phone").setValue(phone);

        //update last donate
        String dDate;
        if (TextUtils.isEmpty(donateET.getText().toString())){
            dDate = "Never";
        } else {
            dDate = donateET.getText().toString();
        }
        database.child("users").child(userId).child("lastDonate").setValue(dDate);

        //update area
        String area = auto.getText().toString();
        database.child("users").child(userId).child("address").setValue(area);
    }
}
