package com.george.autorunpro;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by akshay on 3/25/16.
 */
public class SqlOperator2{

    private DatabaseParent dbHelper;

    private SQLiteDatabase database;

    public final static String APP_TABLE="FuncAlarms"; // name of table
    public final static String FUNC_NAME="funcname"; // id value for employee
    public final static String FUNC_TIME="time";  // name of employee
    public final static String MONDAY = "monday";
    public final static String TUESDAY = "tuesday";
    public final static String WEDNESDAY = "wednesday";
    public final static String THURSDAY = "thursday";
    public final static String FRIDAY = "friday";
    public final static String SATURDAY = "saturday";
    public final static String SUNDAY = "sunday";
    public final static String MODE = "mode";
    public final static String STATUS = "status";
    public final static String ID = "id";
    public final static String ALONETYPE = "alonetype";

    /**
     *
     * @param
     */
    public SqlOperator2(Context context){
        dbHelper = DatabaseParent.getInstance(context);
        database = dbHelper.getWritableDatabase();
    }


    public long createRecords(String funcname, String time,int mon,int tue,int wed,int thur,int fri,int sat,int sun,int mode,int status,int alonetype){
        ContentValues values = new ContentValues();
        values.put(FUNC_NAME, funcname);
        values.put(FUNC_TIME, time);
        values.put(MONDAY,mon);
        values.put(TUESDAY,tue);
        values.put(WEDNESDAY,wed);
        values.put(THURSDAY,thur);
        values.put(FRIDAY,fri);
        values.put(SATURDAY,sat);
        values.put(SUNDAY,sun);
        values.put(MODE,mode);
        values.put(STATUS,status);
        values.put(ALONETYPE,alonetype);
        return database.insert(APP_TABLE, null, values);
    }

    public Cursor selectRecords() {
        //String[] cols = new String[] {ID,APP_NAME, APP_TIME};
        Cursor mCursor = database.query(true, APP_TABLE,null,null
                , null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor; // iterate to get each value.
    }
    public int getNextid(){

        int id;
        Cursor c = database.rawQuery("SELECT * FROM "+APP_TABLE+" ORDER BY id DESC LIMIT 1", null);
        if(c.moveToFirst()){

            id = c.getInt(c.getColumnIndex("id"));
            System.out.println("inside getlastid id="+id);
        }
        else
            id = 500;
        c.close();
        return id;
    }


    public Cursor selectRecord(String query){

        Cursor c = database.rawQuery(query,null);
        return c;
    }

    public boolean updateRecord(int id,ContentValues contentValues){


        try {
            database.update(APP_TABLE,contentValues, "Id="+id, null);
        }
        catch(Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void delete(String[] Id)
    {
        try {
            database.delete(APP_TABLE, "id IN (?, ?)", Id);
        }
        catch(Exception e) {
            e.printStackTrace(); }
    }

}
