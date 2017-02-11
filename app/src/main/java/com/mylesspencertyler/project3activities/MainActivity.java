package com.mylesspencertyler.project3activities;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mylesspencertyler.project3activities.service.activityrecognition.ActivityRecognizedService;

import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private LocationRequest mLocationRequest;

    public GoogleApiClient mApiClient;
    boolean mIsReceiverRegistered = false;
    ActivityBroadcastReceiver mReceiver = null;
    private ActivityType mCurrentActivity;
    private TextView activity;


    private void handleNewLocation(Location location) {
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);
        Log.d("PrintLat", "Lat: " + currentLatitude);
        Log.d("PrintLong", "Long: " + currentLongitude);
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title("I am here!");
        mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, (float)16.0));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
	// Head
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(0)
                .setMaxWaitTime(1)
                .setFastestInterval(0)
                .setSmallestDisplacement(0);
	// =====

        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mApiClient.connect();

        mCurrentActivity = ActivityType.UNKNOWN;
        activity = (TextView) findViewById(R.id.activity_textview);
        ActivityTime.setContext(getBaseContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mIsReceiverRegistered) {
            if (mReceiver == null)
                mReceiver = new ActivityBroadcastReceiver();
            registerReceiver(mReceiver, new IntentFilter("com.mylesspencertyler.ACTIVITY_INTENT"));
            mIsReceiverRegistered = true;
        }
        //setUpMap();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mIsReceiverRegistered) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
            mIsReceiverRegistered = false;
        }
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Intent intent = new Intent( this, ActivityRecognizedService.class );
        PendingIntent pendingIntent = PendingIntent.getService( this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates( mApiClient, 3000, pendingIntent );

        //Log.i(TAG, "Location services connected.");

        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            mMap.setMyLocationEnabled(true);
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1600);

        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        //if (location == null) {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        //}

        handleNewLocation(location);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                //e.printStackTrace();
            }
        } else {
            //Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    private void updateUI(ActivityType result, long timeStarted) {
        // Insert into database
        new ActivityTime(result.name().toLowerCase());
        ActivityTime last = ActivityTime.getLastActivityTime();
        Date current = new Date(timeStarted);
        long diff = current.getTime() - last.getStartTime().getTime();
        long diffSecs = diff / 1000 % 60;
        long diffMins = diff / (60 * 1000) % 60;

        String text = "";
        switch (last.getActivityType()) {
            case "walking":
                text = "You walked for ";
                break;
            case "running":
                text = "You were running for ";
                break;
            case "vehicle":
                text = "You were in a vehicle for ";
                break;
            case "still":
                text = "You were standing still for ";
                break;
            default:
                text = "You were doing something unknown for ";

        }

        text = text + diffMins + ":" + diffSecs;

        activity.setText(text);
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();

    }

    private class ActivityBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ActivityType result = ActivityType.detachFrom(intent);
            long timeStarted = intent.getLongExtra("timeStarted", -1);

            if(result != mCurrentActivity) {
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 500 milliseconds
                v.vibrate(500);
                mCurrentActivity = result;
                updateUI(result, timeStarted);
            }
        }
	// master
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);

        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        MarkerOptions options = new MarkerOptions()
                .position(new LatLng(0,0))
                .title("I am here!");
    }

    @Override
    public void onLocationChanged(Location location) {
        mMap.clear();
        handleNewLocation(location);
    }


}
