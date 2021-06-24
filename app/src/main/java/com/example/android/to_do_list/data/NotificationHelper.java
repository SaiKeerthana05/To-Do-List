package com.example.android.to_do_list.data;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.example.android.to_do_list.R;
import com.example.android.to_do_list.TaskActivity;


public class NotificationHelper extends ContextWrapper {
    public static final String ChannelId = "ChannelID";
    public static final String ChannelName = "Channel 1";
    private NotificationManager mManager;
    private Context mContext;

    public NotificationHelper(Context base) {
        super(base);
        mContext = base;
        if(Build.VERSION.SDK_INT>=26){
            createChannel();
        }
    }

    public void createChannel() {
        NotificationChannel channel = new NotificationChannel(ChannelId,ChannelName, NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setLightColor(R.color.colorPrimary);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager() {
       if(mManager == null) {
           mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
       }
       return mManager;
    }

    public NotificationCompat.Builder getChannelNotification(Intent i) {
        Intent intent = new Intent(mContext, TaskActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext,0,intent,0);
        return new NotificationCompat.Builder(getApplicationContext(),ChannelId)
                .setContentTitle("Is it done?")
                .setContentText("Deadline reached for your task")
                .setSmallIcon(R.drawable.ic_alarm)
                .setContentIntent(pendingIntent);
    }
}
