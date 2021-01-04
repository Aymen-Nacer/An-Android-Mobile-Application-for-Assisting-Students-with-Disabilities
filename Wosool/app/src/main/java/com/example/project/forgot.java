package com.example.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class forgot extends AppCompatActivity {
    private EditText emailEditText;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String LANGUAGE = "Ar";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);
        getSupportActionBar().hide();

        emailEditText = findViewById(R.id.emailEditText);
        if(loadData().equals("En")){
            emailEditText.setHint("Email");
            emailEditText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.person, 0, 0, 0);
            Button button1=findViewById(R.id.forgot);
            button1.setText("Reset Password");
            button1.setAllCaps(false);

        }
    }
    public void reset(View view){
        if(emailEditText.getText().toString().trim().length() == 0) {
            if(loadData().equals("En")) {
                Toast.makeText(this,"Email is empty", Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(this, "البريد الإلكتروني فارغ", Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String emailAddress = emailEditText.getText().toString();
        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            if(loadData().equals("En")) {
                                Toast.makeText(getApplicationContext(),"Reset password has been sent", Toast.LENGTH_SHORT).show();
                            }
                            else
                                Toast.makeText(getApplicationContext(), "تم إرسال رابط إعادة تعيين كلمة المرور", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                        else {
                            if(loadData().equals("En")) {
                                Toast.makeText(getApplicationContext(),"Email is wrong", Toast.LENGTH_SHORT).show();
                            }
                            else
                                Toast.makeText(getApplicationContext(), "البريد الإلكتروني خطأ", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public String loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String currentLanguage = sharedPreferences.getString(LANGUAGE, "Ar");
        return currentLanguage;

    }
}