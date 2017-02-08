package com.mylesspencertyler.project3activities.service.activityrecognition;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by tyler on 2/7/2017.
 */

public class ActivityRecognizedService extends IntentService {
    public ActivityRecognizedService() {
        super("ActivityRecognizedService");
    }

    public ActivityRecognizedService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
    }
}
