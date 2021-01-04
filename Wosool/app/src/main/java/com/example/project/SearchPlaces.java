package com.example.project;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.maps.android.SphericalUtil;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;



import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;



import java.util.Locale;

public class SearchPlaces extends AppCompatActivity implements OnMapReadyCallback {
    // primary attributes
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private List<AutocompletePrediction> predictionList; // in the search bar
    private boolean locationPermissionGranted;
    private PlacesClient placesClient;
    private SpeechRecognizer speechRecognizer;
    private Location mLastKnownLocation;
    private MaterialSearchBar materialSearchBar;
    private String LANGUAGE = "Ar";

    // helper attributes
    Runnable runnable;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int PERMISSIONS_REQUEST_RECOGNIZER = 6;
    protected static  final  int RESULT_SPEECH = 77;
    private CameraPosition cameraPosition;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 23487;
    private TextView responseView;
    public TextView text1234;
    private static final int RECOGNIZER_RESULT = 1;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private LocationCallback locationCallback;
    private View mapView;
    private final float DEFAULT_ZOOM = 15;
    public static final String SHARED_PREFS = "sharedPrefs";
    Button screenReaderBackBtn;
    Button screenReaderMicBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_places);
        // initializing
        materialSearchBar = findViewById(R.id.searchBar);
        Button goToButton = findViewById(R.id.goToButton);
        Button buildingsButton = findViewById(R.id.buildingsButton);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        screenReaderBackBtn = findViewById(R.id.whiteButton);
        screenReaderMicBtn = findViewById(R.id.Microphone);

        // translate to Arabic in case lanugage is Arabic
        if(loadData().equals("Ar")){
            goToButton.setText("أذهب إلى");
            buildingsButton.setText("مباني الجامعة");
            materialSearchBar.setHint("إبحث عن مكان");
            materialSearchBar.setPlaceHolder("إبحث عن مكان");
            screenReaderBackBtn.setContentDescription("الرجوع");
        }


        // Back search bar button for screen reader purpose
        screenReaderBackBtn.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View view) {
                                               materialSearchBar.closeSearch();
                                           }
        });



        // Microphone search bar button for screen reader purpose
        // if change from microphone to delete when search is initiated
        screenReaderMicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loadData().equals("Ar")) {
                    if (screenReaderMicBtn.getContentDescription().equals("الحذف")) {
                        materialSearchBar.setText("");
                    } else if (screenReaderMicBtn.getContentDescription().equals("Microphone")) {
                        openVoiceRecognizer();
                    }

                } else {
                    if (screenReaderMicBtn.getContentDescription().equals("Delete")) {
                        materialSearchBar.setText("");
                    } else if (screenReaderMicBtn.getContentDescription().equals("Microphone")) {
                        openVoiceRecognizer();
                    }
                }

            }
        });

        // hide Action Bar
        getSupportActionBar().hide();


        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapView = mapFragment.getView();

        // check for Audio permission
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            checkPermission();
        }

        // initialize mfused location for retreiving user's current location
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(SearchPlaces.this);

        // initialize places client
        Places.initialize(SearchPlaces.this, getString(R.string.google_maps_api));
        placesClient = Places.createClient(this);
        final AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

        // if search tool clicked
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                String s = enabled ? "enabled" : "disabled";
                if(s.equals("enabled")){
                    screenReaderBackBtn.setEnabled(true);

                    if(loadData().equals("Ar")){
                        screenReaderMicBtn.setContentDescription("الحذف");
                    }
                    else {
                        screenReaderMicBtn.setContentDescription("Delete");
                    }

                }
                else {
                    screenReaderBackBtn.setEnabled(false);
                    screenReaderMicBtn.setContentDescription("Microphone");
                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearch(text.toString(), true, null, true);
            }

            @Override
            public void onButtonClicked(int buttonCode) {
                if (buttonCode == MaterialSearchBar.BUTTON_NAVIGATION) {
                    //opening or closing a navigation drawer
                }
                if (buttonCode == MaterialSearchBar.BUTTON_BACK) {
                    materialSearchBar.closeSearch();

                }
                if(buttonCode == MaterialSearchBar.BUTTON_SPEECH) {
                    openVoiceRecognizer(); // for speech recognition

                }
            }
        });


        // if text in the search bar changed
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                materialSearchBar.clearSuggestions();
            }
            //Recommended: A RectangularBounds object, which specifies latitude and longitude bounds to constrain results to the specified region.
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
                // and once again when the user makes a selection (for example when calling fetchPlace()).
                AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

                // Use the builder to create a FindAutocompletePredictionsRequest.
                FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                        .setCountries("SA")
                        .setSessionToken(token)
                        .setQuery(s.toString())
                        .build();
                List<String> suggestionsList = new ArrayList<>();

                // make an Auto complete prediction request to retreive the suggestion list shown on the search bar
                placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
                    predictionList = response.getAutocompletePredictions();
                    for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                        suggestionsList.add(prediction.getFullText(null).toString());
                    }
                    materialSearchBar.updateLastSuggestions(suggestionsList);
                    if (!materialSearchBar.isSuggestionsVisible()) {
                        materialSearchBar.showSuggestionsList();
                    }
                }).addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        Log.e("TAG", "Place not found: " + apiException.getStatusCode());
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        // if suggestions are clicked
        materialSearchBar.setSuggestionsClickListener(new SuggestionsAdapter.OnItemViewClickListener() {
            @Override
            public void OnItemClickListener(int position, View v) {
                if (position >= predictionList.size()) {
                    return;
                }
                AutocompletePrediction selectedPrediction = predictionList.get(position);
                String suggestion = materialSearchBar.getLastSuggestions().get(position).toString();
                materialSearchBar.setText(suggestion);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        materialSearchBar.clearSuggestions();
                    }
                }, 1000);
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.hideSoftInputFromWindow(materialSearchBar.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);

                final String placeId = selectedPrediction.getPlaceId();
                List<Place.Field> placeFields = Arrays.asList(Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.TYPES);

                // conduct a place request to retreive the place details
                final FetchPlaceRequest fetchPlaceRequest = FetchPlaceRequest.newInstance(placeId, placeFields);
                placesClient.fetchPlace(fetchPlaceRequest).addOnSuccessListener((response) -> {
                    Place place = response.getPlace();

                    // latitude and longitude of typed area
                    LatLng latLngOfPlace = place.getLatLng();

                    // user's last known location
                    LatLng mlast = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());


                    // get place type and capitalize the first letter
                    String myString = place.getTypes().get(0).toString();
                    String typeOfPlace = myString.substring(0, 1).toUpperCase() + myString.substring(1).toLowerCase();

                    // in case of Arabic Language translate to Arabic
                    if (loadData().equals("Ar")) {
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


                    int distanceToPlace = (int)SphericalUtil.computeDistanceBetween(mlast, latLngOfPlace);

                    // pass data to Trip Information Activity
                    Intent intent = new Intent(getApplicationContext(), TripInformation.class);
                    intent.putExtra("PlaceName" , place.getName() );
                    intent.putExtra("Distance" , distanceToPlace);
                    intent.putExtra("Direction" , "" );
                    intent.putExtra("Latitude" ,  latLngOfPlace.latitude );
                    intent.putExtra("Longitude" ,  latLngOfPlace.longitude );
                    intent.putExtra("typeOfPlace" , typeOfPlace );


                    if (latLngOfPlace != null) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngOfPlace, DEFAULT_ZOOM));
                    }


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

            @Override
            public void OnItemDeleteListener(int position, View v) {

            }
        });




    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    // [START maps_current_place_on_save_instance_state]
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }
    // [END maps_current_place_on_save_instance_state]

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getLocationPermission();
        updateLocationUI();

        //  moves location button to bottom right
        if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0,0,30,270);
            if(loadData().equals("Ar"))
            locationButton.setContentDescription("مكاني");

        }



        //check if gps is enabled or not and then request user to enable it
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(SearchPlaces.this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(SearchPlaces.this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getDeviceLocation();
            }
        });

        task.addOnFailureListener(SearchPlaces.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    try {
                        resolvable.startResolutionForResult(SearchPlaces.this, 51);
                    } catch (IntentSender.SendIntentException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                if (materialSearchBar.isSuggestionsVisible())
                    materialSearchBar.clearSuggestions();
                if (materialSearchBar.isSearchOpened())
                    materialSearchBar.closeSearch();
                return false;
            }
        });
    }
    // on gps enabled
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 51) {
            if (resultCode == RESULT_OK) {
                getDeviceLocation();
            }
        }

    }

    public void microphoneButton (View view){
        openVoiceRecognizer();
    }

    @SuppressLint("MissingPermission")
    private void getDeviceLocation() {
        mFusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            mLastKnownLocation = task.getResult();
                            if (mLastKnownLocation != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            } else {
                                final LocationRequest locationRequest = LocationRequest.create();
                                locationRequest.setInterval(10000);
                                locationRequest.setFastestInterval(5000);
                                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                                locationCallback = new LocationCallback() {
                                    @Override
                                    public void onLocationResult(LocationResult locationResult) {
                                        super.onLocationResult(locationResult);
                                        if (locationResult == null) {
                                            return;
                                        }
                                        mLastKnownLocation = locationResult.getLastLocation();
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                        mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
                                    }
                                };
                                mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);

                            }
                        } else {
                            Log.e("TAG" , "Enable to get last location");
                        }
                    }
                });
    }



    /**
     * Prompts the user for permission to use the device location.
     */
    // [START maps_current_place_location_permission]
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
    // [END maps_current_place_location_permission]


 //when voice search is used make request and fetch suggestions list
    private void openVoiceRecognizer() {
        final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        if(loadData().equals("Ar")) {
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar-EG");
        }
        else {
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
        }
        speechRecognizer.startListening(speechRecognizerIntent);
        // what happens when he starts speaking
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {

                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                materialSearchBar.openSearch();
                materialSearchBar.setText(data.get(0));
                // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
                // and once again when the user makes a selection (for example when calling fetchPlace()).
                AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();


                // Use the builder to create a FindAutocompletePredictionsRequest.
                FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                        .setCountries("SA")
                        .setSessionToken(token)
                        .setQuery(data.get(0))
                        .build();
                List<String> suggestionsList = new ArrayList<>();
                placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
                    predictionList = response.getAutocompletePredictions();
                    for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                        Log.i("TAG", prediction.getPlaceId());
                        Log.i("TAG", prediction.getPrimaryText(null).toString());
                        suggestionsList.add(prediction.getFullText(null).toString());
                    }
                    materialSearchBar.updateLastSuggestions(suggestionsList);
                    if (!materialSearchBar.isSuggestionsVisible()) {
                        materialSearchBar.showSuggestionsList();
                    }
                }).addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        Log.e("TAG", "Place not found: " + apiException.getStatusCode());
                    }
                });
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });

    }

    /**
     * Handles the result of the request for location permissions.
     */
    // [START maps_current_place_on_request_permissions_result]

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
                updateLocationUI();
            }

            case PERMISSIONS_REQUEST_RECOGNIZER: {
                if (requestCode == 6 && grantResults.length > 0 ){
                    if(grantResults[0] == PackageManager.PERMISSION_GRANTED){

                    }

                }
            }
        }

    }
    // [END maps_current_place_on_request_permissions_result]

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    // [START maps_current_place_update_location_ui]
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }
    // [END maps_current_place_update_location_ui]




    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},6);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (speechRecognizer!=null)
        speechRecognizer.destroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (speechRecognizer!=null)
        speechRecognizer.destroy();
    }

    @Override
    protected void onResume() {
        super.onStop();
        if (speechRecognizer!=null)
        speechRecognizer.destroy();
    }

    public String loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String currentLanguage = sharedPreferences.getString(LANGUAGE, "Ar");
        return currentLanguage;
    }

    // go to button listener, direct to google based based on a car or walk mode
    public void goToButton(View view){
                    LatLng currentMarkerLocation = mMap.getCameraPosition().target;
                    // redirect to google maps
                    if (loadData().equals("Ar")) {
                        String[] ListItems = new String[]{"سيارة", "سيرا"};
                        new AlertDialog.Builder(SearchPlaces.this)
                                .setTitle("اختر وسيلة النقل")
                                .setItems(ListItems, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == 0) {

                                            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + currentMarkerLocation.latitude + "," + currentMarkerLocation.longitude + "&mode=d&avoid=tfh");
                                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                            mapIntent.setPackage("com.google.android.apps.maps");

                                            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                                                startActivity(mapIntent);


                                            } else {
                                                Toast.makeText(SearchPlaces.this, "Please install Google Maps", Toast.LENGTH_LONG).show();
                                            }
                                        } else {

                                            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + currentMarkerLocation.latitude + "," + currentMarkerLocation.longitude + "&mode=w&avoid=tfh");
                                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                            mapIntent.setPackage("com.google.android.apps.maps");

                                            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                                                startActivity(mapIntent);


                                            } else {
                                                Toast.makeText(SearchPlaces.this, "Please install Google Maps", Toast.LENGTH_LONG).show();
                                            }

                                        }
                                    }
                                })
                                .show();



                    }
                    else {
                        String[] ListItems = new String[]{"Car", "Walk"};
                        new AlertDialog.Builder(SearchPlaces.this)
                                .setTitle("Choose a transportation")
                                .setItems(ListItems, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == 0) {

                                            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + currentMarkerLocation.latitude + "," + currentMarkerLocation.longitude + "&mode=d&avoid=tfh");
                                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                            mapIntent.setPackage("com.google.android.apps.maps");

                                            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                                                startActivity(mapIntent);


                                            } else {
                                                Toast.makeText(SearchPlaces.this, "Please install Google Maps", Toast.LENGTH_LONG).show();
                                            }
                                        } else {

                                            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + currentMarkerLocation.latitude + "," + currentMarkerLocation.longitude + "&mode=w&avoid=tfh");
                                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                            mapIntent.setPackage("com.google.android.apps.maps");

                                            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                                                startActivity(mapIntent);


                                            } else {
                                                Toast.makeText(SearchPlaces.this, "Please install Google Maps", Toast.LENGTH_LONG).show();
                                            }

                                        }
                                    }
                                })
                                .show();


                    }

    }

    public void BuildingsButton(View view ){

        ArrayList<String> buildingsNames= new ArrayList<>();
        if (loadData().equals("Ar")) {
            buildingsNames.add("كلية ادارة الاعمال");
            buildingsNames.add("كلية العمارة والتخطيط");
            buildingsNames.add("كلية علوم الأغذية والزراعة");
            buildingsNames.add("كلية علوم الحاسب والمعلومات");
            buildingsNames.add("كلية الهندسة");
            buildingsNames.add("كلية العلوم مبنى 4");
            buildingsNames.add("كلية العلوم مبنى 5");
            buildingsNames.add("مطعم الطلاب");
            buildingsNames.add("برنامج الوصول الشامل");
        }
        else {
            buildingsNames.add("College of Business Administration");
            buildingsNames.add("College of Architecture & Planning");
            buildingsNames.add("College of Food and Agriculture Sciences");
            buildingsNames.add("College of Computer and Information Sciences Building 31");
            buildingsNames.add("College of Engineering");
            buildingsNames.add("College of Science Building 4");
            buildingsNames.add("College of Science Building 5");
            buildingsNames.add("Student Restaurant");
            buildingsNames.add("Universal Access Program");
        }
        int typeActionBar = -2 ; // flag to indentify the name of the action bar in the PlacesList activity
        int flag = 0 ; // there are two actions to perform in PlacesList either nearby places list or Building list


        // pass data and move to PlacesList activity
        Intent intent = new Intent(getApplicationContext(), PlacesList.class);
        intent.putStringArrayListExtra("BuildingNames" , buildingsNames);
        intent.putExtra("typeActionBar" , typeActionBar);
        intent.putExtra("flag" , flag);
        startActivity(intent);




    }


}






