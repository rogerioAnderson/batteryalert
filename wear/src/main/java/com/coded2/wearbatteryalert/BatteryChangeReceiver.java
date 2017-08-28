package com.coded2.wearbatteryalert;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.Set;

public class BatteryChangeReceiver extends BroadcastReceiver {

    private static final String BATTERY_STATUS_CAPABILITY = "battery_status";
    private static final String PATH_BATTERY_REQUEST = "wear_battery_request";
    private int level;
    private boolean isCharging;
    private GoogleApiClient wearApiClient;
    private Node capbilityNode;


    @Override
    public void onReceive(final Context context, Intent intentReceived) {

        Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        int status = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        isCharging =    status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;


        int rawLevel = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE,-1);

        if(rawLevel>=0 && scale>0){
            level = (rawLevel*100)/scale;
        }

        Log.d(Constants.PACKAGE_NAME,"battery level/ischarging: ".concat(Integer.toString(level)).concat("/").concat(Boolean.toString(isCharging)));

        initGoogleAPI(context);
        wearApiClient.connect();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sendWearBatteryData(context);
            }
        }, 2000);


    }


    private void initGoogleAPI(Context context) {
        wearApiClient = new GoogleApiClient.Builder(context).
                addApi(Wearable.API).
                addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        Log.d(Constants.PACKAGE_NAME,"onConnected()");
                        Wearable.CapabilityApi.addCapabilityListener(
                                wearApiClient,
                                new CapabilityApi.CapabilityListener() {
                                    @Override
                                    public void onCapabilityChanged(CapabilityInfo capabilityInfo) {
                                        Log.d(Constants.PACKAGE_NAME, "onCapabilityChanged(): " + capabilityInfo);

                                        capbilityNode = pickBestNodeId(capabilityInfo.getNodes());
                                    }
                                },
                                BATTERY_STATUS_CAPABILITY);

                        com.google.android.gms.common.api.PendingResult<CapabilityApi.GetCapabilityResult> pendingResult =
                                Wearable.CapabilityApi.getCapability(
                                        wearApiClient,
                                        BATTERY_STATUS_CAPABILITY,
                                        CapabilityApi.FILTER_ALL);

                        pendingResult.setResultCallback(new ResultCallback<CapabilityApi.GetCapabilityResult>(){

                            @Override
                            public void onResult(@NonNull CapabilityApi.GetCapabilityResult getCapabilityResult) {
                                Log.d(Constants.PACKAGE_NAME, "onResult(): " + getCapabilityResult);

                                if (getCapabilityResult.getStatus().isSuccess()) {
                                    CapabilityInfo capabilityInfo = getCapabilityResult.getCapability();
                                    capbilityNode = pickBestNodeId(capabilityInfo.getNodes());
                                } else {
                                    Log.d(Constants.PACKAGE_NAME, "Failed CapabilityApi: " + getCapabilityResult.getStatus());
                                }
                            }
                        });
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Log.d(Constants.PACKAGE_NAME,"onConnectionSuspended()");
                    }
                }).
                build();
    }


    private Node pickBestNodeId(Set<Node> nodes) {
        Log.d(Constants.PACKAGE_NAME,"pickBestNodeId()");

        Node bestNode = null;

        for(Node node: nodes){
            bestNode = node;
        }
        Log.d(Constants.PACKAGE_NAME, "Best node: "+bestNode);

        return bestNode;
    }


    public void sendWearBatteryData(final Context context){
        Log.d(Constants.PACKAGE_NAME,"sendWearBatteryData()");
        if(capbilityNode!=null){
            Log.d(context.getPackageName(),"node name: "+capbilityNode.getDisplayName());


            String data = new String().concat(Integer.toString(level).concat("@").concat(Boolean.toString(isCharging)));

            final com.google.android.gms.common.api.PendingResult<MessageApi.SendMessageResult> result = Wearable.MessageApi.sendMessage(wearApiClient, capbilityNode.getId(), PATH_BATTERY_REQUEST, data.getBytes());
            result.setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                @Override
                public void onResult(@NonNull MessageApi.SendMessageResult sendMessageResult) {
                    if(sendMessageResult.getStatus().isSuccess()){
                        Toast.makeText(context,"Request Battery from your wearable", Toast.LENGTH_SHORT).show();
                        Log.d(Constants.PACKAGE_NAME,sendMessageResult.getStatus().toString());
                    }else{
                        Toast.makeText(context,"Failed on wear battery status request", Toast.LENGTH_SHORT).show();
                        Log.e(context.getPackageName(),sendMessageResult.getStatus().toString());
                    }
                }
            });
        }else{
            Toast.makeText(context, "device weareble not found", Toast.LENGTH_SHORT).show();
        }
    }


}



