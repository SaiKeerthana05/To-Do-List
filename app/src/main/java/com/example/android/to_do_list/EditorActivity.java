package com.example.android.to_do_list;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.LoaderManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.android.to_do_list.data.AlarmReceiver;
import com.example.android.to_do_list.data.TaskContract.TaskEntry;

import java.util.Calendar;


/**
 * Allows user to create a new task or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private static final int EXISTING_TASK_LOADER = 0;
    private static final int RQS_1 = 1;

    private Uri mCurrentTaskUri;

    private EditText mNameEditText;

    private EditText mDescriptionEditText;

    private RadioGroup mpriorityGroup;

    private boolean mTaskHasChanged = false;

    private TextView mDateText;

    private TextView mTimeText;

    private NotificationManagerCompat notificationManager;

    public  Calendar mCalendar = Calendar.getInstance();

    private Button mSetAlarm;

    private Button mCancelAlarm;

    private CheckBox mCompleted;

    static Calendar now = Calendar.getInstance();
    public static int mYear = now.get(Calendar.YEAR);
    public static int mMonth = now.get(Calendar.MONTH);
    public static int mDay  = now.get(Calendar.DAY_OF_MONTH);

    public static int mHour = now.get(Calendar.HOUR_OF_DAY);
    public static int mMinute = now.get(Calendar.MINUTE);

    final

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mTaskHasChanged = true;
            return false;
        }
    };

    private RadioGroup.OnCheckedChangeListener mChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            mTaskHasChanged = true;
        }
    };

    private CompoundButton.OnCheckedChangeListener mCheckBoxListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            mTaskHasChanged = true;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        notificationManager = NotificationManagerCompat.from(this);

        Intent intent = getIntent();
        mCurrentTaskUri = intent.getData();

        if (mCurrentTaskUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_task));

            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_task));
            getLoaderManager().initLoader(EXISTING_TASK_LOADER, null, this);
        }

        mNameEditText = (EditText) findViewById(R.id.edit_task_name);
        mDescriptionEditText = (EditText) findViewById(R.id.edit_task_description);
        mpriorityGroup = (RadioGroup) findViewById(R.id.priority);
        mCompleted = (CheckBox) findViewById(R.id.completedCheckbox);

        mNameEditText.setOnTouchListener(mTouchListener);
        mDescriptionEditText.setOnTouchListener(mTouchListener);
        mpriorityGroup.setOnCheckedChangeListener(mChangeListener);
        mCompleted.setOnCheckedChangeListener(mCheckBoxListener);

        mDateText= (TextView) findViewById(R.id.edit_task_due_date);
        mDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });

        mTimeText = (TextView) findViewById(R.id.edit_task_due_time);
        mTimeText.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                showTimePicker();
            }
        });

        mSetAlarm = (Button) findViewById(R.id.setAlarm);
        mSetAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar current = Calendar.getInstance();
                Calendar cal = Calendar.getInstance();
                cal.set(mYear,mMonth,mDay,mHour,mMinute);
                if(cal.compareTo(current)<=0) {
                    Toast.makeText(view.getContext(),"Invalid Date & Time",Toast.LENGTH_LONG).show();
                }
                else{
                     setAlarm(cal);
                }
            }
        });

        mCancelAlarm = (Button) findViewById(R.id.cancelAlarm);
        mCancelAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelAlarm();
            }
        });

    }


    /**
     * Get user input from editor and save pet into database.
     */
    private void saveTask() {

        String nameString = mNameEditText.getText().toString().trim();
        String descriptionString = mDescriptionEditText.getText().toString().trim();
        String priorityString;
        String dateString = getString(R.string.hint_task_due_date);
        String timeString = mTimeText.getText().toString();
        int checked = (mCompleted.isChecked())?1:0;

        int id = mpriorityGroup.getCheckedRadioButtonId();
        RadioButton priorityButton = (RadioButton) findViewById(id);
        priorityString = priorityButton.getText().toString();

        String date = mDateText.getText().toString();
        if(!date.equals(getString(R.string.hint_task_due_date))) {dateString = date.substring(6) + "/";
        dateString += date.substring(3,5) + "/" + date.substring(0,2);}

        if (mCurrentTaskUri == null &&
                TextUtils.isEmpty(nameString)){
            Toast.makeText(this,"Task not mentioned", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(TaskEntry.COLUMN_TASK_NAME, nameString);

        String description = "Nothing provided!";
        if (!TextUtils.isEmpty(descriptionString)) {
            description = descriptionString;
        }

        values.put(TaskEntry.COLUMN_TASK_DESCRIPTION, description);
        values.put(TaskEntry.COLUMN_PRIORITY, priorityString);
        values.put(TaskEntry.COLUMN_DATE,dateString);
        values.put(TaskEntry.COLUMN_TIME,timeString);
        values.put(TaskEntry.COLUMN_COMPLETED,checked);

        if (mCurrentTaskUri == null) {
            Uri newUri = getContentResolver().insert(TaskEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_task_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_task_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentTaskUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_update_task_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_update_task_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentTaskUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveTask();
                finish();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mTaskHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        if (!mTaskHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                TaskEntry._ID,
                TaskEntry.COLUMN_TASK_NAME,
                TaskEntry.COLUMN_TASK_DESCRIPTION,
                TaskEntry.COLUMN_PRIORITY,
                TaskEntry.COLUMN_DATE,
                TaskEntry.COLUMN_TIME,
                TaskEntry.COLUMN_COMPLETED};

        return new CursorLoader(this,
                mCurrentTaskUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {

            int nameColumnIndex = cursor.getColumnIndex(TaskEntry.COLUMN_TASK_NAME);
            int descriptionColumnIndex = cursor.getColumnIndex(TaskEntry.COLUMN_TASK_DESCRIPTION);
            int priorityColumnIndex = cursor.getColumnIndex(TaskEntry.COLUMN_PRIORITY);
            int dateColumnIndex = cursor.getColumnIndex(TaskEntry.COLUMN_DATE);
            int timeColumnIndex = cursor.getColumnIndex(TaskEntry.COLUMN_TIME);
            int completedColumnIndex = cursor.getColumnIndex(TaskEntry.COLUMN_COMPLETED);

            String name = cursor.getString(nameColumnIndex);
            String description = cursor.getString(descriptionColumnIndex);
            String priority = cursor.getString(priorityColumnIndex);
            String date = cursor.getString(dateColumnIndex);
            String time = cursor.getString(timeColumnIndex);
            int completedStatus = cursor.getInt(completedColumnIndex);

            mNameEditText.setText(name);
            mDescriptionEditText.setText(description);
            if(description.equals("Nothing provided!")) {}
            else mDescriptionEditText.setTextColor(this.getResources().getColor(R.color.black));
            if(priority.equals("High")) mpriorityGroup.check(R.id.high);
            else if(priority.equals("Medium"))  mpriorityGroup.check(R.id.medium);
            else if(priority.equals("Low"))  mpriorityGroup.check(R.id.low);
            else  mpriorityGroup.check(R.id.None);

            String dateString = getString(R.string.hint_task_due_date);
            if(!date.equals(getString(R.string.hint_task_due_date))) {
                dateString = date.substring(8) + "/" + date.substring(5,7) + "/" + date.substring(0,4);
            }
            mDateText.setText(dateString);
            if(date.equals(R.string.hint_task_due_date)){}
            else mDateText.setTextColor(this.getResources().getColor(R.color.black));

            mTimeText.setText(time);
            if(time.equals(R.string.hint_task_due_time)){}
            else mTimeText.setTextColor(this.getResources().getColor(R.color.black));

            if(completedStatus==0) mCompleted.setChecked(false);
            else mCompleted.setChecked(true);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mDescriptionEditText.setText("");
        mpriorityGroup.check(R.id.None);
        mDateText.setText(R.string.hint_task_due_date);
        mTimeText.setText(R.string.hint_task_due_time);
        mCompleted.setChecked(false);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteTask();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the task in the database.
     */
    private void deleteTask() {
        if (mCurrentTaskUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentTaskUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_task_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_task_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    private void showDatePicker(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,this,mCalendar.get(Calendar.YEAR),mCalendar.get(Calendar.MONTH),mCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        mYear = year;
        mMonth = month;
        mDay = day;
        month += 1;
        String myFormat;
        if(day<=9) myFormat = "0" + day + "/";
        else myFormat = day + "/";
        if(month<=9) myFormat += "0" + month + "/";
        else myFormat += month + "/";
        myFormat += year;
        mDateText.setText(myFormat);
        mDateText.setTextColor(ContextCompat.getColor(this,R.color.black));
    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, this,mCalendar.get(Calendar.HOUR_OF_DAY),mCalendar.get(Calendar.MINUTE),false);
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
            mHour = hour;
            mMinute = minute;
            String myFormat;
            if(hour<=9) myFormat = "0" + hour + ":";
            else myFormat = hour+":";
            if(minute<=9) myFormat = myFormat+"0";
            myFormat = myFormat+minute;
            mTimeText.setText(myFormat);
            mTimeText.setTextColor(ContextCompat.getColor(this,R.color.black));
    }

    private void setAlarm(Calendar cal) {
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,RQS_1,intent,0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),pendingIntent);
        Toast.makeText(this,"Alarm Set",Toast.LENGTH_SHORT).show();
    }

    private void cancelAlarm() {
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,RQS_1,intent,0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        mDateText.setText(getString(R.string.hint_task_due_date));
        mTimeText.setText(getString(R.string.hint_task_due_time));
    }
}
