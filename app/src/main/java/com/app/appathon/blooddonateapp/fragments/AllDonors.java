package com.app.appathon.blooddonateapp.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.appathon.blooddonateapp.R;
import com.app.appathon.blooddonateapp.adapter.AllAdapter;
import com.app.appathon.blooddonateapp.database.FirebaseDatabaseHelper;
import com.app.appathon.blooddonateapp.helper.SimpleDividerItemDecoration;
import com.app.appathon.blooddonateapp.model.User;

import java.util.ArrayList;
import java.util.List;

public class AllDonors extends Fragment implements FirebaseDatabaseHelper.AllDonorInterface {

    private RecyclerView recyclerView;
    private List<User> userArrayList = new ArrayList<>();

    public AllDonors() {
        // Required empty public constructor
    }

    public static AllDonors newInstance(){
        AllDonors fragment = new AllDonors();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_all_donors, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.allDonor);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));

        FirebaseDatabaseHelper firebaseDatabaseHelper = new FirebaseDatabaseHelper(getActivity(), this);
        firebaseDatabaseHelper.getAllUserListData();

        return rootView;
    }

    private void setRecyclerViewAdapter(AllAdapter adapter){
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void getAllDonorInfo(String id, String email, List<User> users) {
        userArrayList = users;
        setRecyclerViewAdapter(new AllAdapter(getActivity(), userArrayList));
    }

    @Override
    public void onFirebaseInternalError(String error) {

    }
}
