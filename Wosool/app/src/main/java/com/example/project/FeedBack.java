package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class FeedBack extends AppCompatActivity {
    private EditText name;
    private EditText title;
    private EditText description;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String LANGUAGE = "Ar";
    /* ////// This store the feedback in the database

    private Suggestion suggestion;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        getSupportActionBar().hide();

        name=findViewById(R.id.name);
        title=findViewById(R.id.title);
        description=findViewById(R.id.description);
        if(loadData().equals("En")) {
            TextView textView=findViewById(R.id.textViewName);
            TextView textView1=findViewById(R.id.textViewTitle);
            TextView textView2=findViewById(R.id.textViewDescription);
            TextView textView3=findViewById(R.id.textView3);
            TextView textView4=findViewById(R.id.feedbackHeader);
            textView.setText(" Name");
            textView1.setText(" Title");
            textView2.setText(" Description");
            textView3.setText("Or");
            textView4.setText("Wosool\nFeedback");
            Button button1=findViewById(R.id.send);
            Button button2=findViewById(R.id.callUs);
            button1.setAllCaps(false);
            button2.setAllCaps(false);
            button1.setText("Send");
            button2.setText("Call Us");

        }
    }

    public void submitForm(View view){
        if(name.getText().toString().trim().length() == 0) {
            if(loadData().equals("En")) {
                Toast.makeText(this,"Name is empty", Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(this, "الإسم فارغ", Toast.LENGTH_SHORT).show();
            return;
        }
        if(title.getText().toString().trim().length() == 0) {
            if(loadData().equals("En")) {
                Toast.makeText(this,"Title is empty", Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(this, "العنوان فارغ", Toast.LENGTH_SHORT).show();
            return;
        }
        if(description.getText().toString().trim().length() == 0) {
            if(loadData().equals("En")) {
                Toast.makeText(this,"Description is empty", Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(this, "الوصف فارغ", Toast.LENGTH_SHORT).show();
            return;
        }
                /* ////// This store the feedback in the database
        suggestion=new Suggestion(name.getText().toString(),title.getText().toString(),description.getText().toString());
        db.collection("users").document(FirebaseAuth.getInstance().getUid()).collection("FeedBacks").document().set(suggestion);
        onBackPressed();
         */
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"accessibility@ksu.edu.sa"});
        intent.putExtra(Intent.EXTRA_SUBJECT, title.getText().toString());
        intent.putExtra(Intent.EXTRA_TEXT,description.getText().toString()+"\n"+name.getText().toString());
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
    public void callUs(View view){
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "0114696414"));
        startActivity(intent);
        onBackPressed();
    }
    public String loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String currentLanguage = sharedPreferences.getString(LANGUAGE, "Ar");
        return currentLanguage;
    }
}
