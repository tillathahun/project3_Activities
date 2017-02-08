package com.mylesspencertyler.project3activities.service.activityrecognition;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.mylesspencertyler.project3activities.ActivityType;

import java.util.Date;
import java.util.List;

/**
 * Created by tyler on 2/7/2017.
 */

public class ActivityRecognizedService extends IntentService {
    private int mCurrentActivity;

    public ActivityRecognizedService() {
        super("ActivityRecognizedService");
        mCurrentActivity = DetectedActivity.UNKNOWN;
    }

    public ActivityRecognizedService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            handleDetectedActivities( result.getProbableActivities() );
        }
    }

    private void handleDetectedActivities(List<DetectedActivity> probableActivities) {
        for( DetectedActivity activity : probableActivities ) {
            switch( activity.getType() ) {
                case DetectedActivity.IN_VEHICLE: {
                    Log.e( "ActivityRecogition", "In Vehicle: " + activity.getConfidence() );
                    if(activity.getConfidence() >= 70 && mCurrentActivity != DetectedActivity.IN_VEHICLE){
                        Intent intent = new Intent();
                        Date activityStarted = new Date();
                        intent.putExtra("timeStarted", activityStarted.getTime());
                        ActivityType.VEHICLE.attachTo(intent);

                        intent.setAction("com.mylesspencertyler.ACTIVITY_INTENT");
                        sendBroadcast(intent);
                        mCurrentActivity = DetectedActivity.IN_VEHICLE;
                    }
                    break;
                }

                case DetectedActivity.RUNNING: {
                    Log.e( "ActivityRecogition", "Running: " + activity.getConfidence() );
                    if(activity.getConfidence() >= 70 && mCurrentActivity != DetectedActivity.RUNNING){
                        Intent intent = new Intent();
                        Date activityStarted = new Date();
                        intent.putExtra("timeStarted", activityStarted.getTime());
                        ActivityType.RUNNING.attachTo(intent);

                        intent.setAction("com.mylesspencertyler.ACTIVITY_INTENT");
                        sendBroadcast(intent);
                    }
                    break;
                }
                case DetectedActivity.STILL: {
                    Log.e("ActivityRecogition", "Still: " + activity.getConfidence());
                    if(activity.getConfidence() >= 70 && mCurrentActivity != DetectedActivity.STILL){
                        Intent intent = new Intent();
                        Date activityStarted = new Date();
                        intent.putExtra("timeStarted", activityStarted.getTime());
                        ActivityType.STILL.attachTo(intent);

                        intent.setAction("com.mylesspencertyler.ACTIVITY_INTENT");
                        sendBroadcast(intent);
                    }
                    break;
                }
                case DetectedActivity.WALKING: {
                    Log.e( "ActivityRecogition", "Walking: " + activity.getConfidence() );
                    if(activity.getConfidence() >= 70 && mCurrentActivity != DetectedActivity.WALKING){
                        Intent intent = new Intent();
                        Date activityStarted = new Date();
                        intent.putExtra("timeStarted", activityStarted.getTime());
                        ActivityType.WALKING.attachTo(intent);

                        intent.setAction("com.mylesspencertyler.ACTIVITY_INTENT");
                        sendBroadcast(intent);
                    }
                    break;
                }
                case DetectedActivity.UNKNOWN: {
                    Log.e( "ActivityRecogition", "Unknown: " + activity.getConfidence() );
                    if(activity.getConfidence() >= 70 && mCurrentActivity != DetectedActivity.UNKNOWN){
                        Intent intent = new Intent();
                        Date activityStarted = new Date();
                        intent.putExtra("timeStarted", activityStarted.getTime());
                        ActivityType.UNKNOWN.attachTo(intent);

                        intent.setAction("com.mylesspencertyler.ACTIVITY_INTENT");
                        sendBroadcast(intent);
                    }
                    break;
                }
            }
        }
    }
}
