package com.george.autorunpro;

/**
 * Created by Akshay on 06-Mar-16.
 */
public class Pojo_fetch_data {

        public int id;
        public  String appname;
        public String start_time,stop_time;


        public Pojo_fetch_data(int id, String name, String start, String stop) {
            this.id = id;
            this.appname = name;
            this.start_time = start;
            this.stop_time = stop;
        }
    }

