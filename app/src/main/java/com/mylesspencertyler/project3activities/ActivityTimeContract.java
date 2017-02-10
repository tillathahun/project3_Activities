package com.mylesspencertyler.project3activities;

import android.provider.BaseColumns;

/**
 * Created by myles on 2/9/17.
 */

public class ActivityTimeContract {
    public static final String DB_NAME = "ActivityTime.db";
    public static final int DB_VERSION = 1;

    private ActivityTimeContract() {}

    public static class TimeEntry implements BaseColumns {
        public static final String TABLE_NAME = "ActivityTimes";
        public static final String ACTIVITY_TYPE = "activityType";
        public static final String START_TIME = "startTime";
        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TimeEntry.TABLE_NAME + " (" +
                TimeEntry._ID + " INTEGER PRIMARY KEY," +
                TimeEntry.ACTIVITY_TYPE + " TEXT," +
                TimeEntry.START_TIME + " TEXT)";
        public static final String SQL_DELETE_TABLE =
                "DROP TABLE IF EXISTS " + TimeEntry.TABLE_NAME;
    }
}
