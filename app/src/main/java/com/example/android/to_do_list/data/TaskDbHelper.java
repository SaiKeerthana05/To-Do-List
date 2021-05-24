/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

    /** Name of the database file */
    private static final String DATABASE_NAME = "tasker.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link TaskDbHelper}.
     *
     * @param context of the app
     */
    public TaskDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the tasks table
        String SQL_CREATE_TASKS_TABLE =  "CREATE TABLE " + TaskEntry.TABLE_NAME + " ("
                + TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TaskEntry.COLUMN_TASK_NAME + " TEXT NOT NULL, "
                + TaskEntry.COLUMN_TASK_DESCRIPTION + " TEXT);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_TASKS_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}