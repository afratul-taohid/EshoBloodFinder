package com.app.appathon.blooddonateapp.adapter;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.appathon.blooddonateapp.R;
import com.app.appathon.blooddonateapp.model.Inbox;
import com.app.appathon.blooddonateapp.model.User;

import java.util.ArrayList;

/**
 * Created by IMRAN on 8/25/2017.
 */

public class OutgoingAdapter extends RecyclerView.Adapter<OutgoingAdapter.ViewHolder> {

    private ArrayList<Inbox> inboxArrayList;
    private Activity activity;
    private ArrayList<User> userArrayList;

    public OutgoingAdapter(ArrayList<User> userArrayList, ArrayList<Inbox> inboxArrayList, Activity activity) {
        this.inboxArrayList = inboxArrayList;
        this.activity = activity;
        this.userArrayList = userArrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_outbox, parent, false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Typeface ThemeFont = Typeface.createFromAsset(activity.getAssets(), "fonts/HelveticaNeue.ttf");

        holder.sender_name.setText(userArrayList.get(position).getName());
        holder.msg_thumb.setText(String.valueOf(userArrayList.get(position).getName().charAt(0)));
        holder.msg_time.setText(inboxArrayList.get(position).getSendTime());
        holder.msg_count.setText(String.valueOf(inboxArrayList.get(position).getCount()));
        holder.msg_body.setText(inboxArrayList.get(position).getMessage());

//        holder.op1.setText(R.string.rating);
//        holder.op2.setText(R.string.fake);

        holder.msg_body.setTypeface(ThemeFont);
        holder.msg_time.setTypeface(ThemeFont);
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView msg_body;
        private TextView sender_name;
        private TextView msg_thumb;
        private TextView msg_time;
        private TextView msg_count;
        private CardView cardView;
        private TextView op1;
        private TextView op2;

        ViewHolder(View itemView) {
            super(itemView);

            msg_body = (TextView) itemView.findViewById(R.id.msg_body);
            sender_name = (TextView) itemView.findViewById(R.id.sender_name);
            msg_thumb = (TextView) itemView.findViewById(R.id.msg_thumb);
            msg_time = (TextView) itemView.findViewById(R.id.sending_time);
            msg_count = (TextView) itemView.findViewById(R.id.msg_count);
            cardView = (CardView) itemView.findViewById(R.id.inbox_card);
//            op1 = (TextView) itemView.findViewById(R.id.op1);
//            op2 = (TextView) itemView.findViewById(R.id.op2);
        }
    }
}
