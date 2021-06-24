package com.example.android.to_do_list.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.to_do_list.data.TaskContract.TaskEntry;

/**
 * Database helper for To-Do-List app. Manages database creation and version management.
 */
public class TaskDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = TaskDbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "tasker.db";

    private static final int DATABASE_VERSION = 1;

    public TaskDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_TASKS_TABLE =  "CREATE TABLE " + TaskEntry.TABLE_NAME + " ("
                + TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TaskEntry.COLUMN_TASK_NAME + " TEXT NOT NULL, "
                + TaskEntry.COLUMN_TASK_DESCRIPTION + " TEXT,"
                + TaskEntry.COLUMN_PRIORITY + " TEXT,"
                + TaskEntry.COLUMN_DATE + " TEXT, "
                + TaskEntry.COLUMN_TIME + " TEXT, "
                + TaskEntry.COLUMN_COMPLETED + " INTEGER DEFAULT 0)";

        db.execSQL(SQL_CREATE_TASKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TaskEntry.TABLE_NAME);
        onCreate(db);
    }
}