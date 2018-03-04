package com.app.appathon.blooddonateapp.fragments;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.app.appathon.blooddonateapp.R;
import com.app.appathon.blooddonateapp.adapter.InboxAdapter;
import com.app.appathon.blooddonateapp.database.FirebaseDatabaseHelper;
import com.app.appathon.blooddonateapp.interfaces.ActionCallToUser;
import com.app.appathon.blooddonateapp.model.Inbox;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class IncomingFragment extends Fragment implements FirebaseDatabaseHelper.IncomingInboxInterface {

    private RecyclerView incomingView;
    private ArrayList<Inbox> incomingList = new ArrayList<>();
    private static final int REQUEST_PHONE_CALL = 1;
    private String phone;

    public IncomingFragment() {
        // Required empty public constructor
    }

    public static IncomingFragment newInstance(){
        IncomingFragment fragment = new IncomingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_incoming, container, false);

        incomingView = (RecyclerView) rootView.findViewById(R.id.inbox_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        incomingView.setLayoutManager(layoutManager);

        FirebaseDatabaseHelper databaseHelper = new FirebaseDatabaseHelper(getActivity(), this);
        databaseHelper.getUserIncomingInboxData();
        return rootView;
    }

    @Override
    public void getIncomingInboxData(String id, String email, List<Inbox> inboxes) {
        InboxAdapter adapter = new InboxAdapter(incomingList, getActivity());
        if (incomingList.size()>0)
            incomingList.clear();

        incomingList.addAll(inboxes);
        incomingView.setAdapter(adapter);
        adapter.setCallToUser(onItemCallToUser);
    }

    @Override
    public void onFirebaseInternalError(String error) {

    }

    private ActionCallToUser onItemCallToUser = new ActionCallToUser() {
        @Override
        public void onCall(View v, int position) {
            phone = incomingList.get(position).getSenderPhone();
            new MaterialDialog.Builder(getContext())
                    .title(phone)
                    .icon(ContextCompat.getDrawable(getContext(), R.drawable.ic_phone_round))
                    .positiveText("Call")
                    .backgroundColorRes(R.color.dialog_color)
                    .titleColorRes(android.R.color.white)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            try {
                                Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                                phoneIntent.setData(Uri.parse("tel:" + phone));
                                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
                                } else {
                                    startActivity(phoneIntent);
                                }
                            } catch (android.content.ActivityNotFoundException | SecurityException ex) {
                                Toast.makeText(getContext(),
                                        "Call failed, please try again later!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .show();
        }
    };

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
                        Toast.makeText(getContext(),
                                "Call failed, please try again later!", Toast.LENGTH_SHORT).show();
                    }
                } else {

                }
                return;
            }
        }
    }
}
