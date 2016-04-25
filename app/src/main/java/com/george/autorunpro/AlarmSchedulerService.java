package com.george.autorunpro;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

public class AlarmSchedulerService extends Service {

    private Cursor c = null;
    private int sunb,monb,tueb,wedb,thub,frib,satb,req_id;
    String time = null;

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int  onStartCommand(Intent intent,int flags, int startId) {
        // TODO Auto-generated method stub
        Toast.makeText(this, "setting alarms after reboot"     , Toast.LENGTH_LONG).show();
        getAndSetAlarms("select * from 'AppAlarms' where status = 1");
        getAndSetAlarms("select * from 'FuncAlarms' where status = 1");

        this.stopSelf();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

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
    private void getAndSetAlarms(String query){

        SqlOperator sqlOperator = new SqlOperator(this);
        c = sqlOperator.selectRecord(query);
        if (c.moveToFirst()) {
            do {
                sunb = c.getInt(c.getColumnIndex("sunday"));
                monb = c.getInt(c.getColumnIndex("monday"));
                tueb = c.getInt(c.getColumnIndex("tuesday"));
                wedb = c.getInt(c.getColumnIndex("wednesday"));
                thub = c.getInt(c.getColumnIndex("thursday"));
                frib = c.getInt(c.getColumnIndex("friday"));
                satb = c.getInt(c.getColumnIndex("saturday"));
                req_id = c.getInt(c.getColumnIndex("id"));
                time  = c.getString(c.getColumnIndex("time"));
                Add_Alarm(time);

            }while (c.moveToNext());

        }
        c.close();
    }
}
