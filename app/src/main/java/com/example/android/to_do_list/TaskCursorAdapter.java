package com.example.android.to_do_list;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.to_do_list.R;


import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.to_do_list.data.TaskContract.TaskEntry;
import com.example.android.to_do_list.R;

/**
 * {@link TaskCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of task data as its data source. This adapter knows
 * how to create list items for each row of pet data in the {@link Cursor}.
 */
public class TaskCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link TaskCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public TaskCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find the view that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.task);

        // Find the columns of task attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(TaskEntry.COLUMN_TASK_NAME);

        // Read the task attributes from the Cursor for the current task
        String TaskName = cursor.getString(nameColumnIndex);


        // Update the TextViews with the attributes for the current pet
        nameTextView.setText(TaskName);
    }
}
