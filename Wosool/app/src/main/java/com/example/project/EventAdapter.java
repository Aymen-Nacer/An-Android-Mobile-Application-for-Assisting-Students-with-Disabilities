package com.example.project;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;

public class EventAdapter extends FirestoreRecyclerAdapter<Event,EventAdapter.EventHolder> {
    private OnItemClickListener listener;
    private Context mContext;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String LANGUAGE = "Ar";
    public void setContext(Context mContext) {
        this.mContext = mContext;
    }

    public EventAdapter(@NonNull FirestoreRecyclerOptions<Event> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull EventHolder holder, int position, @NonNull Event model) {
        Long remainingTime=model.getTime().getTime()-new Date().getTime();
            holder.name.setText(model.getName());
            if(model.getName().matches("^[a-zA-Z]+$")){
                holder.name.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            }
            if(remainingTime<=0){
                holder.daysLeft.setText("");
            }
            else if(remainingTime<=3600000){
                if(loadData().equals("En")){
                    holder.daysLeft.setText("Less then a hour");
                }
                else
                    holder.daysLeft.setText("أقل من ساعة");
            }
            else if(remainingTime<=86400000){
                if(loadData().equals("En")){
                    holder.daysLeft.setText("("+String.valueOf(remainingTime/(1000*60*60))+" Hours"+")");
                }
                else
                    holder.daysLeft.setText("("+String.valueOf(remainingTime/(1000*60*60))+" ساعات"+")");
            }
            else {
                if(loadData().equals("En")){
                    holder.daysLeft.setText("("+String.valueOf(remainingTime/(1000*60*60*24))+" days "+String.valueOf((remainingTime%(1000*60*60*24))/(1000*60*60))+" hours"+")");
                }
                else
                    holder.daysLeft.setText("("+String.valueOf(remainingTime/(1000*60*60*24))+" ايام "+String.valueOf((remainingTime%(1000*60*60*24))/(1000*60*60))+" ساعات"+")");
            }

    }

    @NonNull
    @Override
    public EventHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_card,
                parent, false);
        return new EventHolder(v);
    }

    public void deleteEvent(int position){
       int eventID=getSnapshots().getSnapshot(position).getLong("eventID").intValue();

        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(mContext, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, eventID, intent, 0);
        alarmManager.cancel(pendingIntent);

        getSnapshots().getSnapshot(position).getReference().delete();
    }

    class EventHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView daysLeft;
        public EventHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            daysLeft = itemView.findViewById(R.id.daysLeft);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    public String loadData() {
        SharedPreferences sharedPreferences =  mContext.getSharedPreferences(SHARED_PREFS, 0);
        String currentLanguage = sharedPreferences.getString(LANGUAGE, "Ar");
        return currentLanguage;
    }
}
