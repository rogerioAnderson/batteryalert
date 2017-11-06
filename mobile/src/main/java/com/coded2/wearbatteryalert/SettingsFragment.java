package com.coded2.wearbatteryalert;

import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.RingtonePreference;
import android.provider.Settings;
import android.support.annotation.Nullable;


public class SettingsFragment extends PreferenceFragment {


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        final SharedPreferences prefs = getPreferenceManager().getSharedPreferences();

        Uri ringtoneUri = Uri.parse(prefs.getString(Constants.PREFERENCE.LOW_BAT_NOTIFICATION,
                Settings.System.DEFAULT_NOTIFICATION_URI.toString()));

        updateLowAlertRingTone(ringtoneUri,Constants.PREFERENCE.LOW_BAT_NOTIFICATION);


        RingtonePreference ringtonePreference = (RingtonePreference) findPreference(Constants.PREFERENCE.LOW_BAT_NOTIFICATION);
        ringtonePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                updateLowAlertRingTone(Uri.parse(newValue.toString()),Constants.PREFERENCE.LOW_BAT_NOTIFICATION);

                return true;
            }
        });


        Uri ringtoneUriWear = Uri.parse(prefs.getString(Constants.PREFERENCE.LOW_BAT_WEAR_NOTIFICATION,
                Settings.System.DEFAULT_NOTIFICATION_URI.toString()));

        updateLowAlertRingTone(ringtoneUriWear,Constants.PREFERENCE.LOW_BAT_WEAR_NOTIFICATION);


        RingtonePreference ringtonePreferenceWear = (RingtonePreference) findPreference(Constants.PREFERENCE.LOW_BAT_WEAR_NOTIFICATION);

        ringtonePreferenceWear.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                updateLowAlertRingTone(Uri.parse(newValue.toString()),Constants.PREFERENCE.LOW_BAT_WEAR_NOTIFICATION);

                return true;
            }
        });




    }

    private void updateLowAlertRingTone(Uri ringtoneUri,String key) {

        final Ringtone ringtone = RingtoneManager.getRingtone(getActivity(), ringtoneUri);

        RingtonePreference ringtonePreference = (RingtonePreference) findPreference(key);

        ringtonePreference.setSummary(ringtone.getTitle(getActivity()));


    }

}
