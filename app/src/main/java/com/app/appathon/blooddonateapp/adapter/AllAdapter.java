package com.app.appathon.blooddonateapp.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.app.appathon.blooddonateapp.R;
import com.app.appathon.blooddonateapp.activities.UserProfileActivity;
import com.app.appathon.blooddonateapp.model.User;
import java.util.List;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by IMRAN on 10/22/2016.
 */

public class AllAdapter extends RecyclerView.Adapter<AllAdapter.ListHolder>{

    private List<User> arrayColumns;
    private Activity mContext;

    public AllAdapter(Activity context, List<User> arrayColumns){
        this.arrayColumns= arrayColumns;
        this.mContext = context;
    }
    @Override
    public ListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recycler_all,parent,false);
        return new ListHolder(rootView);
    }

    @Override
    public void onBindViewHolder(final ListHolder holder, final int position) {

        String date = arrayColumns.get(position).getLastDonate();
        final Typeface ThemeFont = Typeface.createFromAsset(mContext.getAssets(), "fonts/HelveticaNeue.ttf");
        final String id = arrayColumns.get(position).getId();

        holder.tName.setText(arrayColumns.get(position).getName());
        holder.tBloodGroup.setText(arrayColumns.get(position).getBloodGroup());
        holder.tArea.setText(arrayColumns.get(position).getAddress());
        holder.proImage.setImageResource(R.drawable.ic_person);

        holder.tDonateDate.setTypeface(ThemeFont);
        holder.tArea.setTypeface(ThemeFont);
        holder.tBloodGroup.setTypeface(ThemeFont);

        if (date.compareTo("Never")==0){
            holder.tDonateDate.setText(R.string.last_donated);
            holder.tBloodGroup.setBackgroundResource(R.drawable.round_bg);
        } else {
            try {
                String donateDATE = differenceBetweenDates(date);
                int dDate = Integer.parseInt(donateDATE.split("\\s")[0]);
                if (dDate == 1){
                    holder.tDonateDate.setText("Last Donated " + donateDATE);
                    holder.tBloodGroup.setBackgroundResource(R.drawable.round_red);
                }
                if(dDate <= 3){
                    holder.tDonateDate.setText("Last Donated " +donateDATE);
                    holder.tBloodGroup.setBackgroundResource(R.drawable.round_red);
                } else {
                    holder.tDonateDate.setText("Last Donated "+ donateDATE);
                    holder.tBloodGroup.setBackgroundResource(R.drawable.round_bg);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, UserProfileActivity.class);
                intent.putExtra("id", id);
                mContext.startActivity(intent);
                mContext.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayColumns.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(getItemCount() - position - 1);
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

    final class ListHolder extends RecyclerView.ViewHolder {
        private CoordinatorLayout cardView;
        private final TextView tName;
        private final TextView tBloodGroup;
        private final TextView tArea;
        private final TextView tDonateDate;
        private final ImageView proImage;

        ListHolder(View itemView) {
            super(itemView);

            cardView = (CoordinatorLayout) itemView.findViewById(R.id.card);
            tName = (TextView) itemView.findViewById(R.id.nName);
            tBloodGroup = (TextView) itemView.findViewById(R.id.nBlood);
            tArea = (TextView) itemView.findViewById(R.id.nArea);
            tDonateDate = (TextView) itemView.findViewById(R.id.nLastDonation);
            proImage = (ImageView) itemView.findViewById(R.id.imageView1);
        }
    }
}
