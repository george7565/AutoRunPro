package com.george.autorunpro;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
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


        //Text watcher for the edit text
        et = (EditText) findViewById(R.id.ettime);
        btn =(Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(EventAdder.this, "button event",Toast.LENGTH_SHORT).show();
                Toast.makeText(EventAdder.this, et.getText().toString(),Toast.LENGTH_SHORT).show();
            }
        });

        //app list starts
        packageManager = getPackageManager();
        List<PackageInfo> packageList = packageManager
                .getInstalledPackages(PackageManager.GET_PERMISSIONS);
        List<PackageInfo> packageList1 = new ArrayList<PackageInfo>();  //new list to add packages
        /*To filter out System apps*/
        for (PackageInfo pi : packageList) {
            boolean b = isSystemPackage(pi);
            if (!b) {
                packageList1.add(pi);
            }
        }
       /*  int listSize = packageList1.size();

       for (int i = 0; i<listSize; i++){
            Log.i("Member name: ", packageList1.get(i).toString());
        }*/

        //spinner
        Spinner apkList = (Spinner) findViewById(R.id.dynamic_spinner);
        apkList.setAdapter(new ApkAdapter(EventAdder.this, packageList1, packageManager));
        apkList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                PackageInfo packageInfo =  (PackageInfo) parent.getItemAtPosition(position);
                Log.i("Member name: ", packageInfo.toString());
               // Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(packageInfo.packageName);
               // if (launchIntent != null) {
              //      context.startActivity(launchIntent);
              //  } else {
              //      Toast.makeText(context, "Package not found", Toast.LENGTH_SHORT).show();
              //  }
              //  startActivity( LaunchIntent );
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
    }

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
