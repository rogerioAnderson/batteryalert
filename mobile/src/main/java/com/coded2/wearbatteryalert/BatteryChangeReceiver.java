package com.coded2.wearbatteryalert;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

public class BatteryChangeReceiver extends BroadcastReceiver {

    boolean notified;

    @Override
    public void onReceive(Context context, Intent intent) {

        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

        int rawLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE,-1);
        int level = 0;

        if(rawLevel>=0 && scale>0){
            level = (rawLevel*100)/scale;
        }

        if(status == BatteryManager.BATTERY_STATUS_FULL && !notified){
            fireNotification(context);
        }
    }

    private void fireNotification(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        Notification notification = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_stat_battery_charging_full)
                .setContentTitle(context.getString(R.string.alert_title))
                .setContentText(context.getString(R.string.full_battery_text))
                .setVibrate(new long[]{0,200,200,200})
                .setContentIntent(pendingIntent)
                .build();
        NotificationManagerCompat nManagerCompat = NotificationManagerCompat.from(context);
        nManagerCompat.notify(010,notification);
        notified = true;

    }
}