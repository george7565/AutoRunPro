package com.george.autorunpro.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.widget.TimePicker;
import java.util.Calendar;

    public class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        String time;
        public interface OnDataPass {
             void onDataPass(String data);
        }
        OnDataPass dataPasser;

        @Override
        public void onAttach(Activity a) {
            super.onAttach(a);
            dataPasser = (OnDataPass) a;
        }
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
           time =  (Integer.toString(hourOfDay) + ":"+ Integer.toString(minute));
            passData(time);

        }
        public void passData(String data) {
            dataPasser.onDataPass(data);
        }
    }

