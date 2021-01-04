package com.example.project;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.Date;


public class StartUpReceiver extends BroadcastReceiver {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    public void onReceive(Context context, Intent intent) {

        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            db.collection("users").document(FirebaseAuth.getInstance().getUid()).collection("Events")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                    Event event=documentSnapshot.toObject(Event.class);
                                    Date date1=new Date();
                                    if(event.getTime().after(date1)) {
                                        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                                        Intent intent = new Intent(context, AlertReceiver.class);
                                        intent.putExtra("name",event.getName());
                                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,event.getEventID(), intent, 0);
                                        alarmManager.setExact(AlarmManager.RTC_WAKEUP,event.getTime().getTime(), pendingIntent);
                                    }
                                    }
                            } else {
                                Log.d("h", "Error getting events: ", task.getException());
                            }
                        }
                    });
        }

    }
}
