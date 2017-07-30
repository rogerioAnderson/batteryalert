package com.coded2.wearbatteryalert;


import android.app.Fragment;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.github.anastr.speedviewlib.base.Gauge;
import com.google.android.gms.common.api.GoogleApiClient;



/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    private ToggleButton toggleButton;

    private Gauge phoneBatteryGauge;
    private ImageView phoneChargeState;
    //private TextView phoneTime;

    private Gauge watchBatteryGauge;
    private ImageView watchChargeState;
    //private TextView watchTime;


    private GoogleApiClient wearAPI;

    public HomeFragment() {

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
                Toast.makeText(v.getContext(),"Weareble feature will be avaliable soon",Toast.LENGTH_SHORT).show();
            }
        });





        /*
        wearAPI =  new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        Log.d(Constants.PACKAGE_NAME,"Conectado");
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Log.d(Constants.PACKAGE_NAME,"Conexao Suspensa");
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.d(Constants.PACKAGE_NAME,"Falha na conexao");
                    }
                })
                .addApi(Wearable.API)
                .build();
                wearAPI.connect();


        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        wearAPI.getContext().registerReceiver(watchBatteryReceiver,ifilter);
        */



        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        getActivity().registerReceiver(this.phoneBatteryChangeReceiver, ifilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(phoneBatteryChangeReceiver);
    }




    public interface OnBatteryChargeListener{
        public void OnBatteryCharge(boolean isCharging);
    }
}
