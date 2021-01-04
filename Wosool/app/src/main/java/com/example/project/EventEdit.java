package com.example.project;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.Date;


public class EventEdit extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference eventRef;
    Button date_timeButton;
    EditText nameTextView;
    Event event;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String LANGUAGE = "Ar";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit);
        getSupportActionBar().hide();

        nameTextView = findViewById(R.id.name);
        date_timeButton = findViewById(R.id.date_time);
        event = (Event) getIntent().getSerializableExtra("event");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd                                      hh:mm a");
        date_timeButton.setText(formatter.format(event.getTime()));
        nameTextView.setText(event.getName());
        date_timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleDateButton();
            }
        });
        if(loadData().equals("En")) {
            nameTextView.setHint("Name of the reminder");
            Button button=findViewById(R.id.save);
            Button button1=findViewById(R.id.remove);
            button.setAllCaps(false);
            button1.setAllCaps(false);
            button1.setText("Remove");
            button.setText("Save");

        }
    }

    private void handleDateButton() {

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                event.getTime().setYear(year-1900);
                event.getTime().setMonth(month);
                event.getTime().setDate(date);
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd                                      hh:mm a");
                date_timeButton.setText(formatter.format(event.getTime()));
                handleTimeButton();
            }
        }, event.getTime().getYear(), event.getTime().getMonth(), event.getTime().getDate());

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 10000);
        datePickerDialog.show();
    }

    private void handleTimeButton() {
        boolean is24HourFormat = DateFormat.is24HourFormat(this);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                event.getTime().setHours(hour);
                event.getTime().setMinutes(minute);
                event.getTime().setSeconds(0);
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd                                      hh:mm a");
                date_timeButton.setText(formatter.format(event.getTime()));
            }
        }, event.getTime().getHours(), event.getTime().getMinutes(), is24HourFormat);

        timePickerDialog.show();

    }

    public void save(View view){
        if(nameTextView.getText().toString().isEmpty()){
            if(loadData().equals("En")) {
                Toast.makeText(getApplicationContext(),"Name is empty", Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(getApplicationContext(),"الإسم فارغ",Toast.LENGTH_LONG).show();
            return;
        }
        event.setName(nameTextView.getText().toString());
        eventRef=db.document(getIntent().getStringExtra("path"));
        eventRef.set(event);
        cancelAlarm(event.getEventID());
        Date date1=new Date();
        if(event.getTime().after(date1)) {
            startAlarm(event);
        }
        if(loadData().equals("En")) {
            Toast.makeText(EventEdit.this,"Reminder updated", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(EventEdit.this,"تم تعديل التذكير بنجاح", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(EventEdit.this, Calendar.class);
        startActivity(intent);
        finish();
    }

    private void startAlarm(Event event){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplication(), AlertReceiver.class);
        intent.putExtra("name",event.getName());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplication(),event.getEventID(), intent, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP,event.getTime().getTime(), pendingIntent);
    }

    private void cancelAlarm(int id) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id, intent, 0);
        alarmManager.cancel(pendingIntent);
    }

    public void delete(View view){
        cancelAlarm(event.getEventID());
        eventRef=db.document(getIntent().getStringExtra("path"));
        eventRef.delete();
        if(loadData().equals("En")) {
            Toast.makeText(this,"Reminder has been removed", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(EventEdit.this,"تم إزالة التذكير بنجاح", Toast.LENGTH_SHORT).show();
        onBackPressed();
    }
    public String loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String currentLanguage = sharedPreferences.getString(LANGUAGE, "Ar");
        return currentLanguage;
    }
    public void onBackPressed() {
        Intent intent = new Intent(EventEdit.this, Calendar.class);
        startActivity(intent);
        finish();
    }
}