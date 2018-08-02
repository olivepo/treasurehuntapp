package com.example.treasurehuntapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.treasurehuntapp.client.AppContext;
import com.example.treasurehuntapp.createhunt.CreateHuntActivity;
import com.example.treasurehuntapp.client.AppPermissions;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.common.api.GoogleApiClient;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;
import java.util.List;

import treasurehunt.client.Configuration;
import treasurehunt.client.CourseRESTMethods;
import treasurehunt.model.Course;
import treasurehunt.model.marshalling.JsonObjectMapperBuilder;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, View.OnClickListener, GoogleMap.OnMarkerClickListener {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int REQUEST_CHECK_SETTINGS = 2;
    private static final String REQUESTING_LOCATION_UPDATES_KEY = "";
    private static final int LOCATION = 2;
    private GoogleMap mMap;

    private FusedLocationProviderClient mFusedLocationClient;

    private Location mLastLocation;
    private LocationRequest mLocationRequest = new LocationRequest();
    private LocationCallback mLocationCallback;
    private boolean mRequestingLocationUpdates = true;

    private HashMap<String,Course> markersCourse = new HashMap<String,Course>(); // key = markerId, value = course

    private AppContext appContext;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private MapsActivity.NearestCourseTask mNearestCourseTask = null;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        appContext = AppContext.getInstance(MapsActivity.this);

        super.onCreate(savedInstanceState);
        AppPermissions.checkLocationPermission(this);
        setContentView(R.layout.activity_maps);

        Button create = findViewById(R.id.createButton);
        create.setOnClickListener(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //restore save values
        updateValuesFromBundle(savedInstanceState);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        defLocUpdateCallBack();

    }


    private void defLocUpdateCallBack() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location.distanceTo(mLastLocation)>Configuration.RadiusInMetres){
                        mNearestCourseTask=new NearestCourseTask(location.getLatitude(),location.getLongitude());
                        mNearestCourseTask.execute();
                    }
                    mLastLocation=location;
                    // Update UI with location data
                    // ...
                 /*   LatLng updateLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(updateLatLng).title("location update"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(updateLatLng, 17));*/


                }
            }

        };
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        setUpMap();

    }


    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    getLastLocation();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MapsActivity.this, "Permission refusée :", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }


    @SuppressLint("MissingPermission")
    private void setUpMap() {

        mMap.setOnMarkerClickListener(this);

        getLastLocation();
        getCurrentLocationSettings();


    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    // Logic to handle location object
                    mLastLocation = location;
                }
                if (mLastLocation != null) {

                    LatLng successLastLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

                    mMap.addMarker(new MarkerOptions().position(successLastLatLng).title("Ma position"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(successLastLatLng, 17));


                    Toast.makeText(MapsActivity.this, "Ma position : " + successLastLatLng.toString(), Toast.LENGTH_LONG).show();
                    mNearestCourseTask=new NearestCourseTask(mLastLocation.getLatitude(),mLastLocation.getLongitude());
                    mNearestCourseTask.execute();


                } else {
//                    LatLng failedLastLatLng = new LatLng(48.8666846, 2.3553182);
//                    Toast.makeText(MapsActivity.this, "Nw Location  : " + failedLastLatLng.toString(), Toast.LENGTH_LONG).show();
//
//                    mMap.addMarker(new MarkerOptions().position(failedLastLatLng).title("Le CNAM"));
//                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(failedLastLatLng, 17));

                }
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    protected LocationRequest createLocationRequest() {
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    private void getCurrentLocationSettings() {
        mLocationRequest = createLocationRequest();

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);


        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
                Toast.makeText(MapsActivity.this, "succes  : " + LocationRequest.PRIORITY_HIGH_ACCURACY, Toast.LENGTH_LONG).show();
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        Toast.makeText(MapsActivity.this, "failure  : " + e.toString(), Toast.LENGTH_LONG).show();
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MapsActivity.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback,
                null /* Looper */);
    }


    //save the state of the activity
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY,
                mRequestingLocationUpdates);

        super.onSaveInstanceState(outState);
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }

        // Update the value of mRequestingLocationUpdates from the Bundle.
        if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
            mRequestingLocationUpdates = savedInstanceState.getBoolean(
                    REQUESTING_LOCATION_UPDATES_KEY);
        }

        // ...

        // Update UI to match restored state
        updateUI();
    }

    private void updateUI() {
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == LOCATION) {

            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                mRequestingLocationUpdates = true;
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.

                // Do something with the contact here (bigger example below)

            } else {
                mRequestingLocationUpdates = false;
            }
        }
    }


    public void onClick(View view) {

        switch (view.getId()) {
            case (R.id.createButton):
                create(view);
                finish();
                break;

            default:
                break;
        }
    }

    private void create(View view) {
        //TODO appel creation de chasse au trésor

        startActivity(new Intent(MapsActivity.this,CreateHuntActivity.class));

    }


    /**
     * Called when pointer capture is enabled or disabled for the current window.
     *
     * @param hasCapture True if the window has pointer capture.
     */
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        // lancer le parcours d'une course
        Intent intent = new Intent(MapsActivity.this, RunthroughActivity.class);
        Bundle bundle = new Bundle();
        ObjectMapper mapper = JsonObjectMapperBuilder.buildJacksonObjectMapper();
        try {
            bundle.putString("serializedCourse", mapper.writeValueAsString(markersCourse.get(marker.getId())));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return false;
        }
        intent.putExtras(bundle);
        startActivity(intent);
        return true;
    }

    /**
     * Represents an asynchronous login/registration task used to get the nearest course
     * the user.
     */
    private class NearestCourseTask extends AsyncTask<Void, Void, Boolean> {

        private final double latitude;
        private final double longitude;
        private List<Course> listCourses;

        NearestCourseTask(double latitude, double longitude) {
            this.latitude =latitude;
            this.longitude=longitude;

        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {

                listCourses = CourseRESTMethods.getNearestCourses(appContext.getRequestQueue(), latitude,longitude,
                Configuration.RadiusInMetres);
            } catch (Exception e) {
                return false;
            }

            return listCourses != null;

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mNearestCourseTask = null;
         //   showProgress(false);

            if (success) {
                appContext.nearestCourse = listCourses;
                for (Course course : listCourses){

                    LatLng courseLatLong = new LatLng(course.start.latitude, course.start.longitude);
                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(courseLatLong)
                            .title(course.name)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                    markersCourse.put(marker.getId(),course);
                }
            }
        }

        @Override
        protected void onCancelled() {
            mNearestCourseTask = null;
          //  showProgress(false);
        }
    }
}