package com.coded2.wearbatteryalert;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

public class BatterySatusListener extends WearableListenerService {
    public BatterySatusListener() {
    }

    private int level;
    private boolean isCharging;
    private String sourceNodeID;
    private final String PATH_RESPONSE_STATUS = "/wear_battery_response";



    private BroadcastReceiver watchBatteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            isCharging =    status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL;


            int rawLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE,-1);

            if(rawLevel>=0 && scale>0){
                level = (rawLevel*100)/scale;
            }

            reply();

        }
    };



    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        Log.d(getPackageName(),"onMessageReceived");

        sourceNodeID = messageEvent.getSourceNodeId();
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(watchBatteryReceiver,ifilter);

    }

    private void reply(){

        GoogleApiClient clienAPI = new GoogleApiClient.Builder(getApplicationContext()).addApi(Wearable.API).build();

        clienAPI.connect();

        String response = level + "@"+isCharging;

        Wearable.MessageApi.sendMessage(clienAPI,sourceNodeID,PATH_RESPONSE_STATUS,response.getBytes()).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
            @Override
            public void onResult(@NonNull MessageApi.SendMessageResult sendMessageResult) {
                Log.d(getPackageName(), sendMessageResult.getStatus().isSuccess()+" / "+sendMessageResult.getStatus());
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
            unregisterReceiver(watchBatteryReceiver);
    }
}
