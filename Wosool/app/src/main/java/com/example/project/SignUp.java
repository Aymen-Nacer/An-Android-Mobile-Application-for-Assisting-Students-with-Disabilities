package com.example.project;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rilixtech.Country;
import com.rilixtech.CountryCodePicker;

import java.util.Date;

public class SignUp extends AppCompatActivity {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText emailEditText;
    private EditText name;
    private EditText passwordEditText;
    private EditText phoneN;
    LoadingDialog loadingDialog;
    CountryCodePicker ccp;
    private String codeC="966";
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String LANGUAGE = "Ar";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().hide();

        emailEditText = findViewById(R.id.emailEditText);
        loadingDialog=new LoadingDialog(SignUp.this);
        name = findViewById(R.id.name);
        passwordEditText = findViewById(R.id.passwordEditText);
        phoneN = findViewById(R.id.number);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected(Country selectedCountry) {
                codeC=selectedCountry.getPhoneCode();
            }
        });
        if(loadData().equals("En")){
            emailEditText.setHint("Email");
            emailEditText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.person, 0, 0, 0);
            passwordEditText.setHint("Password");
            passwordEditText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock, 0, 0, 0);
            name.setHint("Name");
            name.setCompoundDrawablesWithIntrinsicBounds(R.drawable.id, 0, 0, 0);
            phoneN.setHint("Phone Number");
            phoneN.setCompoundDrawablesWithIntrinsicBounds(R.drawable.phone, 0, 0, 0);
            Button button1=findViewById(R.id.signUp);
            button1.setText("Sign Up");
            button1.setAllCaps(false);
            passwordEditText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            phoneN.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        }
    }

    public void signUp(View view){
        if(emailEditText.getText().toString().trim().length() == 0) {
            if(loadData().equals("En")) {
                Toast.makeText(this,"Email is empty", Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(this, "البريد الالكتروني فارغ", Toast.LENGTH_SHORT).show();
            return;
        }
        if(passwordEditText.getText().toString().trim().length() <6) {
            if(loadData().equals("En")) {
                Toast.makeText(this,"Password less then 6", Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(this, "كلمة المرور اقل من 6", Toast.LENGTH_SHORT).show();
            return;
        }
        if(name.getText().toString().trim().length() ==0) {
            if(loadData().equals("En")) {
                Toast.makeText(this,"Name is empty", Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(this, "الأسم فارغ", Toast.LENGTH_SHORT).show();
            return;
        }
        if(phoneN.getText().toString().trim().length() ==0) {
            if(loadData().equals("En")) {
                Toast.makeText(this,"Phone number is empty", Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(this, "رقم الجوال فارغ", Toast.LENGTH_SHORT).show();
            return;
        }
        loadingDialog.startLoadingDialog();
        mAuth.createUserWithEmailAndPassword(emailEditText.getText().toString(), passwordEditText.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            User user=new User(name.getText().toString(),emailEditText.getText().toString(),codeC+phoneN.getText().toString(),0,"","-","-",15,"ذكر");
                            db.collection("users").document(mAuth.getUid()).set(user);
                            addEvent("Mid-year vacation",new Date(2020-1900,12-1,30));
                            addEvent("Second semester",new Date(2021-1900,1-1,17));
                            addEvent("Eid al-Fitr holiday",new Date(2021-1900,4-1,28));
                            addEvent("Resuming classes after mid-semester break",new Date(2021-1900,5-1,17));
                            addEvent("Beginning of final examinations",new Date(2021-1900,5-1,22));
                            addEvent("End of final examinations\n",new Date(2021-1900,6-1,2));

                            if(loadData().equals("En")) {
                                Toast.makeText(SignUp.this,"Signed up", Toast.LENGTH_SHORT).show();
                            }
                            else
                                Toast.makeText(SignUp.this, "تم التسجيل بنجاح", Toast.LENGTH_SHORT).show();
                            loadingDialog.dismissDialog();
                            Intent intent = new Intent(SignUp.this, Homepage.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            loadingDialog.dismissDialog();
                            if(loadData().equals("En")) {
                                Toast.makeText(SignUp.this,"Email is wrong or used", Toast.LENGTH_SHORT).show();
                            }
                            else
                                Toast.makeText(SignUp.this,"البريد الالكتروني مستعمل او خاطئ", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public String loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String currentLanguage = sharedPreferences.getString(LANGUAGE, "Ar");
        return currentLanguage;
    }
    private void addEvent(String name, Date date){
        DocumentReference newEventRef = db.collection("users").document(FirebaseAuth.getInstance().getUid()).collection("Events").document();
        DocumentReference userRef = db.collection("users").document(FirebaseAuth.getInstance().getUid());
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                int eventID=user.getNumOfEvent();
                Event event = new Event(newEventRef.getId(),eventID,name,date);
                userRef.update("numOfEvent", FieldValue.increment(1));
                newEventRef.set(event); // add to firebase

                Date date1=new Date();
                if(date.after(date1)) {
                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(getApplication(), AlertReceiver.class);
                    intent.putExtra("name", event.getName());
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplication(), eventID, intent, 0);
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, event.getTime().getTime(), pendingIntent);
                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SignUp.this, Login.class);
        startActivity(intent);
        finish();
    }
}