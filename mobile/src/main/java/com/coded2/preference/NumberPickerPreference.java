package com.coded2.preference;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.coded2.wearbatteryalert.R;

/**
 * Created by rogerio on 7/20/17.
 */

public class NumberPickerPreference extends DialogPreference{


    private final int DEFAULT_VALUE = 20;
    private int value = DEFAULT_VALUE;
    private View view;
    private NumberPicker numberPicker;

    public NumberPickerPreference(Context context, AttributeSet attrs) {
        super(context,attrs);
    }


    @Override
    protected View onCreateView(ViewGroup parent) {
        updateValue();
        return super.onCreateView(parent);
    }

    @Override
    protected void onBindDialogView(View view) {

        this.view = view;
        super.onBindDialogView(view);

        numberPicker = (NumberPicker) view.findViewById(R.id.number_picker_pref);

        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(100);
        numberPicker.setValue(value);


    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInteger(index,DEFAULT_VALUE);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            Integer persistedInt = getPersistedInt(DEFAULT_VALUE);
            value = persistedInt;
        } else {
            value = (Integer)defaultValue;
            persistInt(value);
        }
    }


    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            value = numberPicker.getValue();
            persistInt(value);
        }

        updateValue();
    }


    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        this.view = view;
        updateValue();
    }

    private void updateValue() {
        int value =  getSharedPreferences().getInt(getKey(),DEFAULT_VALUE);
        //if(view !=null){
            //TextView TxtValue = (TextView) view.findViewById(R.id.pref_view_value);
            //TxtValue.setText(String.valueOf(value));
            String title = getContext().getString(R.string.title_low_battery_percent);
            setTitle(title.concat(" ").concat(String.valueOf(value)).concat("%"));
        //}
    }
}
