package com.app.appathon.blooddonateapp.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.app.appathon.blooddonateapp.R;
import com.app.appathon.blooddonateapp.helper.InterstitialAdsHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText etPhone, etPwd;
    private DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private static final String TAG = "SignInActivity";
    private Button btn_signIn;
    private AppCompatImageButton btn_send;
    private ProgressBar progressBar;
    private TextInputLayout textInputLayout;
    private InterstitialAdsHelper interAds;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Arkhip_font.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_sign_in);

        progressBar = (ProgressBar) findViewById(R.id.marker_progress);

        interAds = new InterstitialAdsHelper(this);

        mDatabase= FirebaseDatabase.getInstance().getReference();

        etPhone = (EditText)findViewById(R.id.sin_email);
        etPwd = (EditText)findViewById(R.id.sin_password);
        btn_signIn = (Button) findViewById(R.id.btnSignIn);
        btn_send = (AppCompatImageButton) findViewById(R.id.btnSend);
        textInputLayout = (TextInputLayout) findViewById(R.id.codeView);

        btn_signIn.setOnClickListener(this);
        btn_send.setOnClickListener(this);

        btn_signIn.setVisibility(View.INVISIBLE);
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Log.d(TAG, "onVerificationCompleted:" + credential);
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.w(TAG, "onVerificationFailed", e);
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    etPhone.setError("Invalid phone number.");
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                            Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                Log.d(TAG, "onCodeSent:" + verificationId);
                progressBar.setVisibility(View.INVISIBLE);
                mVerificationId = verificationId;
                textInputLayout.setVisibility(View.VISIBLE);
                btn_signIn.setVisibility(View.VISIBLE);
            }
        };
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                120,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            final FirebaseUser user = task.getResult().getUser();
                            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for(DataSnapshot data: dataSnapshot.getChildren()){
                                        if (data.child(user.getUid()).child("id").exists()) {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            interAds.launchInter();
                                            interAds.loadInterstitial();
                                            startActivity(new Intent(SignInActivity.this, MainActivity.class));
                                            finish();
                                        } else {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
                                            finish();
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                etPwd.setError("Invalid code.");
                            }
                        }
                    }
                });
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSignIn :
                String code = etPwd.getText().toString();
                if (TextUtils.isEmpty(code)) {
                    etPwd.setError("Cannot be empty.");
                    return;
                }
                verifyPhoneNumberWithCode(mVerificationId, code);
                break;
            case R.id.btnSend:
                String phoneNm = etPhone.getText().toString();
                if (TextUtils.isEmpty(phoneNm)) {
                    etPhone.setError("Cannot be empty.");
                    return;
                }
                if (phoneNm.length()<10) {
                    etPhone.setError("Invalid Phone Number");
                    return;
                }
                if(!phoneNm.startsWith("1")){
                    etPhone.setError("Phone Number start from 1");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                startPhoneNumberVerification("+880"+phoneNm);
                break;
            default:
                break;
        }
    }

}
