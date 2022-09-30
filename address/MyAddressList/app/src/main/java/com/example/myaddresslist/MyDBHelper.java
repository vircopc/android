package com.example.myaddresslist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MyDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "mydb.db";
    public static final String TABLE_NAME = "information";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_NUMBER = "number";
    public static final String FIELD_EMAIL = "email";

    public MyDBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table "+TABLE_NAME+"(_id integer primary key autoincrement, "+FIELD_NAME+" varchar(20), "+FIELD_NUMBER+" varchar(20), "+FIELD_EMAIL+" varchar(20))";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
