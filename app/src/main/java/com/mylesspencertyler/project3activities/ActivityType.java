package com.mylesspencertyler.project3activities;

import android.content.Intent;

/**
 * Created by tyler on 2/8/2017.
 */

public enum ActivityType {
    VEHICLE,
    RUNNING,
    STILL,
    WALKING,
    UNKNOWN;

    private static final String name = ActivityType.class.getName();
    public void attachTo(Intent intent) {
        intent.putExtra(name, ordinal());
    }
    public static ActivityType detachFrom(Intent intent) {
        if(!intent.hasExtra(name)) throw new IllegalStateException();
        return values()[intent.getIntExtra(name, -1)];
    }
}
