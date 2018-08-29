package com.example.treasurehuntapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.treasurehuntapp.client.AppContext;
import com.example.treasurehuntapp.client.AppPermissions;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.time.LocalDateTime;

import treasurehunt.client.RunThroughRESTMethods;
import treasurehunt.model.Course;
import treasurehunt.model.RunThrough;
import treasurehunt.model.StepComposite;
import treasurehunt.model.StepLeaf;
import treasurehunt.model.marshalling.JsonObjectMapperBuilder;
import treasurehunt.sqlite.PersistenceManager;
import treasurehunt.sqlite.RunThroughPersistentFactory;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class RunthroughActivity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 1000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private TextView stepDescription;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            stepDescription.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    private AppContext appContext;

    // données treasurehunt.model
    private Course course;
    private RunThrough runThrough = new RunThrough();

    // gestion de la persistance
    PersistenceManager persistenceManager = new PersistenceManager(RunthroughActivity.this);

    // service de localisation
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int REQUEST_CHECK_SETTINGS = 2;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest = new LocationRequest();
    private LocationCallback mLocationCallback;

    // résolution de l'enigme une fois une étape atteinte physiquement
    static final int RIDDLE_ANSWER_REQUEST_CODE = 1;

    private Button finishRunThrough;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        appContext = AppContext.getInstance(RunthroughActivity.this);

        AppPermissions.checkLocationPermission(this);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_runthrough);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        stepDescription = findViewById(R.id.stepDescription);
        finishRunThrough = findViewById(R.id.finishRunThrough);
        finishRunThrough.setVisibility(View.GONE);
        finishRunThrough.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // initialisation de variables : le joueur est invité à se présenter à l'étape de départ, la course démarrera réellement lorsque
        // celui-ci sera sur zone et aura déclaré être prêt.

        // récupération de la course fournie en paramètre
        Bundle bundle = getIntent().getExtras();
        ObjectMapper mapper = JsonObjectMapperBuilder.buildJacksonObjectMapper();
        if (bundle == null) {
            finish();
        }
        else
        {
            try {
                course = mapper.readValue(bundle.getString("serializedCourse"),Course.class);
            } catch (IOException e) {
                e.printStackTrace();
                finish();
            }
        }
        runThrough.setAccountEmail(appContext.account.email);
        runThrough.setCourseId(course.id);
        runThrough.setCurrentStep(course.start);
        // fin initialisation des variables

        setTitle(String.format("Parcours de %s",course.name));
        showCurrentStepInfo();

        // Set up the user interaction to manually show or hide the system UI.
        stepDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.backToFullScreen).setOnTouchListener(mDelayHideTouchListener);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // dix mètres de proximité
                    Toast.makeText(RunthroughActivity.this,String.valueOf(location.distanceTo(buildCurrentStepLocation())),Toast.LENGTH_LONG);
                    if (location.distanceTo(buildCurrentStepLocation()) <= 10){
                        // demander à l'utilisateur la résolution de l'énigme et stopper l'abonnement au service de localisation
                        stopLocationUpdates();
                        showCurrentRiddleToUser();
                    }
                }
            }

        };
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        getCurrentLocationSettings();
        startLocationUpdates();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        stepDescription.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
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

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(RunthroughActivity.this, "Permission refusée :", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
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
                Toast.makeText(RunthroughActivity.this, "succes  : " + LocationRequest.PRIORITY_HIGH_ACCURACY, Toast.LENGTH_LONG).show();
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
                        Toast.makeText(RunthroughActivity.this, "failure  : " + e.toString(), Toast.LENGTH_LONG).show();
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(RunthroughActivity.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });

    }

    protected LocationRequest createLocationRequest() {
        mLocationRequest.setInterval(2000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback,
                null /* Looper */);
    }

    @SuppressLint("MissingPermission")
    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    private void showCurrentRiddleToUser() {
        // lancer le parcours d'une course
        Intent intent = new Intent(RunthroughActivity.this, RiddleActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("jokersLeft",(course.jokersAllowed - runThrough.getJokersUsed()));
        ObjectMapper mapper = JsonObjectMapperBuilder.buildJacksonObjectMapper();
        try {
            bundle.putString("serializedRiddle", mapper.writeValueAsString(runThrough.getCurrentStep().riddle));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return;
        }
        intent.putExtras(bundle);
        startActivityForResult(intent,RIDDLE_ANSWER_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RIDDLE_ANSWER_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                runThrough.validateCurrentStepResolution(LocalDateTime.now(), data.getBooleanExtra("jokerUsed",false));
            } else {
                runThrough.validateCurrentStepResolution(LocalDateTime.now(), true);
            }
        }
        if (runThrough.getCurrentStep() instanceof StepLeaf) {
            // course terminée !
            showEndedRunThroughInfo();
        } else {
            // passer à l'étape suivante
            StepComposite currentStep = (StepComposite) runThrough.getCurrentStep();
            // ici il faut proposer le choix de l'étape à suivre
            runThrough.setCurrentStep(currentStep.getNextStep(currentStep.getNextStepsIds().iterator().next()));
            showCurrentStepInfo();
            // reprise de l'abonnement au service de localisation
            startLocationUpdates();
        }
        // dans tous les cas il faut enregistrer les données de parcours
        SendRunThroughTask sendTask = new SendRunThroughTask(runThrough);
        sendTask.execute();
    }

    private Location buildCurrentStepLocation() {
        Location result = new Location("");
        result.setLatitude(runThrough.getCurrentStep().latitude);
        result.setLongitude(runThrough.getCurrentStep().longitude);
        return result;
    }

    private void showCurrentStepInfo() {
        stepDescription.setText(runThrough.getCurrentStep().description);
    }

    private void showEndedRunThroughInfo() {
        stepDescription.setText(String.format("Parcours terminé !\n\nVotre score : %d",runThrough.getScore(course)));
        finishRunThrough.setVisibility(View.VISIBLE);
    }

    /**
     * Represents an asynchronous login/registration task used to get the nearest course
     * the user.
     */
    private class SendRunThroughTask extends AsyncTask<Void, Void, Boolean> {

        RunThrough runThroughToSend;

        SendRunThroughTask(RunThrough runThroughToSend) {
            this.runThroughToSend = runThroughToSend;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                return RunThroughRESTMethods.put(appContext.getRequestQueue(),runThroughToSend);
            } catch (Exception e) {
                return false;
            }
            

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                Toast.makeText(RunthroughActivity.this, "Données de parcours envoyées", Toast.LENGTH_LONG).show();
            } else {
                persistenceManager.insertOrUpdateObject(new RunThroughPersistentFactory()
                        .makePersistentObject(runThroughToSend.getId(),runThroughToSend));
            }
        }

        @Override
        protected void onCancelled() {

        }
    }
}
