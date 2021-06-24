package com.example.android.to_do_list.data;

import android.net.Uri;
import android.content.ContentResolver;
import android.provider.BaseColumns;

/**
 * API Contract for the T0-Do-List app.
 */
public final class TaskContract {

    private TaskContract() {}

    public static final String CONTENT_AUTHORITY = "com.example.android.to_do_list";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_TASKS = "tasks";

    public static final class TaskEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_TASKS);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TASKS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TASKS;

        public final static String TABLE_NAME = "tasks";

        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_TASK_NAME ="name";

        public final static String COLUMN_TASK_DESCRIPTION = "description";

        public final static String COLUMN_PRIORITY = "priority";

        public final static String COLUMN_DATE = "date";

        public final static String COLUMN_TIME = "time";

        public final static String COLUMN_COMPLETED = "completed";

    }

}

