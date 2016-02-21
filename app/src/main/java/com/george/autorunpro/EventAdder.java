package com.george.autorunpro;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.george.autorunpro.activity.TimePickerFragment;
import com.george.autorunpro.adapter.ApkAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventAdder extends AppCompatActivity implements TimePickerFragment.OnDataPass {


    PackageManager packageManager;
    ListView apkList;
    EditText et;
    Button btn;
    String time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_adder);

        //app list starts
        packageManager = getPackageManager();
        List<PackageInfo> packageList = packageManager
                .getInstalledPackages(PackageManager.GET_PERMISSIONS);
        //Text watcher for the edit text
        et = (EditText) findViewById(R.id.three);
        btn =(Button) findViewById(R.id.btn);
        //btn.setAlpha(.5f);
        // btn.setClickable(false);
        // btn.setEnabled(false);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(EventAdder.this, "button event",Toast.LENGTH_SHORT).show();
                Toast.makeText(EventAdder.this, et.getText().toString(),Toast.LENGTH_SHORT).show();
            }
        });
        et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(v);
            }

        });
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void afterTextChanged(Editable s) {
                // btn.setAlpha(1f);
                // btn.setClickable(true);
            }
        });


        List<PackageInfo> packageList1 = new ArrayList<PackageInfo>();  //new list to add packages

        /*To filter out System apps*/
        for (PackageInfo pi : packageList) {
            boolean b = isSystemPackage(pi);
            if (!b) {
                packageList1.add(pi);
            }
        }

        apkList = (ListView) findViewById(R.id.applist);
        apkList.setAdapter(new ApkAdapter(EventAdder.this, packageList1, packageManager));
        apkList.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long row) {
                PackageInfo packageInfo = (PackageInfo) parent
                        .getItemAtPosition(position);
                AppData appData = (AppData) getApplicationContext();
                appData.setPackageInfo(packageInfo);

                Intent appInfo = new Intent(getApplicationContext(), ApkInfo.class);
                startActivity(appInfo);
            }



        });
    }
    /**
     * Return whether the given PackgeInfo represents a system package or not.
     * User-installed packages (Market or otherwise) should not be denoted as
     * system packages.
     *
     * @param pkgInfo
     * @return boolean
     */

    private boolean isSystemPackage(PackageInfo pkgInfo) {
        return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true
                : false;


    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        // newFragment.setTargetFragment(this.getFragmentManager(), 0);
        newFragment.show(getFragmentManager(), "timePicker");
    }

    @Override
    public void onDataPass(String data) {
        Log.d("LOG", "hello " + data);
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
            final Date dateObj = sdf.parse(data);
            System.out.println(dateObj);
            System.out.println(new SimpleDateFormat("hh:mm aa").format(dateObj));
            String time = new SimpleDateFormat("hh:mm aa").format(dateObj).toString();
            et.setText(time);
        }
        catch(final ParseException e)
        {e.printStackTrace();}
    }
}
