package com.mylesspencertyler.project3activities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by myles on 2/9/17.
 */

public class ActivityTimeDbHelper extends SQLiteOpenHelper {
    private long lastEntryId;

    public ActivityTimeDbHelper(Context context) {
        super(context, ActivityTimeContract.DB_NAME, null, ActivityTimeContract.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ActivityTimeContract.TimeEntry.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(ActivityTimeContract.TimeEntry.SQL_DELETE_TABLE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void setActivityType(String activityType, String startTime) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ActivityTimeContract.TimeEntry.ACTIVITY_TYPE, activityType);
        values.put(ActivityTimeContract.TimeEntry.START_TIME, startTime);

        this.lastEntryId = db.insert(ActivityTimeContract.TimeEntry.TABLE_NAME, null, values);
    }

    public Cursor getLastActivity() {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                ActivityTimeContract.TimeEntry._ID,
                ActivityTimeContract.TimeEntry.ACTIVITY_TYPE,
                ActivityTimeContract.TimeEntry.START_TIME
        };

        String selection = ActivityTimeContract.TimeEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(this.lastEntryId) };

        String sortOrder = ActivityTimeContract.TimeEntry.START_TIME + " DESC";

        return db.query(
                ActivityTimeContract.TimeEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }
}
