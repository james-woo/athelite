package com.athelite.Util;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.athelite.Activity.ViewDay;
import com.athelite.R;

import br.com.goncalves.pugnotification.notification.PugNotification;


public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent viewDay = new Intent(context, ViewDay.class);
        viewDay.putExtra("VIEW_DAY_DATETIME", intent.getLongExtra("VIEW_DAY_DATETIME", 0));
        viewDay.putExtra("VIEW_DAY_PARENT", "");

        PugNotification.with(context)
                .load()
                .title("Today's Workout")
                .message("Tap to see your workout for today")
                .smallIcon(R.drawable.ic_workout_icon_white_24dp)
                .largeIcon(R.drawable.ic_workout_icon_white_24dp)
                .flags(Notification.DEFAULT_ALL)
                .onlyAlertOnce(true)
                .autoCancel(true)
                .click(PendingIntent.getActivity(context, 0, viewDay, PendingIntent.FLAG_UPDATE_CURRENT))
                .simple()
                .build();
    }
}