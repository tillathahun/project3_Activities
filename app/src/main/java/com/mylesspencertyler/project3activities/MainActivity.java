package com.mylesspencertyler.project3activities;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.DetectedActivity;
import com.mylesspencertyler.project3activities.service.activityrecognition.ActivityRecognizedService;

import java.text.SimpleDateFormat;

public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public GoogleApiClient mApiClient;
    boolean mIsReceiverRegistered = false;
    ActivityBroadcastReceiver mReceiver = null;
    private ActivityType mCurrentActivity;
    private TextView activity;

    private AudioActionPlayer mediaPlayer = new AudioActionPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mApiClient.connect();

        mCurrentActivity = ActivityType.UNKNOWN;
        activity = (TextView) findViewById(R.id.activity_textview);
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mIsReceiverRegistered) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
            mIsReceiverRegistered = false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mediaPlayer.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Intent intent = new Intent( this, ActivityRecognizedService.class );
        PendingIntent pendingIntent = PendingIntent.getService( this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates( mApiClient, 3000, pendingIntent );
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void updateUI(ActivityType result, long timeStarted) {
        // here we update the image, throw a toast
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        String formattedDate = df.format(timeStarted);
        Toast.makeText(this, formattedDate, Toast.LENGTH_SHORT).show();
        activity.setText(result.name().toLowerCase());
    }

    private void handleMusic(ActivityType result) {
        if(result == ActivityType.RUNNING || result == ActivityType.WALKING) {
            mediaPlayer.play(this);
        } else {
            mediaPlayer.stop();
        }
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

            handleMusic(result);
        }
    }
}
