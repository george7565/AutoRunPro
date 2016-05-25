package com.george.autorunpro;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;



/**
 * Created by Akshay on 21-Apr-16.
 */
public class SettingsActivity extends PreferenceActivity{


        @Override
        protected void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            //setContentView(R.layout.activity_settings);

            getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();

        }

        public static class MyPreferenceFragment extends PreferenceFragment
        {

            @Override
            public void onCreate(final Bundle savedInstanceState)
            {

                super.onCreate(savedInstanceState);
                addPreferencesFromResource(R.xml.preferences);

            }


        }



    }


