package com.george.autorunpro;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Akshay on 23-Feb-16.
 */
public class DatabaseParent extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "AutorunPro";

    private static final int DATABASE_VERSION = 2;

    // Database creation sql statement
    private static final String DATABASE_CREATE1 = "create table AppAlarms(" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "appname text not null," +
            "time text not null, " +
            "monday int not null," +
            "tuesday int not null," +
            "wednesday int not null," +
            "thursday int not null," +
            "friday int not null," +
            "saturday int not null," +
            "sunday int not null," +
            "mode int not null," +
            "status int not null);";

    // Database creation sql statement
    private static final String DATABASE_CREATE2 = "create table FuncAlarms(" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "funcname text not null," +
            "time text not null, " +
            "monday int not null," +
            "tuesday int not null," +
            "wednesday int not null," +
            "thursday int not null," +
            "friday int not null," +
            "saturday int not null," +
            "sunday int not null," +
            "mode int not null," +
            "status int not null);";

    public DatabaseParent(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE1);
        database.execSQL(DATABASE_CREATE2);
        database.execSQL("INSERT INTO SQLITE_SEQUENCE VALUES('FuncAlarms',499)");

    }

    // Method is called during an upgrade of the database,
    @Override
    public void onUpgrade(SQLiteDatabase database,int oldVersion,int newVersion){
        Log.w(DatabaseParent.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS AppAlarms");
        database.execSQL("DROP TABLE IF EXISTS FuncAlarms");
        onCreate(database);
    }
}