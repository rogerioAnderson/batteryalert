package com.coded2.wearbatteryalert;


import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.github.anastr.speedviewlib.base.Gauge;
import com.google.android.gms.common.api.BooleanResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.Set;




/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {



    private String BATTERY_STATUS_CAPABILITY = "battery_status";
    private String PATH_BATTERY_REQUEST = "/wear_battery_request";

    private ToggleButton toggleButton;

    private Gauge phoneBatteryGauge;
    private ImageView phoneChargeState;
    //private TextView phoneTime;

    private Gauge watchBatteryGauge;
    private ImageView watchChargeState;
    //private TextView watchTime;



    private GoogleApiClient wearApiClient;
    private Node capbilityNode;

    public HomeFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initGoogleAPI();
    }

    private BroadcastReceiver phoneBatteryChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging =    status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL;


            int rawLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE,-1);
            int level = 0;

            if(rawLevel>=0 && scale>0){
                level = (rawLevel*100)/scale;
            }

            phoneBatteryGauge.speedTo(level,3000);


            if(isCharging){
                phoneChargeState.setImageDrawable(getResources().getDrawable(R.drawable.charging_flag));

            }else{
                phoneChargeState.setImageDrawable(getResources().getDrawable(R.drawable.discharging_flag));
            }

            Log.d(Constants.PACKAGE_NAME,"Charging: "+isCharging);
            ((OnBatteryChargeListener)getActivity()).OnBatteryCharge(isCharging);

        }
    };


    private BroadcastReceiver watchBatteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging =    status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL;


            int rawLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE,-1);
            int level = 0;

            if(rawLevel>=0 && scale>0){
                level = (rawLevel*100)/scale;
            }

           watchBatteryGauge.speedTo(level,3000);


            if(isCharging){
                watchChargeState.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.charging_flag,null));
            }else{
                watchChargeState.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.discharging_flag,null));
            }
        }
    };



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)  {

        final View view = inflater.inflate(R.layout.fragment_home, container, false);

        phoneBatteryGauge = (Gauge) view.findViewById(R.id.btryPhoneGauge);
        //phoneTime = (TextView) view.findViewById(R.id.btryPhoneTime);
        phoneChargeState = (ImageView) view.findViewById(R.id.phoneChargeState);


        watchBatteryGauge = (Gauge) view.findViewById(R.id.btryWatchGauge);
        //watchTime = (TextView) view.findViewById(R.id.btryWatchTime);
        watchChargeState = (ImageView) view.findViewById(R.id.watchChargeState);

        ConstraintLayout watchLayout = (ConstraintLayout) view.findViewById(R.id.watchContent);

        watchLayout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                requestBattertStatysFromWear();
            }
        });

        wearApiClient.connect();
        return view;
    }




    @Override
    public void onResume() {
        super.onResume();
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        getActivity().registerReceiver(this.phoneBatteryChangeReceiver, ifilter);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                requestBattertStatysFromWear();
            }
        },3000);

        Wearable.MessageApi.addListener(wearApiClient,messageListener);


   }


   private MessageApi.MessageListener messageListener = new MessageApi.MessageListener() {
       @Override
       public void onMessageReceived(MessageEvent event) {
            String data = new String(event.getData());
           Log.d(Constants.PACKAGE_NAME, "received data from wear: "+data);

           final String[] dataStr = data.split("@");
           int level = Integer.valueOf(dataStr[0]);
           boolean isCharging = Boolean.valueOf(dataStr[1]);


           watchBatteryGauge.speedTo(level,3000);

           if(isCharging){
               watchChargeState.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.charging_flag,null));
           }else{
               watchChargeState.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.discharging_flag,null));
           }


           HomeFragment.this.getView().findViewById(R.id.watchContent).setAlpha(1);

       }
   };

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(phoneBatteryChangeReceiver);
        Wearable.MessageApi.removeListener(wearApiClient,messageListener);
    }


    public interface OnBatteryChargeListener{
        public void OnBatteryCharge(boolean isCharging);
    }


    private void initGoogleAPI() {
        wearApiClient = new GoogleApiClient.Builder(getActivity()).
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

                        PendingResult<CapabilityApi.GetCapabilityResult> pendingResult =
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


    public void requestBattertStatysFromWear(){
        Log.d(Constants.PACKAGE_NAME,"requestBattertStatysFromWear()");
        if(capbilityNode!=null){
            Log.d(Constants.PACKAGE_NAME,"node name: "+capbilityNode.getDisplayName());
            final PendingResult<MessageApi.SendMessageResult> result = Wearable.MessageApi.sendMessage(wearApiClient, capbilityNode.getId(), PATH_BATTERY_REQUEST, null);
            result.setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                @Override
                public void onResult(@NonNull MessageApi.SendMessageResult sendMessageResult) {
                        if(sendMessageResult.getStatus().isSuccess()){
                                Toast.makeText(getActivity(),"Request Battery from your wearable", Toast.LENGTH_SHORT).show();
                                Log.d(Constants.PACKAGE_NAME,sendMessageResult.getStatus().toString());

                            PreferenceManager.getDefaultSharedPreferences(getActivity())
                                    .edit()
                                    .putBoolean(Constants.PREFERENCE.EXISTS_WEAREBLE,true)
                                    .apply();


                        }else{
                                if(getActivity()!=null){
                                    Toast.makeText(getActivity(),"Failed on wear battery status request", Toast.LENGTH_SHORT).show();
                                    Log.e(getActivity().getPackageName(),sendMessageResult.getStatus().toString());
                                }

                        }
                }
            });
        }else{
            Toast.makeText(getActivity(), "device weareble not found", Toast.LENGTH_SHORT).show();
        }
    }
}
