package com.example.android.to_do_list;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import android.widget.CursorAdapter;

import com.example.android.to_do_list.data.TaskContract.TaskEntry;

import java.awt.font.TextAttribute;

public class TaskCursorAdapter extends CursorAdapter {

    public TaskCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameTextView = (TextView) view.findViewById(R.id.task);
        ImageView completionTick = (ImageView) view.findViewById(R.id.completed);

        int nameColumnIndex = cursor.getColumnIndex(TaskEntry.COLUMN_TASK_NAME);

        int priorityColumnIndex = cursor.getColumnIndex(TaskEntry.COLUMN_PRIORITY);

        int completedColumnIndex = cursor.getColumnIndex(TaskEntry.COLUMN_COMPLETED);

        String TaskName = cursor.getString(nameColumnIndex);

        String priority = cursor.getString(priorityColumnIndex);

        int completedStatus = cursor.getInt(completedColumnIndex);

        TextView invisibleText = (TextView) view.findViewById(R.id.invisibleText);
        GradientDrawable priorityCircle = (GradientDrawable) invisibleText.getBackground();
        int colorId;
        if(priority.equals("High")) colorId = R.color.high;
        else if(priority.equals("Medium")) colorId = R.color.medium;
        else if(priority.equals("Low")) colorId = R.color.low;
        else colorId = R.color.none;
        int backgroundColor = ContextCompat.getColor(view.getContext(),colorId);
        priorityCircle.setColor(backgroundColor);

        nameTextView.setText(TaskName);

        if(completedStatus==1) {
            int viewBackgroundColor = ContextCompat.getColor(view.getContext(),R.color.gray);
            view.setBackgroundColor(viewBackgroundColor);
            completionTick.setVisibility(View.VISIBLE);
        }
        else {
            int viewBackgroundColor = ContextCompat.getColor(view.getContext(),R.color.white);
            view.setBackgroundColor(viewBackgroundColor);
            completionTick.setVisibility(View.GONE);
        }
    }
}
