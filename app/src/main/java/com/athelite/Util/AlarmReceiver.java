package com.athelite.Util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.athelite.Activity.ViewDay;
import com.athelite.R;


public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        android.support.v4.app.NotificationCompat.Builder mBuilder =
                new android.support.v4.app.NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_workout_icon_white_24dp)
                        .setContentTitle("Today's Workout")
                        .setContentText("Tap to see your workout for today");

        Intent resultIntent = new Intent(context, ViewDay.class);
        //resultIntent.putExtra("VIEW_DAY_DATETIME", _dateTime);
        resultIntent.putExtra("VIEW_DAY_PARENT", "Home");
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        mNotifyMgr.notify(0, mBuilder.build());
    }
}