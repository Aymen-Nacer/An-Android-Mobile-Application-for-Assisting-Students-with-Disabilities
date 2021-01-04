package com.example.project;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class PersonalProfile extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private EditText name;
    private EditText UniversityID;
    private EditText PhoneNumber;
    private EditText DisabilityType;
    private EditText Age;
    private User user;
    private String Gender="";
    private String TypeOfDisability="";
    private String DegreeOfDisability="";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String LANGUAGE = "Ar";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_profile);
        getSupportActionBar().hide();

        user = (User) getIntent().getSerializableExtra("user");
        name = findViewById(R.id.name);
        UniversityID = findViewById(R.id.university_ID);
        PhoneNumber = findViewById(R.id.Phone_Number);
        DisabilityType = findViewById(R.id.DisabilityType);
        Age = findViewById(R.id.ageEditText);
        String[] typeOfDisability={"-","سمعية","بصرية","حركية","غير ذلك"};
        String[] degreeOfDisability={"-","شديدة","متوسطة","خفيفة"};
        String[] genders={"ذكر","أنثى"};


        if(loadData().equals("En")){
            typeOfDisability[1]="Hearing";
            typeOfDisability[2]="Vision";
            typeOfDisability[3]="Movement";
            typeOfDisability[4]="Other";
            degreeOfDisability[1]="Strong";
            degreeOfDisability[2]="Average";
            degreeOfDisability[3]="Little";
            genders[0]="Male";
            genders[1]="Female";
            TextView textView=findViewById(R.id.header);
            textView.setText("Wosool\nPersonal Profile");
            TextView textView1=findViewById(R.id.textName);
            textView1.setText(" Name");
            TextView textView2=findViewById(R.id.EditTextID);
            textView2.setText(" University ID/Employee ID");
            TextView textView3=findViewById(R.id.EditTextPhone);
            textView3.setText(" Phone Number");
            Button button=findViewById(R.id.save);
            button.setText("Save");
            button.setAllCaps(false);
            TextView textView4=findViewById(R.id.textView1);
            textView4.setText("Type Of Disability");
            TextView textView5=findViewById(R.id.degree);
            textView5.setText("Degree Of Disability");
            TextView textView6=findViewById(R.id.textViewGender);
            textView6.setText("Gender");
            TextView textView7=findViewById(R.id.textViewAge);
            textView7.setText("Age");

        }
        Spinner spinner1 = findViewById(R.id.spinner1);
        ArrayAdapter adapter1 = new ArrayAdapter(this,android.R.layout.simple_spinner_item,typeOfDisability);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter1);
        spinner1.setOnItemSelectedListener(this);

        Spinner spinner2 = findViewById(R.id.spinner2);
        ArrayAdapter adapter2 = new ArrayAdapter(this,android.R.layout.simple_spinner_item,degreeOfDisability);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);
        spinner2.setOnItemSelectedListener(this);

        Spinner spinner3 = findViewById(R.id.spinner3);
        ArrayAdapter adapter3 = new ArrayAdapter(this,android.R.layout.simple_spinner_item,genders);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner3.setAdapter(adapter3);
        spinner3.setOnItemSelectedListener(this);



        if(user.getName()!=null){
            name.setText(user.getName());
        }
        if(user.getPhoneN()!=null){
            PhoneNumber.setText(user.getPhoneN());
        }
        if(user.getUniversity_ID()!=null){
            UniversityID.setText(user.getUniversity_ID());
        }
        if(user.getType_of_Disability()!=null){
            switch (user.getType_of_Disability()) {
                case "-":
                    spinner1.setSelection(0);
                    break;
                case "سمعية":
                case "Hearing":
                    spinner1.setSelection(1);
                    break;
                case "بصرية":
                case "Vision":
                    spinner1.setSelection(2);
                    break;
                case "حركية":
                case "Movement":
                    spinner1.setSelection(3);
                    break;
                default:
                    spinner1.setSelection(4);
                    DisabilityType.setVisibility(View.VISIBLE);
                    DisabilityType.setText(user.getType_of_Disability());
                    break;
            }
        }
        if(user.getDegree_of_disability()!=null){
            switch (user.getDegree_of_disability()) {
                case "-":
                    spinner2.setSelection(0);
                    break;
                case "شديدة":
                case "Strong":
                    spinner2.setSelection(1);
                    break;
                case "متوسطة":
                case "Average":
                    spinner2.setSelection(2);
                    break;
                case "خفيفة":
                case "Little":
                    spinner2.setSelection(3);
                    break;
            }
        }
        if(user.getGender()!=null){
            if(user.getGender().equals("ذكر")||user.getGender().equals("Male")){
                spinner3.setSelection(0);
            }
            else
                spinner3.setSelection(1);
        }
        Age.setText(String.valueOf(user.getAge()));

        DisabilityType.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                TypeOfDisability=DisabilityType.getText().toString();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.spinner1:
                if(parent.getItemAtPosition(position).toString().equals("غير ذلك")||parent.getItemAtPosition(position).toString().equals("Other")){
                    DisabilityType.setVisibility(View.VISIBLE);
                    TypeOfDisability="";
                }
                else{
                    TypeOfDisability=parent.getItemAtPosition(position).toString();
                    DisabilityType.setVisibility(View.GONE);
                }
                break;
            case R.id.spinner2:
                DegreeOfDisability = parent.getItemAtPosition(position).toString();
                break;
            case R.id.spinner3:
                Gender = parent.getItemAtPosition(position).toString();
                break;
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    public void save(View view){
        db.collection("users").document(FirebaseAuth.getInstance().getUid())
                .update("name",name.getText().toString());
        db.collection("users").document(FirebaseAuth.getInstance().getUid())
                .update("phoneN",PhoneNumber.getText().toString());
        db.collection("users").document(FirebaseAuth.getInstance().getUid())
                .update("university_ID",UniversityID.getText().toString());
        if (TypeOfDisability.matches(""))
            db.collection("users").document(FirebaseAuth.getInstance().getUid())
                    .update("type_of_Disability","-");
        else
            db.collection("users").document(FirebaseAuth.getInstance().getUid())
                .update("type_of_Disability",TypeOfDisability);
        db.collection("users").document(FirebaseAuth.getInstance().getUid())
                .update("degree_of_disability",DegreeOfDisability);
        if(Age.getText().toString().equals("")) {
            db.collection("users").document(FirebaseAuth.getInstance().getUid())
                    .update("age",0);
        }
        else{
            db.collection("users").document(FirebaseAuth.getInstance().getUid())
                    .update("age",Integer.parseInt(Age.getText().toString()));
        }
        db.collection("users").document(FirebaseAuth.getInstance().getUid())
                .update("gender",Gender);
        if(loadData().equals("En")) {
            Toast.makeText(this,"Saved", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(this,"تم الحفظ بنجاح", Toast.LENGTH_SHORT).show();
        onBackPressed();
    }
    public String loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String currentLanguage = sharedPreferences.getString(LANGUAGE, "Ar");
        return currentLanguage;
    }
}