package com.mylesspencertyler.project3activities;

import android.content.Context;
import android.database.Cursor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by myles on 2/10/17.
 */

public class ActivityTime {
    private static final String dateFormat = "yyyyMMddhhmmss";
    private static ActivityTimeDbHelper dbHelper = null;
    private static Context context;
    private String activityType;
    private Date startTime;

    ActivityTime(String activityType) {
        this.activityType = activityType;
        this.startTime = Calendar.getInstance().getTime();

        SimpleDateFormat formatter = new SimpleDateFormat(ActivityTime.dateFormat);
        ActivityTimeDbHelper dbHelper = ActivityTime.getDbHelper();
        dbHelper.setActivityType(this.activityType, formatter.format(this.startTime));
    }

    ActivityTime(String activityType, Date startTime) {
        this.activityType = activityType;
        this.startTime = startTime;
    }

    private static ActivityTimeDbHelper getDbHelper() {
        if(ActivityTime.dbHelper == null) {
            ActivityTime.dbHelper = new ActivityTimeDbHelper(ActivityTime.context);
        }

        return ActivityTime.dbHelper;
    }

    public static void setContext(Context context) {
        ActivityTime.context = context;
    }

    public static ActivityTime getLastActivityTime() {
        SimpleDateFormat formatter = new SimpleDateFormat(ActivityTime.dateFormat);
        ActivityTimeDbHelper dbHelper = ActivityTime.getDbHelper();
        Cursor cr = dbHelper.getLastActivity();
        String activityType = cr.getString(cr.getColumnIndex(ActivityTimeContract.TimeEntry.ACTIVITY_TYPE));
        String startTimeString = cr.getString(cr.getColumnIndex(ActivityTimeContract.TimeEntry.START_TIME));
        try {
            Date startTime = formatter.parse(startTimeString);
            return new ActivityTime(activityType, startTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getActivityType() {
        return this.activityType;
    }

    public Date getStartTime() {
        return this.startTime;
    }
}