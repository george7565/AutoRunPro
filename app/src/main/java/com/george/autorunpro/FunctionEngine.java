package com.george.autorunpro;

import android.content.Context;
import android.net.wifi.WifiManager;

/**
 * Created by akshay on 3/25/16.
 */
public class FunctionEngine {

    private int ON =0;
    private int OFF = 1;
    private Context context;


    public FunctionEngine(Context context){
       this.context = context;

    }

    public void wifi(int mode){

        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        if(mode == ON){
            System.out.println("wifi on");
            wifiManager.setWifiEnabled(true);
        }
        else{
            System.out.println("wifi off");
            wifiManager.setWifiEnabled(false);
        }
    }//wifi close




}//engine close
