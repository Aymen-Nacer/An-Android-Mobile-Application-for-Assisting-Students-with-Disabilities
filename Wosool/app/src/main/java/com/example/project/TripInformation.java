package com.example.project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project.R;
import com.google.maps.android.SphericalUtil;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.Locale;


public class TripInformation extends AppCompatActivity {

// primary attributes
    private  String name;
    private  String type;
    private  int distance = -1;
    private  String direction;
    private  double lat = -1;
    private  double lng = -1;
    private int delayBetweenDistanceDirectionTTS = 15000; // delay for text to speech
    private TextToSpeech mTTS;
    private String LANGUAGE = "Ar";

    // helper attributes
    public boolean trigger;
    Handler handler = new Handler();
    Runnable runnable;
    int delayAfterName;
    TextView distanceText;
    public static final String SHARED_PREFS = "sharedPrefs";
    String sp;
    String url;
    public int flag;
    public int Case;
    MediaPlayer mediaPlayer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_information);
        // translation
        if(loadData().equals("En")){
                getSupportActionBar().setTitle("Trip Information");
        }
        else {
                getSupportActionBar().setTitle("معلومات الرحلة");
                Button carBtn = findViewById(R.id.CarBtn);
                Button WalkBtn = findViewById(R.id.WalkBtn);
                carBtn.setText("سيارة");
                WalkBtn.setText("سيرا");
        }


        // change color of action bar
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blueWosool)));

        TextView nameText = findViewById(R.id.nameText);
        TextView typeText = findViewById(R.id.typeText);
        distanceText = findViewById(R.id.distanceText);

        // grab data from the previous activity
        name = getIntent().getStringExtra("PlaceName");
        type = getIntent().getStringExtra("typeOfPlace");
        distance = getIntent().getIntExtra("Distance" , -1);
        direction = getIntent().getStringExtra("Direction");
        lat = getIntent().getDoubleExtra("Latitude" , -1);
        lng = getIntent().getDoubleExtra("Longitude", -1);





        // setting the text views
        if (name != null) {
                    nameText.setText(name);
        }
        else {
                    nameText.setText("");
        }

        if (type != null) {
                    typeText.setText(type);
        }
        else {
                    typeText.setText("");
        }

        if (distance != -1 && direction != null) {

                    // computing direction to a destinative relative to user orientation
                    Location destinationLoc = new Location("service Provider");
                    destinationLoc.setLatitude(lat); //kaaba latitude setting
                    destinationLoc.setLongitude(lng); //kaaba longitude setting

                    Float bearing = HomeMaps.mLastKnownLocation.bearingTo(destinationLoc);
                    Float heading = HomeMaps.mLastKnownLocation.getBearing();

                    float degree = (360+((bearing + 360) % 360)-heading) % 360;
                    direction = ComputeDirection(degree);


                    if (loadData().equals("Ar")){
                            if (direction.equals("North"))
                                direction = "الشمال";
                            else if (direction.equals("Northeast"))
                                direction = "الشمال الشرقي";
                            else if (direction.equals("East"))
                                direction = "الشرق";
                            else if (direction.equals("Southeast"))
                                direction = "الجنوب الشرقي";
                            else if (direction.equals("South"))
                                direction = "الجنوب";
                            else if (direction.equals("Southwest"))
                                direction = "الجنوب الغربي";
                            else if (direction.equals("West"))
                                direction = "الغرب";
                            else if (direction.equals("Northwest"))
                                direction = "الشمال الغربي";


                            distanceText.setText(distance + " متر الى " + direction);

                }
                else {

                            distanceText.setText(distance + " meters to the " + direction);
                }

        }
        else {
                            distanceText.setText("");
        }





        // initializing English Text to Speech
        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status == TextToSpeech.SUCCESS) {
                            int result = mTTS.setLanguage(Locale.ENGLISH);
                            if (result == TextToSpeech.LANG_MISSING_DATA
                                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                                Log.e("TTS", "Language not supported");
                            } else {
                                speak(0);
                                Log.e("TTS", "tts initialized successfully");
                            }
                        } else {
                            Log.e("TTS", "Initialization failed");
                        }
                    }
        });



    }


    // car Button is pressed
    public void carBtn(View view){
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + lat  + "," + lng + "&mode=d&avoid=tfh");

                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");

                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {
                    Toast.makeText(TripInformation.this, "Please install Google Maps", Toast.LENGTH_LONG).show();
                }

    }


    // walk button is pressed
    public void walkBtn(View view){

                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + lat  + "," + lng + "&mode=w&avoid=tfh");

                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");

                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {
                    Toast.makeText(TripInformation.this, "Please install Google Maps", Toast.LENGTH_LONG).show();
                }


    }

    @Override
    protected void onResume() {
        super.onResume();
            // handler to run text to speech every amount of time specified in delayBetweenDistanceDirectionTTS
            handler.postDelayed(runnable = new Runnable() {
                public void run() {
                    handler.postDelayed(runnable, delayBetweenDistanceDirectionTTS);
                    if (distance != -1 && direction != null) {

                        // compute and update distance and direction
                        LatLng mlast = new LatLng(HomeMaps.mLastKnownLocation.getLatitude(), HomeMaps.mLastKnownLocation.getLongitude());
                        Location destinationLoc = new Location("service Provider");
                        destinationLoc.setLatitude(lat); //kaaba latitude setting
                        destinationLoc.setLongitude(lng); //kaaba longitude setting
                        Float bearing = HomeMaps.mLastKnownLocation.bearingTo(destinationLoc);
                        Float heading = HomeMaps.mLastKnownLocation.getBearing();
                        float degree = (360+((bearing + 360) % 360)-heading) % 360;
                        direction = ComputeDirection(degree);
                        distance = (int) SphericalUtil.computeDistanceBetween(mlast, new LatLng(lat, lng));

                        // convert text to speech
                        speak(1);


                    }


                }
            }, delayBetweenDistanceDirectionTTS);


    }
    @Override
    protected void onPause() {
        super.onPause();
        if (mTTS != null)
            mTTS.stop();
        if(handler!=null)
        handler.removeCallbacks(runnable); //stop handler when activity not visible super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mTTS != null)
            mTTS.stop();
        if(handler!=null)
        handler.removeCallbacks(runnable); //stop handler when activity not visible super.onPause();

        if(mediaPlayer!=null){
            mediaPlayer.stop();
        }

    }


    @Override
    protected void onDestroy() {
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }
        if(handler != null)
        handler.removeCallbacks(runnable); //stop handler when activity not visible super.onPause();

        if(mediaPlayer!=null){
            mediaPlayer.stop();
        }

        super.onDestroy();
    }


    // given a degree from 0 to 360, the method convert it to a string direction
    public static String ComputeDirection(float value){
        if((value>=337.5 && value<=360) || (value<22.5 && value>=0)){
            return "North";
        }
        else if(value>=22.5 && value<67.5){
            return "Northeast";
        }
        else if(value>=67.5 && value<112.5){
            return "East";
        }
        else if(value>=112.5 && value<157.5){
            return "Southeast";
        }
        else if(value>=157.5 && value<202.5){
            return "South";
        }
        else if(value>=202.5 && value<247.5){
            return "Southwest";
        }
        else if(value>=247.5 && value<292.5){
            return "West";
        }
        else if(value>=292.5 && value<337.5){
            return "Northwest";
        }
        return "error!";
    }


    private class SpeechText extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                fetchJsonResponse(sp);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // combine arabic and english language to avoid problems related to have a place name in english and arabic language
            if(trigger== true){


                try {
                    Thread.sleep(delayAfterName);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                mTTS.speak(" "  + distance + " meters to the " + direction, TextToSpeech.QUEUE_FLUSH, null); //english
            }




            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

        }
    }

    // grab audio file for arabic text to speech
    private void fetchJsonResponse(final String speech) {
        AudioManager audioManager = (AudioManager) TripInformation.this.getApplication().getSystemService(Context.AUDIO_SERVICE);
        url = "https://translate.google.com.vn/translate_tts?ie=UTF-8&q=" + speech + "&tl=ar&client=tw-ob";
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            delayAfterName = mediaPlayer.getDuration();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // turns text to speech: Case 0: when activity is initiated it utters name, type, distance, direction. Case 1: repeating only distance and direction every amount of time
    public void speak(int Case){
        trigger = false;
        if (loadData().equals("Ar")) {
                                    if (direction.equals("North"))
                                        direction = "الشمال";
                                    else if (direction.equals("Northeast"))
                                        direction = "الشمال الشرقي";
                                    else if (direction.equals("East"))
                                        direction = "الشرق";
                                    else if (direction.equals("Southeast"))
                                        direction = "الجنوب الشرقي";
                                    else if (direction.equals("South"))
                                        direction = "الجنوب";
                                    else if (direction.equals("Southwest"))
                                        direction = "الجنوب الغربي";
                                    else if (direction.equals("West"))
                                        direction = "الغرب";
                                    else if (direction.equals("Northwest"))
                                        direction = "الشمال الغربي";
                                    ConnectivityManager cm = (ConnectivityManager) TripInformation.this.getSystemService(Context.CONNECTIVITY_SERVICE);
                                    NetworkInfo netInfo = cm.getActiveNetworkInfo();
                                    boolean isOnline = netInfo != null && netInfo.isConnectedOrConnecting();
                                    if(Case ==0){
                                        sp = name + " " + type  + distance + " متر الى " + direction;

                                        if (isOnline) {
                                            new SpeechText().execute();
                                        } else {
                                            Log.e("TAG" , "Arabic text to Speech is not Working");
                                        }
                                    } // case 0
                                        else if (Case ==1){
                                    distanceText.setText(distance + " متر الى " + direction);

                                    // say name in arabic

                                    sp = distance + " متر الى " + direction;

                                    if (isOnline) {
                                        new SpeechText().execute();
                                    } else {
                                        Log.e("TAG" , "Arabic text to Speech is not Working");

                                    }


                                                         }

        } // Arabic Language
        else {
                                        if (Case ==0){
                                            ConnectivityManager cm = (ConnectivityManager) TripInformation.this.getSystemService(Context.CONNECTIVITY_SERVICE);
                                            NetworkInfo netInfo = cm.getActiveNetworkInfo();
                                            boolean isOnline = netInfo != null && netInfo.isConnectedOrConnecting();
                                            trigger = true;
                                            sp = name + " " + type;
                                            if (isOnline) {
                                                new SpeechText().execute();
                                            } else {
                                                Log.e("TAG" , "Arabic text to Speech is not Working");
                                            }

                                        }

                                        else if (Case == 1) {
                                            distanceText.setText(distance + " meters to the " + direction);
                                            mTTS.speak(distance + " meters to the " + direction, TextToSpeech.QUEUE_FLUSH, null); //english
                                        }
                                    }
    }
    public String loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String currentLanguage = sharedPreferences.getString(LANGUAGE, "Ar");
        return currentLanguage;
    }

}