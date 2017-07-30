package com.coded2.wearbatteryalert;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.preference.PreferenceManager;
import android.util.Log;

public class PowerConnectionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {


        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);

        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS,-1);

        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        boolean isFull = status == BatteryManager.BATTERY_STATUS_FULL;

        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = (chargePlug == BatteryManager.BATTERY_PLUGGED_USB);
        boolean acCharge = (chargePlug == BatteryManager.BATTERY_PLUGGED_AC);

        final boolean isServiceEnabled = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getResources().getString(R.string.key_service_enable), true);

            Intent itService = new Intent(context,ChargeMonitorService.class);
            if((acCharge || usbCharge) && !isFull && isServiceEnabled){
                Log.d(Constants.PACKAGE_NAME,"starting charging service");
                context.startService(itService);
            }else {
                Log.d(Constants.PACKAGE_NAME,"stoppoing charging service");
                context.stopService(itService);
            }
    }
}
