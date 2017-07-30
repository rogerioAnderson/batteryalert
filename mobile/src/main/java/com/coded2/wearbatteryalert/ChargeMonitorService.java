package com.coded2.wearbatteryalert;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

public class ChargeMonitorService extends Service {


    public ChargeMonitorService() {
    }

    private Binder binder;
    private BatteryChangeReceiver btryReceiver;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        startForeground(Constants.CHARGE_MONITOR_SERVICE_NOTIFICATION,buildNotification());
        this.binder = new Binder();
        btryReceiver = new BatteryChangeReceiver();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(btryReceiver,ifilter);
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    private Notification buildNotification(){




        Intent intent = new Intent(this,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);

        Notification notification =  new NotificationCompat.Builder(getBaseContext())
                .setSmallIcon(R.drawable.ic_action_charge)
                .setContentIntent(pendingIntent)
                .setContentTitle(getBaseContext().getString(R.string.chaging_battery_status))
                .build();
        return notification;
    }



    public class Binder extends android.os.Binder{
        public ChargeMonitorService getService(){
            return ChargeMonitorService.this;
        }
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(btryReceiver);
    }
}