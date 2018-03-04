package com.app.appathon.blooddonateapp.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import com.app.appathon.blooddonateapp.model.PlaceAPI;

import java.util.ArrayList;

/**
 * Created by IMRAN on 7/19/2017.
 */

public class PlacesAutoCompleteAdapter extends ArrayAdapter<String>{
    private ArrayList<String> resultList;
    private Context mContext;
    private int mResource;

    private PlaceAPI mPlaceAPI = new PlaceAPI();

    public PlacesAutoCompleteAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);

        this.mContext = context;
        this.mResource = resource;
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return resultList.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null){
                    resultList = mPlaceAPI.autocomplete(constraint.toString());

                    filterResults.values = resultList;
                    filterResults.count = resultList.size();
                    notifyDataSetChanged();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0){
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
