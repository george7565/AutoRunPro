package com.george.autorunpro;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.george.autorunpro.fragment.TimePickerFragment;
import com.george.autorunpro.adapter.ApkAdapter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EventAdder extends AppCompatActivity implements TimePickerFragment.OnDataPass {


    PackageManager packageManager;
    EditText start_time,stop_time;
    TextView stoppadding;
    Button btn;
    String time_temp = "00:00",start_timme;
    PackageInfo packageInfo;
    int req_id;
    CheckBox sun,mon,tue,wed,thu,fri,sat;
    int sunb,monb,tueb,wedb,thub,frib,satb;
    int resultcode = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_adder);
        start_time = (EditText) findViewById(R.id.starttime);
        stop_time = (EditText) findViewById(R.id.stoptime);
        stoppadding = (TextView) findViewById(R.id.stoppadding);
        btn =(Button) findViewById(R.id.btn);
        mon = (CheckBox) findViewById(R.id.mon);
        tue = (CheckBox) findViewById(R.id.tue);
        wed = (CheckBox) findViewById(R.id.wed);
        thu = (CheckBox) findViewById(R.id.thu);
        fri= (CheckBox) findViewById(R.id.fri);
        sat = (CheckBox) findViewById(R.id.sat);
        sun = (CheckBox) findViewById(R.id.sun);
        sunb = 0; monb =0 ; tueb = 0 ; wedb=0 ; thub=0; frib=0; satb=0;


        // having onclick listeners for showing timepicker dialogue fragment
        stop_time.setVisibility(View.GONE);
        stoppadding.setVisibility(View.GONE);
       start_time.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                DialogFragment newFragment = new TimePickerFragment();
                Bundle bundle = new Bundle();
                bundle.putString("key","start" );
                newFragment.setArguments(bundle);
                newFragment.show(getFragmentManager(), "timePicker");

            }
       });

      //events happenning on button click
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //checking weekday checkboxes checked or not
                if(sun.isChecked()) sunb = 1; else sunb = 0;
                if(mon.isChecked()) monb = 1; else monb = 0;
                if(tue.isChecked()) tueb = 1; else tueb = 0;
                if(wed.isChecked()) wedb = 1; else wedb = 0;
                if(thu.isChecked()) thub = 1; else thub = 0;
                if(fri.isChecked()) frib = 1; else frib = 0;
                if(sat.isChecked()) satb = 1; else satb = 0;

                if ( start_time.getText().toString().length() == 0 ) {
                    Snackbar.make(v, "Enter the start time!",
                            Snackbar.LENGTH_LONG).show();
                }
                else {

                    DbAdd(packageInfo.packageName, start_timme, monb, tueb, wedb, thub, frib, satb, sunb, 1);
                        System.out.println("in event adder id for start in runkill=" + req_id);
                    Add_Alarm(start_timme);

                    Snackbar.make(v, "Alarm Added!", Snackbar.LENGTH_LONG).show();
                    return_to_fragment();
                }
            }
        });

        //app list starts
        packageManager = getPackageManager();
        List<PackageInfo> packageList = packageManager
                .getInstalledPackages(PackageManager.GET_PERMISSIONS);
        List<PackageInfo> packageList1 = new ArrayList<PackageInfo>();  //new list to add packages
        /*To filter out System apps*/
        for (PackageInfo pi : packageList) {
            if(packageManager.getLaunchIntentForPackage(pi.packageName) != null) {
                // apps with launcher intent
                packageList1.add(pi);
            }
        }

        //spinner
        Spinner apkList = (Spinner) findViewById(R.id.dynamic_spinner);
        apkList.setAdapter(new ApkAdapter(EventAdder.this, packageList1, packageManager));
        apkList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                packageInfo =  (PackageInfo) parent.getItemAtPosition(position);
                Log.i("Member name: ", packageInfo.toString());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
    }


    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

    @Override
    public void onDataPass(String data,String type) {
           // Log.d("LOG", "hello " + data);
        try {
            time_temp = data;  //time in 24 hours
            //System.out.println("Inside ondatapass="+time_temp);
            final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
            final Date dateObj = sdf.parse(time_temp);
            String time_12hr = new SimpleDateFormat("hh:mm aa").format(dateObj); //convert to 10:10 AM

            if(type.equals("start")){
                 start_timme = time_temp;
                start_time.setText(time_12hr);
            }
        }catch (final ParseException e){e.printStackTrace();}


    }

    private Boolean Add_Alarm(String time){

        String[] splited = time.split(":");
        int hour =Integer.parseInt(splited[0]);
        int min = Integer.parseInt(splited[1]);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, 00);
        if(Calendar.getInstance().after(calendar)){
            // Move to tomorrow
            calendar.add(Calendar.DATE, 1);
        }

        if(monb == 1 || tueb == 1 || wedb == 1 || thub == 1 || frib==1 || satb==1 || sunb==1)
            AlarmSet.SetRepeatAlarm(getApplicationContext(),calendar,req_id);
        else
            AlarmSet.setOnetimeTimer(getApplicationContext(),calendar,req_id);
        return true;
    }
    private boolean DbAdd(String name,String time,int mon,int tue,int wed,int thur,int fri,int sat,int sund,int status){

        SqlOperator sqlOperator = new SqlOperator(getApplicationContext());
        sqlOperator.createRecords(name,time,mon,tue,wed,thur,fri,sat,sund,status);
        req_id = sqlOperator.getNextid();
        return true;
    }

    public void return_to_fragment(){

        System.out.println("running onfinish");
        Intent returnIntent = new Intent();
        setResult(RESULT_OK,returnIntent);
        finish();

    }



}


