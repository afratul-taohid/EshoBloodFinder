package com.app.appathon.blooddonateapp.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.app.appathon.blooddonateapp.R;
import com.app.appathon.blooddonateapp.activities.MainActivity;
import com.app.appathon.blooddonateapp.adapter.AvailableAdapter;
import com.app.appathon.blooddonateapp.app.BloodApplication;
import com.app.appathon.blooddonateapp.database.FirebaseDatabaseHelper;
import com.app.appathon.blooddonateapp.helper.ConnectivityReceiver;
import com.app.appathon.blooddonateapp.helper.SimpleDividerItemDecoration;
import com.app.appathon.blooddonateapp.interfaces.ActionCallToUser;
import com.app.appathon.blooddonateapp.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class AvailableDonors extends Fragment
        implements FirebaseDatabaseHelper.AvailableDonorInterface, ConnectivityReceiver.ConnectivityReceiverListener,
        MainActivity.FragmentCommunicator {

    private FloatingActionButton mFabButton;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View rootView;
    private List<User> userArrayList = new ArrayList<>();
    private boolean isConnected;
    private AvailableAdapter adapter;
    private int FLAG = 0;
    private List<User> filteredModelList;
    private String phone;

    private static final int REQUEST_PHONE_CALL = 1;
    private FirebaseDatabaseHelper databaseHelper;

    public AvailableDonors() {
        // Required empty public constructor
    }

    public static AvailableDonors newInstance(){
        AvailableDonors fragment = new AvailableDonors();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        ((MainActivity)getContext()).fragmentCommunicator = this;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_available_donors, container, false);

        isConnected = ConnectivityReceiver.isConnected();

        mFabButton = (FloatingActionButton) rootView.findViewById(R.id.fab);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        mFabButton.setOnClickListener(onFabButtonListener);
        showCloudSign(isConnected);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.availableDonor);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));

        databaseHelper = new FirebaseDatabaseHelper(getActivity(), this);
        databaseHelper.getAvailableUserListData();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                FLAG = 0;
                setSwipeRefreshData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy > 0 && mFabButton.isShown()){
                    mFabButton.hide();
                }
                else if(dy < 0) {
                    mFabButton.show();
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                if ( newState == RecyclerView.SCROLL_STATE_IDLE){
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        // register connection status listener
        BloodApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        int activityValue = ((MainActivity) getContext()).someIntValue;
        if (activityValue == 0) {
            //show one view
        } else {
            // show other view
        }
    }

    public void onStop() {
        super.onStop();
        ComponentName component = new ComponentName(getContext(), ConnectivityReceiver.class);
        //Disable
        getContext().getPackageManager().setComponentEnabledSetting(
                component, PackageManager.COMPONENT_ENABLED_STATE_DISABLED , PackageManager.DONT_KILL_APP);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private View.OnClickListener onFabButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PopupMenu popup = new PopupMenu(getContext(), v);
            popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

            MenuPopupHelper menuHelper = new MenuPopupHelper(getContext(), (MenuBuilder) popup.getMenu(), v);
            menuHelper.setForceShowIcon(true);

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()){
                        case 0:
                            setRecyclerViewAdapter(filterByBlood(userArrayList, item.toString()), false);
                            break;
                        case 1:
                            setRecyclerViewAdapter(filterByBlood(userArrayList, item.toString()), false);
                            break;
                        case 2:
                            setRecyclerViewAdapter(filterByBlood(userArrayList, item.toString()), false);
                            break;
                        case 3:
                            setRecyclerViewAdapter(filterByBlood(userArrayList, item.toString()), false);
                            break;
                        case 4:
                            setRecyclerViewAdapter(filterByBlood(userArrayList, item.toString()), false);
                            break;
                        case 5:
                            setRecyclerViewAdapter(filterByBlood(userArrayList, item.toString()), false);
                            break;
                        case 6:
                            setRecyclerViewAdapter(filterByBlood(userArrayList, item.toString()), false);
                            break;
                        case 7:
                            setRecyclerViewAdapter(filterByBlood(userArrayList, item.toString()), false);
                            break;
                        default:
                            break;
                    }
                    return true;
                }
            });
            menuHelper.show();
        }
    };

    private List<User> filter(List<User> models, String query) {
        FLAG = 1;
        String lowerCaseQuery = query.toLowerCase();
        filteredModelList = new ArrayList<>();
        for (User model : models) {
            String add = model.address.toLowerCase() + " " + model.bloodGroup.toLowerCase();
            String[] words = add.replace(",", "").split("\\s");

            //check duplicate word
            for (int i = 0; i < words.length; i++) {
                for (int j = 0; j < words.length; j++) {
                    if (words[i].equals(words[j])) {
                        if (i != j)
                            words[j] = ""; // remove duplicate
                    }
                }
            }

            String wordAdd = Arrays.toString(words).replace(",","").replace("[", "").replace("]", "");
            List<String> queryWord = new ArrayList<>();

            for (String w : wordAdd.split("\\s")){
                if (lowerCaseQuery.contains(w)) {
                    queryWord.add(w);
                }
            }

            String replace = getPlainString(queryWord).replaceAll("\\s", "");
            if (lowerCaseQuery.replaceAll("\\s", "").compareTo(replace)==0) {
                filteredModelList.add(model);
            } else {
                if (lowerCaseQuery.replaceAll("\\s", "").compareTo(
                        rearrangeQuery(lowerCaseQuery, getPlainString(queryWord)))==0){
                    filteredModelList.add(model);
                    break;
                }
            }
        }
        return filteredModelList;
    }

    private String rearrangeQuery(String lowerCaseQuery, String data) {
        String[] queryArray = lowerCaseQuery.split("\\s");
        String[] s = data.split("\\s");

        for (int i=0; i<queryArray.length; i++){
            if (i< s.length){
                for (int j=0; j<s.length; j++){
                    if (i != j){
                        if (s[j].contains(queryArray[i])){
                            String d = s[i];
                            s[i] = s[j];
                            s[j] = d;
                        }
                    }
                }
            }
        }

        return Arrays.toString(s).replace(",","").replace("[", "").replace("]", "").replaceAll("\\s", "");
    }

    private String getPlainString(List<String> s){
        return s.toString().replace(",","").replace("[", "").replace("]", "");
    }

    private List<User> filterByBlood(List<User> models, String query) {

        if(FLAG == 1){
            models = filteredModelList;
            FLAG = 0;
        }

        final String lowerCaseQuery =  query.toLowerCase();
        final List<User> filteredModelList = new ArrayList<>();
        for (User model : models) {
            final String text = model.bloodGroup.toLowerCase();
            if (text.equals(lowerCaseQuery)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    private void setSwipeRefreshData(){

        setRecyclerViewAdapter(userArrayList,true);
    }

    private void setRecyclerViewAdapter(List<User> arrayList, boolean isRefreshing){
        showCloudSign(isConnected);
        if (!isRefreshing){
            adapter = new AvailableAdapter(getActivity(),arrayList);
            recyclerView.setAdapter(adapter);
        } else {
            databaseHelper.getAvailableUserListData();
            adapter.refreshList(userArrayList);
            recyclerView.setAdapter(adapter);
        }
        adapter.setCallToUser(onItemCallToUser);
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    private void snackBarView(String message) {
        int color = Color.RED;
        int TIME_OUT = Snackbar.LENGTH_INDEFINITE;

        Snackbar snackbar = Snackbar
                .make(rootView, message, TIME_OUT);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected){
            snackBarView("No Internet Access!");
        }
    }

    private void showCloudSign(boolean isConnected){
        if (isConnected){
            swipeRefreshLayout.setRefreshing(true);
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void passDataToFragment(String value) {
        if (TextUtils.isEmpty(value)) {

        } else {
            setRecyclerViewAdapter(filter(userArrayList, value), false);
        }
    }

    @Override
    public void getAvailableDonorInfo(String id, String email, List<User> users) {
        swipeRefreshLayout.setRefreshing(true);
        if (userArrayList.size()>0) {
            userArrayList.clear();
        }
        for (User user : users){
            userArrayList.add(user);
        }
        setRecyclerViewAdapter(userArrayList, false);
    }

    @Override
    public void onFirebaseInternalError(String error) {
        snackBarView(error);
        swipeRefreshLayout.setRefreshing(false);
    }

    private ActionCallToUser onItemCallToUser = new ActionCallToUser() {
        @Override
        public void onCall(View v, int position) {
            final int pos = position;
            String id = userArrayList.get(position).getId();
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("users").child(id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("security")) {
                        DataSnapshot data = dataSnapshot.child("security");
                        boolean isHidden = Boolean.parseBoolean(data.child("phoneHidden").getValue().toString());
                        if (isHidden) {
                            phone = "Number is in hidden mode";
                            dialogView(phone, false);
                        } else {
                            phone = userArrayList.get(pos).getPhone();
                            dialogView(phone, true);
                        }
                    } else {
                        phone = userArrayList.get(pos).getPhone();
                        dialogView(phone, true);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    };

    private void dialogView(final String phone, boolean b) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getContext())
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
                });

        MaterialDialog dialog = builder.build();
        MDButton positiveButton = dialog.getActionButton(DialogAction.POSITIVE);
        positiveButton.setEnabled(b);
        dialog.show();
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
