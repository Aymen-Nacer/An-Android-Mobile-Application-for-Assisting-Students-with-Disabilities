package com.example.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NoLogin extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    FusedLocationProviderClient fusedLocationProviderClient;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String LANGUAGE = "Ar";
    private long pressedTime;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_login);
        getSupportActionBar().hide();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if(loadData().equals("En")) {
            Button button2=findViewById(R.id.b2);
            Button button3=findViewById(R.id.b3);
            Button button4=findViewById(R.id.b4);
            Button button6=findViewById(R.id.b6);
            button2.setAllCaps(false);
            button3.setAllCaps(false);
            button4.setAllCaps(false);
            button6.setAllCaps(false);
            button2.setText("  Maps");
            button3.setText("  Dialog");
            button4.setText("  Emergency call");
            button6.setText("  Contact Us");


            button2.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.map, 0);
            button3.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.communicate, 0);
            button4.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.video_call, 0);
            button6.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.contact, 0);
        }
    }


    public void map(View view) {
        Intent intent = new Intent(this, HomeMaps.class);
        startActivity(intent);
    }

    public void communicate(View view) {
        Intent intent = new Intent(this, Conversation.class);
        startActivity(intent);
    }

    public void assistanceVideoCall(View view) {
        getlocation();
        Intent duo = new Intent("com.google.android.apps.tachyon.action.CALL");
        duo.setData(Uri.parse("tel: " + "0114696414"));
        duo.setPackage("com.google.android.apps.tachyon");
        startActivity(Intent.createChooser(duo, "Duo is not installed."));
    }

    public void feedbackForm(View view) {
        Intent intent = new Intent(this, FeedBack.class);
        startActivity(intent);
    }
    public void changeLanguage(View view){
        String currentLanguage=loadData();
        saveData(currentLanguage);
        // to reload activity
        finish();
        startActivity(getIntent());
    }

    @Override
    public void onBackPressed() {

        if (pressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            finish();
        } else {
            if(loadData().equals("En"))
                Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getBaseContext(), "إضغط مره اخرى للخروج", Toast.LENGTH_SHORT).show();
        }
        pressedTime = System.currentTimeMillis();
    }

    void getlocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getLastLocation();
        } else {
            askLocationPermission();
        }
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();

        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    //We have a location
                    Log.d("TAG", "onSuccess: " + location.toString());
                    Log.d("TAG", "onSuccess: " + location.getLatitude());
                    Log.d("TAG", "onSuccess: " + location.getLongitude());
                    String googleMap="http://www.google.com/maps/place/"+location.getLatitude()+","+location.getLongitude();
                    Map<String, String> data = new HashMap<>();
                    data.put("Location", googleMap);
                    Date date=new Date();
                    db.collection("noneUsers").document("("+(date.getYear()+1900)+"-"+(date.getMonth()+1)+"-"+date.getDate()+")"+"("+date.getHours()+"-"+date.getMinutes()+"-"+date.getSeconds()+")").set(data, SetOptions.merge());
                } else  {
                    Log.d("TAG", "onSuccess: Location was null...");
                }
            }
        });

        locationTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("TAG", "onFailure: " + e.getLocalizedMessage() );
            }
        });
    }

    private void askLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.d("TAG", "askLocationPermission: you should show an alert dialog...");
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
            } else {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1001) {
            if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                getLastLocation();
            } else {
                //Permission not granted
            }
        }
    }
    public void saveData(String currentLanguage) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(currentLanguage.equals("Ar"))
            editor.putString(LANGUAGE,"En");
        else
            editor.putString(LANGUAGE,"Ar");
        editor.apply();
    }
    public String loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String currentLanguage = sharedPreferences.getString(LANGUAGE, "Ar");
        return currentLanguage;
    }
}
