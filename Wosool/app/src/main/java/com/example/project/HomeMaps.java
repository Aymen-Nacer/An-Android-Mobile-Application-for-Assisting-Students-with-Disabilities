package com.example.project;


import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.SphericalUtil;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class HomeMaps extends AppCompatActivity {
    // primary attributes
    private TextToSpeech mTTS;
    private boolean isExplorerEnabled;
    private FusedLocationProviderClient fusedLocationClient;
    public static Location mLastKnownLocation;
    private String LANGUAGE = "Ar";


    // helper attributes
    public TextView ContentofExplorer;
    private static final int REQUEST_CHECK_SETTINGS = 1;
    private static final int REQUEST_GRANT_PERMISSION = 2;
    LocationRequest locationRequest;
    private LocationCallback locationCallback;
    Button explorerbotton;
    public boolean mTaskRunning;
    public boolean cancel;
    public int delayAfterName;
    public int flag;
    String sp;
    String url;
    MediaPlayer mediaPlayer;
    String fullDistanceDirection ;
    TextView myLocationTextView;
    public static final String SHARED_PREFS = "sharedPrefs";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_home_maps);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        createLocationRequest();
        settingsCheck();
        isExplorerEnabled = false;
        explorerbotton = findViewById(R.id.expButton);
        myLocationTextView = findViewById(R.id.textViewContent);
        ContentofExplorer = findViewById(R.id.textViewContent);


        // translation
        if(loadData().equals("Ar")){
            TextView restaurantBtn = findViewById(R.id.restaurantBtn);
            TextView HospitalBtn = findViewById(R.id.HospitalBtn);
            TextView parkingBtn = findViewById(R.id.parkingBtn);
            TextView CollegesBtn = findViewById(R.id.CollegesBtn);
            TextView mosqueBtn = findViewById(R.id.mosqueBtn);
            TextView cafeBtn = findViewById(R.id.cafeBtn);
            TextView atmBtn = findViewById(R.id.atmBtn);
            TextView LibraryBtn = findViewById(R.id.LibraryBtn);

            Button expButton = findViewById(R.id.expButton);
            Button myLocationBtn = findViewById(R.id.myLocationBtn);
            Button SearchButton = findViewById(R.id.SearchButton);

            restaurantBtn.setText("المطاعم");
            HospitalBtn.setText("المستشفيات");
            parkingBtn.setText("المواقف");
            CollegesBtn.setText("الكليات");
            mosqueBtn.setText("المساجد");
            cafeBtn.setText("المقاهي");
            atmBtn.setText("الصرافات الآلية");
            LibraryBtn.setText("المكتبات");

            expButton.setText("المستكشف");
            myLocationBtn.setText("مكاني");
            SearchButton.setText("البحث");
        }


        // setting English Text to speech
        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = mTTS.setLanguage(Locale.ENGLISH);
                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    } else {
                        Log.e("TTS", "tts initialized successfully");
                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });


        // check location permission
        if (ActivityCompat.checkSelfPermission(HomeMaps.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(HomeMaps.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_GRANT_PERMISSION);
            return;
        }
        if (locationCallback == null)
            buildLocationCallback();
        if (mLastKnownLocation == null)
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

    }




    public void searchPlaceButton(View view) {
        isExplorerEnabled = false;

        if (mTTS != null) {
            mTTS.stop();
        }
        if( mediaPlayer!= null){
            mediaPlayer.stop();
        }
        // move to SearchPlace Activity
        Intent intent = new Intent(getApplicationContext(), SearchPlaces.class);
        startActivity(intent);
    }


    public void myLocationButton(View view) {
        isExplorerEnabled = false;

        if (mTTS != null) {
            mTTS.stop();
        }
        if( mediaPlayer!= null){
            mediaPlayer.stop();
        }

   // use geocoder to retreive user's address

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            new geocodeSet().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            new geocodeSet().execute();


    }

    public void explorerButton(View view) {


        isExplorerEnabled = true;

        if (mTTS != null) {
            mTTS.stop();
        }
        if( mediaPlayer!= null){
            mediaPlayer.stop();
        }

        if(mTaskRunning){
            cancel = true;
            mTaskRunning = false;
        }
        else{
            String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" + "location=" + mLastKnownLocation.getLatitude() + "," + mLastKnownLocation.getLongitude() + "&rankby=distance" + "&sensor=true" + "&key=" + getResources().getString(R.string.google_maps_api);
            cancel = false;
            mTaskRunning = true;


            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                new placeTaskExplorer().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
            else
                new placeTaskExplorer().execute(url);

        }

    }

    protected void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    // Check for location settings
    public void settingsCheck() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                getCurrentLocation();
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    Log.d("TAG", "onFailure: settingsCheck");
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(HomeMaps.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }

    public void getCurrentLocation(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            mLastKnownLocation=location;
                        }else{
                            buildLocationCallback();
                        }
                    }
                });
    }

    private void buildLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    mLastKnownLocation=location;
                    Log.d("TAG", "onLocationResult: "+mLastKnownLocation.getLatitude());
                    Log.i("tag" , "success" );


                }
            };
        };
    }

    //called after user responds to location permission popup
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUEST_GRANT_PERMISSION){
            getCurrentLocation();
        }
    }
    //called after user responds to location settings popup
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("TAG", "onActivityResult: ");
        if(requestCode==REQUEST_CHECK_SETTINGS && resultCode==RESULT_OK)
            getCurrentLocation();
        if(requestCode==REQUEST_CHECK_SETTINGS && resultCode==RESULT_CANCELED)
            Toast.makeText(this, "Please enable Location settings", Toast.LENGTH_SHORT).show();
    }


    // when nearby restaurants button is clicked
    public void nearbyRestaurantsButton(View view) {
        if (mTTS != null) {
            mTTS.stop();
        }
        if( mediaPlayer!= null){
            mediaPlayer.stop();
        }
        isExplorerEnabled = false;
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" + "location=" + mLastKnownLocation.getLatitude() + "," + mLastKnownLocation.getLongitude() + "&rankby=distance" + "&type=" + "restaurant" + "&sensor=true" + "&key=" + getResources().getString(R.string.google_maps_api);


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            new placeTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
        else
            new placeTask().execute(url);

    }

    // when nearby hospitals button is clicked
    public void nearbyHospitalsButton(View view) {
        if (mTTS != null) {
            mTTS.stop();
        }
        if( mediaPlayer!= null){
            mediaPlayer.stop();
        }

        isExplorerEnabled = false;

        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" + "location=" + mLastKnownLocation.getLatitude() + "," + mLastKnownLocation.getLongitude() + "&rankby=distance" + "&type=" + "hospital" + "&sensor=true" + "&key=" + getResources().getString(R.string.google_maps_api);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            new placeTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
        else
            new placeTask().execute(url);


    }
    // when nearby parkings button is clicked
    public void nearbyParkingsButton(View view) {
        if (mTTS != null) {
            mTTS.stop();
        }
        if( mediaPlayer!= null){
            mediaPlayer.stop();
        }

        isExplorerEnabled = false;

        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" + "location=" + mLastKnownLocation.getLatitude() + "," + mLastKnownLocation.getLongitude() + "&rankby=distance" + "&type=" + "parking" + "&sensor=true" + "&key=" + getResources().getString(R.string.google_maps_api);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            new placeTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
        else
            new placeTask().execute(url);

    }

    // when nearby colleges button is clicked
    public void nearbyCollegesButton(View view) {
        if (mTTS != null) {
            mTTS.stop();
        }
        if( mediaPlayer!= null){
            mediaPlayer.stop();
        }


        isExplorerEnabled = false;

        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" + "location=" + mLastKnownLocation.getLatitude() + "," + mLastKnownLocation.getLongitude() + "&rankby=distance" + "&type=" + "university" + "&sensor=true" + "&key=" + getResources().getString(R.string.google_maps_api);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            new placeTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
        else
            new placeTask().execute(url);

    }

    // when nearby mosques button is clicked
    public void nearbyMosquesButton(View view) {
        if (mTTS != null) {
            mTTS.stop();
        }
        if( mediaPlayer!= null){
            mediaPlayer.stop();
        }


        isExplorerEnabled = false;

        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" + "location=" + mLastKnownLocation.getLatitude() + "," + mLastKnownLocation.getLongitude() + "&rankby=distance" + "&type=" + "mosque" + "&sensor=true" + "&key=" + getResources().getString(R.string.google_maps_api);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            new placeTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
        else
            new placeTask().execute(url);


    }

    // when nearby cafes button is clicked
    public void nearbyCafesButton(View view) {
        if (mTTS != null) {
            mTTS.stop();
        }
        if( mediaPlayer!= null){
            mediaPlayer.stop();
        }


        isExplorerEnabled = false;
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" + "location=" + mLastKnownLocation.getLatitude() + "," + mLastKnownLocation.getLongitude() + "&rankby=distance" + "&type=" + "cafe" + "&sensor=true" + "&key=" + getResources().getString(R.string.google_maps_api);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            new placeTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
        else
            new placeTask().execute(url);

    }

    // when nearby ATMs button is clicked
    public void nearbyAtmsButton(View view) {
        if (mTTS != null) {
            mTTS.stop();
        }
        if( mediaPlayer!= null){
            mediaPlayer.stop();
        }

        isExplorerEnabled = false;
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" + "location=" + mLastKnownLocation.getLatitude() + "," + mLastKnownLocation.getLongitude() + "&rankby=distance" + "&type=" + "atm" + "&sensor=true" + "&key=" + getResources().getString(R.string.google_maps_api);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            new placeTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
        else
            new placeTask().execute(url);

    }

    // when nearby libraries button is clicked
    public void nearbyLibrariesButton(View view) {
        if (mTTS != null) {
            mTTS.stop();
        }
        if( mediaPlayer!= null){
            mediaPlayer.stop();
        }
        isExplorerEnabled = false;
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" + "location=" + mLastKnownLocation.getLatitude() + "," + mLastKnownLocation.getLongitude() + "&rankby=distance" + "&type=" + "library" + "&sensor=true" + "&key=" + getResources().getString(R.string.google_maps_api);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            new placeTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
        else
            new placeTask().execute(url);


    }


    private class placeTask extends AsyncTask<String, Integer, String>{
        @Override
        protected String doInBackground(String... strings) {
           String data = null;
            try {
                 data = downloadUrl(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                new ParserTaskNearbyList().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, s);
            else
                new ParserTaskNearbyList().execute(s);


        }

    }

    private class placeTaskExplorer extends AsyncTask<String, Integer, String>{
        @Override
        protected String doInBackground(String... strings) {
            String data = null;
            try {
                data = downloadUrl(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                new ParserTaskExplorer().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, s);
            else
                new ParserTaskExplorer().execute(s);


        }

    }

    private String downloadUrl(String string) throws IOException{
        URL url = new URL(string);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.connect();
        InputStream stream = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder builder = new StringBuilder();
        String line = "";
        while((line = reader.readLine())!= null){
           builder.append(line);
        }
        String data = builder.toString();

        reader.close();

        return data;
    }

    private class ParserTaskNearbyList extends AsyncTask<String, Integer , List<HashMap<String, String>>>{
        @Override
        protected List<HashMap<String, String>> doInBackground(String... strings) {
           JsonParser jsonParser = new JsonParser();
           List<HashMap<String, String>> hashMaps = null;
           JSONObject object = null;
            try {
                 object = new JSONObject(strings[0]);
                hashMaps = jsonParser.parseResult(object);

            } catch (JSONException e) {
                e.printStackTrace();
            }

return hashMaps;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> hashMaps) {
            fetchNearbyPlacesCategory(hashMaps);

        }
    }

    private class ParserTaskExplorer extends AsyncTask<String, Integer , List<HashMap<String, String>>>{
        @Override
        protected List<HashMap<String, String>> doInBackground(String... strings) {
            JsonParser jsonParser = new JsonParser();
            List<HashMap<String, String>> hashMaps = null;
            JSONObject object = null;
            try {
                object = new JSONObject(strings[0]);
                hashMaps = jsonParser.parseResult(object);

            } catch (JSONException e) {
                e.printStackTrace();
            }



                fetchNearbyPlacesExplorer(hashMaps);



            return hashMaps;
        }


    }




    @Override
    protected void onDestroy() {
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }
        if( mediaPlayer!= null){
            mediaPlayer.stop();
        }

        super.onDestroy();
    }






    @Override
    protected void onPause() {
        super.onPause();
        isExplorerEnabled = false;
        if(mTaskRunning){
            cancel = true;
            mTaskRunning = false;
        }

        if (mTTS != null) {
            mTTS.stop();
        }
        if( mediaPlayer!= null){
            mediaPlayer.stop();
        }

    }
    @Override
    protected void onStop() {
        super.onStop();
        isExplorerEnabled = false;
        if(mTaskRunning){
            cancel = true;
            mTaskRunning = false;
        }

        if (mTTS != null) {
            mTTS.stop();
        }
        if( mediaPlayer!= null){
            mediaPlayer.stop();
        }

    }



    private class SpeechText extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... voids) {





            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

        }
    }



    // to fetch audio file for the Arabic text to speech purpose
    private void fetchJsonResponse(final String speech) {
        AudioManager audioManager = (AudioManager) HomeMaps.this.getApplication().getSystemService(Context.AUDIO_SERVICE);
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


    // geocoder to retreive user's location
    public class geocodeSet extends AsyncTask<Void,Integer,String>
    {

        @Override
        protected String doInBackground(Void... voids)
        {
            Geocoder geocoder = new Geocoder(HomeMaps.this, Locale.getDefault());
            String result = null;
            try {
                List<Address> list = geocoder.getFromLocation(
                        mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude(), 1);
                if (list != null && list.size() > 0) {
                    Address address = list.get(0);
                    // sending back first address line and locality
                    result = address.getAddressLine(0) + ", " + address.getLocality();
                }
            } catch (IOException e) {
                Log.e("TAG", "Impossible to connect to Geocoder", e);
            } finally {
                if (result != null) {
                    return result;
                }

            }
            return "";

        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            myLocationTextView.setText(s);
            mTTS.speak(s, TextToSpeech.QUEUE_FLUSH, null);

        }
    }

    // to retreive language selected by the user and set it
    public String loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String currentLanguage = sharedPreferences.getString(LANGUAGE, "Ar");
        return currentLanguage;
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


    public void fetchNearbyPlacesCategory(List<HashMap<String, String>> hashMaps){

        // retreive places data in a set of lists


        ArrayList<String> buildingNames = new ArrayList<>();
        ArrayList<Double> lats = new ArrayList<>();
        ArrayList<Double> lngs = new ArrayList<>();
        ArrayList<Integer> distances = new ArrayList<>();
        ArrayList<String> types = new ArrayList<>();
        ArrayList<String> directions = new ArrayList<>();
        PlacesList.places.clear();
        String typeOfPlace = null;
        int typeActionBar = -1;


        for (int i =0; i<hashMaps.size(); i++){
            //get data after parsing
            HashMap<String, String> hashMapList = hashMaps.get(i);
            double lat = Double.parseDouble(hashMapList.get("lat"));
            double lng = Double.parseDouble(hashMapList.get("lng"));
            String name = hashMapList.get("name");
            typeOfPlace = hashMapList.get("type");

            // compute distance to the place
            LatLng mlast = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
            LatLng nearby = new LatLng(lat , lng);
            int dis = (int)SphericalUtil.computeDistanceBetween(mlast, nearby);

            // need only places in the range of 1500 meters
            if (dis> 1500){
                break;
            }

            // compute the direction to the place
            Location destinationLoc = new Location("service Provider");
            destinationLoc.setLatitude(lat); //kaaba latitude setting
            destinationLoc.setLongitude(lng); //kaaba longitude setting
            Float bearing = mLastKnownLocation.bearingTo(destinationLoc);
            Float heading = mLastKnownLocation.getBearing();
            float degree = (360+((bearing + 360) % 360)-heading) % 360;
            String directionNearby = ComputeDirection(degree);


            // capitalize the first letter of the place type
            String myString = typeOfPlace;
            typeOfPlace =  myString.substring(0, 1).toUpperCase() + myString.substring(1).toLowerCase();


            // set the type to identify the type of the Action Bar in PlacesList Acitivity
            if (typeOfPlace.equals("Restaurant"))
                typeActionBar = 0;
            else if (typeOfPlace.equals("Hospital"))
                typeActionBar = 1;
            else if (typeOfPlace.equals("Parking"))
                typeActionBar = 2;
            else if (typeOfPlace.equals("University"))
                typeActionBar = 3;
            else if (typeOfPlace.equals("Mosque"))
                typeActionBar = 4;
            else if (typeOfPlace.equals("Cafe"))
                typeActionBar = 5;
            else if (typeOfPlace.equals("Atm"))
                typeActionBar = 6;
            else if (typeOfPlace.equals("Library"))
                typeActionBar = 7;



            // translation
            if (loadData().equals("Ar")){
                if(directionNearby.equals("North"))
                    directionNearby = "الشمال";
                else if(directionNearby.equals("Northeast"))
                    directionNearby = "الشمال الشرقي";
                else if(directionNearby.equals("East"))
                    directionNearby = "الشرق";
                else if(directionNearby.equals("Southeast"))
                    directionNearby = "الجنوب الشرقي";
                else if(directionNearby.equals("South"))
                    directionNearby = "الجنوب";
                else if(directionNearby.equals("Southwest"))
                    directionNearby = "الجنوب الغربي";
                else if(directionNearby.equals("West"))
                    directionNearby = "الغرب";
                else if(directionNearby.equals("Northwest"))
                    directionNearby = "الشمال الغربي";


                fullDistanceDirection = dis + " متر الى " + directionNearby;

                if (typeOfPlace.equals("Restaurant"))
                    typeOfPlace = "مطعم" ;
                else if (typeOfPlace.equals("Hospital"))
                    typeOfPlace = "مستشفى" ;
                else if (typeOfPlace.equals("Parking"))
                    typeOfPlace = "موقف" ;
                else if (typeOfPlace.equals("University"))
                    typeOfPlace = "جامعة" ;
                else if (typeOfPlace.equals("Mosque"))
                    typeOfPlace = "مسجد" ;
                else if (typeOfPlace.equals("Cafe"))
                    typeOfPlace = "مقهى" ;
                else if (typeOfPlace.equals("Atm"))
                    typeOfPlace = "صراف آلي" ;
                else if (typeOfPlace.equals("Library"))
                    typeOfPlace = "مكتبة" ;


            }
            else {

                fullDistanceDirection =  dis + " meters to the " + directionNearby;

            }

            buildingNames.add(name);
            lats.add(lat);
            lngs.add(lng);
            types.add(typeOfPlace);
            distances.add(dis);
            directions.add(directionNearby);
            // places are information to display on the custom list in PlacesList Activity
            PlacesList.places.add(new PlaceDetail(name , typeOfPlace, fullDistanceDirection));

        }



        Intent intent = new Intent(getApplicationContext(), PlacesList.class);

        // pass data to PlacesList Activity
        intent.putStringArrayListExtra("BuildingNames" , buildingNames);
        intent.putExtra("lats" , lats);
        intent.putExtra("lngs" , lngs);
        intent.putStringArrayListExtra("types" , types);
        intent.putIntegerArrayListExtra("distances" , distances);
        intent.putStringArrayListExtra("directions" , directions);
        intent.putExtra("flag" , 1);
        intent.putExtra("typeActionBar" , typeActionBar);
        startActivity(intent);
    }

    public void fetchNearbyPlacesExplorer(List<HashMap<String, String>> hashMaps){



        for (int i = 0; i < hashMaps.size(); i++) {
            if(isExplorerEnabled== false){
                break;

            }
            if(cancel)
                break;



            // retreive data from parsing
            HashMap<String, String> hashMapList = hashMaps.get(i);
            double lat = Double.parseDouble(hashMapList.get("lat"));
            double lng = Double.parseDouble(hashMapList.get("lng"));
            String name = hashMapList.get("name");


            // compute distance to the place
            LatLng mlast = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
            LatLng nearby = new LatLng(lat, lng);
            int dis = (int) SphericalUtil.computeDistanceBetween(mlast, nearby);
            if (dis > 100) {
                break;
            }


            // compute the direction
            Location destinationLoc = new Location("service Provider");
            destinationLoc.setLatitude(lat); //kaaba latitude setting
            destinationLoc.setLongitude(lng); //kaaba longitude setting
            Float bearing = mLastKnownLocation.bearingTo(destinationLoc);
            Float heading = mLastKnownLocation.getBearing();
            float degree = (360+((bearing + 360) % 360)-heading) % 360;
            String direction = ComputeDirection(degree);

            // translation
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

                fullDistanceDirection =  " " + dis + " متر الى " + direction;


                // display explorer content on screen
                runOnUiThread(new Runnable() {

                    public void run() {
                        ContentofExplorer.setText(name + "\n" + fullDistanceDirection);
                    }

                });

                // i used combinations to english and arabic text to speech to eliminate the problem of having a place name in both english and arabic languages

                ConnectivityManager cm = (ConnectivityManager) HomeMaps.this.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                boolean isOnline = netInfo != null && netInfo.isConnectedOrConnecting();

                sp = name  + fullDistanceDirection;

                if (isOnline) {

                    try {
                        fetchJsonResponse(sp);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else {
                    Log.e("TAG" , "Arabic text to Speech is not Working");
                }

                if(isExplorerEnabled== false){
                    break;


                }
                if(cancel)
                    break;

                try {
                    Thread.sleep(delayAfterName);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if(isExplorerEnabled== false){
                    break;

                }
                if(cancel)
                    break;


            }

            // english language
            else {

                fullDistanceDirection =  " " + dis + " meters to the " + direction;


                // display explorer content on screen
                runOnUiThread(new Runnable() {

                    public void run() {
                        ContentofExplorer.setText(name + "\n" + fullDistanceDirection);
                    }

                });



                ConnectivityManager cm = (ConnectivityManager) HomeMaps.this.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                boolean isOnline = netInfo != null && netInfo.isConnectedOrConnecting();

                sp = name;

                if (isOnline) {
                    try {
                        fetchJsonResponse(sp);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    Log.e("TAG" , "Arabic text to Speech is not Working");
                }

                if(isExplorerEnabled== false){
                    break;



                }
                if(cancel)
                    break;

                try {
                    Thread.sleep(delayAfterName);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if(isExplorerEnabled== false){
                    break;



                }
                if(cancel)
                    break;


                mTTS.speak(fullDistanceDirection, TextToSpeech.QUEUE_FLUSH, null); // in english


                if(isExplorerEnabled== false){
                    break;

                }
                if(cancel)
                    break;

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if(isExplorerEnabled== false){
                    break;


                }
                if(cancel) {
                    break;
                }

            }




        }
    }

}

