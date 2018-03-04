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
import com.app.appathon.blooddonateapp.interfaces.ActionCallToUser;
import com.app.appathon.blooddonateapp.model.Inbox;

import java.util.ArrayList;

/**
 * Created by IMRAN on 8/20/2017.
 */

public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.ViewHolder> {

    private ArrayList<Inbox> inboxArrayList;
    private Activity activity;
    private ActionCallToUser callToUser;
    public InboxAdapter(ArrayList<Inbox> inboxArrayList, Activity activity) {
        this.inboxArrayList = inboxArrayList;
        this.activity = activity;
    }

    public void setCallToUser(ActionCallToUser callToUser){
        this.callToUser = callToUser;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inbox, parent, false);
        return new ViewHolder(rootView,callToUser);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Typeface ThemeFont = Typeface.createFromAsset(activity.getAssets(), "fonts/HelveticaNeue.ttf");

        holder.sender_name.setText(inboxArrayList.get(position).getSenderName());
        holder.msg_thumb.setText(String.valueOf(inboxArrayList.get(position).getSenderName().charAt(0)));
        holder.msg_time.setText(inboxArrayList.get(position).getSendTime());
        holder.msg_count.setText(String.valueOf(inboxArrayList.get(position).getCount()));
        holder.msg_body.setText(inboxArrayList.get(position).getMessage());

        holder.op1.setText(R.string.accept);

        holder.op1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callToUser.onCall(v, holder.getAdapterPosition());
            }
        });

        holder.msg_body.setTypeface(ThemeFont);
        holder.msg_time.setTypeface(ThemeFont);

    }


    @Override
    public int getItemCount() {
        return inboxArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView msg_body;
        private TextView sender_name;
        private TextView msg_thumb;
        private TextView msg_time;
        private TextView msg_count;
        private CardView cardView;
        private TextView op1;
        private ActionCallToUser callToUser;

        ViewHolder(View itemView, ActionCallToUser callToUser) {
            super(itemView);
            this.callToUser = callToUser;
            msg_body = (TextView) itemView.findViewById(R.id.msg_body);
            sender_name = (TextView) itemView.findViewById(R.id.sender_name);
            msg_thumb = (TextView) itemView.findViewById(R.id.msg_thumb);
            msg_time = (TextView) itemView.findViewById(R.id.sending_time);
            msg_count = (TextView) itemView.findViewById(R.id.msg_count);
            op1 = (TextView) itemView.findViewById(R.id.op1);
            cardView = (CardView) itemView.findViewById(R.id.inbox_card);
        }

        @Override
        public void onClick(View v) {
            callToUser.onCall(v, getAdapterPosition());
        }
    }
}
