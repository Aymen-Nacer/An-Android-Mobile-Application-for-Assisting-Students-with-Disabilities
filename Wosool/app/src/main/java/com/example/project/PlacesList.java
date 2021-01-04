package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlacesList extends AppCompatActivity {
    // primary attributes
    private int flag; // there are two actions to perform in PlacesList either nearby places list or Building list
    private int typeActionBar; // flag to identify the name of the action bar in the PlacesList activity: 0 for restaurant, 1 for hospital, 2 for parking, 3 for university
    private  ArrayList<String> BuildingsNames= new ArrayList<>();
    private  ArrayList<Double> lats= new ArrayList<>();
    private  ArrayList<Double> lngs= new ArrayList<>();
    private  ArrayList<Integer> distances = new ArrayList<>();
    private  ArrayList<String> types = new ArrayList<>();
    private  ArrayList<String> directions = new ArrayList<>();
    private PlacesClient placesClient;
    private   String LANGUAGE = "Ar";

    // helper attributes
    public static  ArrayList<PlaceDetail> places = new ArrayList<>();
    private   String SHARED_PREFS = "sharedPrefs";
    ListView buildingsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_list);

        Places.initialize(PlacesList.this, getString(R.string.google_maps_api));
        placesClient = Places.createClient(this);
        buildingsList = findViewById(R.id.BuildingsList);

        // set Action bar color
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blueWosool)));

        // grab data from intent passed
        flag = getIntent().getIntExtra("flag" , -3);
        if (flag ==0) {
            BuildingsNames = getIntent().getExtras().getStringArrayList("BuildingNames");
            typeActionBar = getIntent().getIntExtra("typeActionBar", -3);
        }
        else {
            BuildingsNames = getIntent().getExtras().getStringArrayList("BuildingNames");
            lats = (ArrayList<Double>) getIntent().getSerializableExtra("lats");
            lngs = (ArrayList<Double>) getIntent().getSerializableExtra("lngs");
            types = getIntent().getExtras().getStringArrayList("types");
            distances = getIntent().getIntegerArrayListExtra("distances");
            directions = getIntent().getExtras().getStringArrayList("directions");
            typeActionBar = getIntent().getIntExtra("typeActionBar", -3);



            }



        // set the name of the action bar
        if (loadData().equals("En")) {
            if (typeActionBar == 0)
                getSupportActionBar().setTitle("Nearby restaurants");
            else if (typeActionBar == 1)
                getSupportActionBar().setTitle("Nearby hospitals");
            else if (typeActionBar == 2)
                getSupportActionBar().setTitle("Nearby parkings");
            else if (typeActionBar == 3)
                getSupportActionBar().setTitle("Nearby colleges");
            else if (typeActionBar == 4)
                getSupportActionBar().setTitle("Nearby mosques");
            else if (typeActionBar == 5)
                getSupportActionBar().setTitle("Nearby cafes");
            else if (typeActionBar == 6)
                getSupportActionBar().setTitle("Nearby ATMs");
            else if (typeActionBar == 7)
                getSupportActionBar().setTitle("Nearby libraries");
            else if (typeActionBar == -2) {
                getSupportActionBar().setTitle("University buildings");
            }

        }
        else {
            if (typeActionBar == 0)
                getSupportActionBar().setTitle("المطاعم القريبة");
            else if (typeActionBar == 1)
                getSupportActionBar().setTitle("المستشفيات القريبة");
            else if (typeActionBar == 2)
                getSupportActionBar().setTitle("مواقف السيارات القريبة");
            else if (typeActionBar == 3)
                getSupportActionBar().setTitle("الكليات القريبة");
            else if (typeActionBar == 4)
                getSupportActionBar().setTitle("المساجد القريبة");
            else if (typeActionBar == 5)
                getSupportActionBar().setTitle("المقاهي المجاورة");
            else if (typeActionBar == 6)
                getSupportActionBar().setTitle("أجهزة الصراف الآلي القريبة");
            else if (typeActionBar == 7)
                getSupportActionBar().setTitle("المكتبات القريبة");
            else if (typeActionBar == -2) {
                getSupportActionBar().setTitle("مباني الجامعة");
            }
        }

        // this displays a list related to buildings button from the SearchPlace Activity
        if (flag ==0) {
            setUniversityBuildingsList();
        }
        // this displays a list related to nearby places from the HomeMaps Activity
        else {
            setNearbyPlaces();
        }

    }

    // make a request to retreive places details when clicked in University Buildings list, number of place variable is to add details manually to a particular place
    private void getPlacesDetails(String placeID, int numberofPlace){
        List<Place.Field> placeFields = Arrays.asList(Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.TYPES);
        // FetchPlaceRequest fetchPlaceRequest = FetchPlaceRequest.builder(placeId, placeFields).build();
        // Construct a request object, passing the place ID and fields array.

        final FetchPlaceRequest fetchPlaceRequest = FetchPlaceRequest.newInstance(placeID, placeFields);
        placesClient.fetchPlace(fetchPlaceRequest).addOnSuccessListener((response) -> {

            Intent intent = new Intent(getApplicationContext(), TripInformation.class);


            Place place = response.getPlace();

            // latitude and longitude of typed area
            LatLng latLngOfPlace = place.getLatLng();
            LatLng mlast = new LatLng(HomeMaps.mLastKnownLocation.getLatitude(), HomeMaps.mLastKnownLocation.getLongitude());


            String direction = "";
            if(numberofPlace ==0) {
                intent.putExtra("PlaceName" , place.getName());
                String myString = place.getTypes().get(0).toString();



                String typeActivity = myString.substring(0, 1).toUpperCase() + myString.substring(1).toLowerCase();

                if (loadData().equals("Ar")) {

                    if (typeActivity.equals("Restaurant"))
                        typeActivity = "مطعم" ;
                    else if (typeActivity.equals("Hospital"))
                        typeActivity = "مستشفى" ;
                    else if (typeActivity.equals("Parking"))
                        typeActivity = "موقف" ;
                    else if (typeActivity.equals("University"))
                        typeActivity = "جامعة" ;
                    else if (typeActivity.equals("Mosque"))
                        typeActivity = "مسجد" ;
                    else if (typeActivity.equals("Cafe"))
                        typeActivity = "مقهى" ;
                    else if (typeActivity.equals("Atm"))
                        typeActivity = "صراف آلي" ;
                    else if (typeActivity.equals("Library"))
                        typeActivity = "مكتبة" ;

                }
                intent.putExtra("typeOfPlace" , typeActivity);
            }
            else {
                if (loadData().equals("Ar")){

                    intent.putExtra("PlaceName" , "برنامج الوصول الشامل");
                    intent.putExtra("typeOfPlace" , "مركز في الجامعة");

                }
                else {


                    intent.putExtra("PlaceName" , "Universal Access Program");
                    intent.putExtra("typeOfPlace" , "a Center in the university");
                }

            }

            intent.putExtra("Distance" ,(int)SphericalUtil.computeDistanceBetween(mlast, latLngOfPlace));
            intent.putExtra("Direction" , direction);
            intent.putExtra("Latitude" , latLngOfPlace.latitude);
            intent.putExtra("Longitude" , latLngOfPlace.longitude);


            startActivity(intent);

        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                final ApiException apiException = (ApiException) exception;
                Log.e("TAG", "Place not found: " + exception.getMessage());
                final int statusCode = apiException.getStatusCode();
                // TODO: Handle error with given status code.
            }
        });

    }
    public String loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String currentLanguage = sharedPreferences.getString(LANGUAGE, "Ar");
        return currentLanguage;
    }

    public void setUniversityBuildingsList(){
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, BuildingsNames);

        buildingsList.setAdapter(arrayAdapter);
        buildingsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {

                    getPlacesDetails("ChIJfyjoiXbiLj4R-bV9B3cwWFY" , 0);

                } else if (i == 1) {
                    getPlacesDetails("ChIJ94g9K3biLj4Rr0hHuN3cjvg" , 0);


                } else if (i == 2) {
                    getPlacesDetails("ChIJifLREXbiLj4RAjNftGzBHs8" , 0);

                } else if (i == 3) {
                    getPlacesDetails("ChIJm6sts4kdLz4RBzoNpoxoq1k", 0);

                } else if (i == 4) {
                    getPlacesDetails("ChIJHyLaAoodLz4RJESGgXX1C7A" , 0 );

                } else if (i == 5) {
                    getPlacesDetails("ChIJ6atQC4kdLz4RuVYtWM9LGC8" , 0);

                } else if (i == 6) {
                    getPlacesDetails("ChIJS0Ec4IsdLz4RlDqC7c3Ix4Y" , 0);

                } else if (i == 7) {

                    getPlacesDetails("ChIJl4Bl3ogdLz4Rr_yWyYpFW8w" , 0);
                } else if (i == 8) {

                    getPlacesDetails("ChIJz4CE5YsdLz4Rn3OXuEvvw5k" , 1);
                }

            }
        });
    }

    public void setNearbyPlaces(){

        PlacesListAdapter adapter = new PlacesListAdapter(this, R.layout.adapter_view_layout, places);
        buildingsList.setAdapter(adapter);
        buildingsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                // pass the details of the place clicked to trip information Activity
                Intent intent = new Intent(getApplicationContext(), TripInformation.class);
                intent.putExtra("PlaceName" , BuildingsNames.get(i));
                intent.putExtra("Distance" , distances.get(i));
                intent.putExtra("Latitude" ,lats.get(i) );
                intent.putExtra("Longitude" , lngs.get(i));
                intent.putExtra("typeOfPlace" , types.get(i));
                intent.putExtra("Direction" , directions.get(i));
                startActivity(intent);

            }
        });


    }
}