package com.george.autorunpro;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Akshay on 23-Feb-16.
 */
public class SqlOperator{

    private DatabaseParent dbHelper;

    private SQLiteDatabase database;

    public final static String APP_TABLE="AppAlarms"; // name of table
    public final static String APP_NAME="appname"; // id value for employee
    public final static String APP_TIME="time";  // name of employee
    /**
     *
     * @param context
     */
    public SqlOperator(Context context){
        dbHelper = new DatabaseParent(context);
        database = dbHelper.getWritableDatabase();
    }


    public long createRecords(String appname, String time){
        ContentValues values = new ContentValues();
        values.put(APP_NAME, appname);
        values.put(APP_TIME, time);
        return database.insert(APP_TABLE, null, values);
    }

    public Cursor selectRecords() {
        String[] cols = new String[] {APP_NAME, APP_TIME};
        Cursor mCursor = database.query(true, APP_TABLE,cols,null
                , null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor; // iterate to get each value.
    }
}