package com.george.autorunpro;

/**
 * Created by Akshay on 06-Mar-16.
 */
public class Pojo_fetch_data {

        public int id;
        public  String appname,start_time,stop_time,weekday_status;
        public int status;


        public Pojo_fetch_data(int id, String name, String start, String stop,int status,String weekday_status) {
            this.id = id;
            this.appname = name;
            this.start_time = start;
            this.stop_time = stop;
            this.status = status;
            this.weekday_status = weekday_status;
        }
    }

