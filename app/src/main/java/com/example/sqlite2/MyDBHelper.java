package com.example.sqlite2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDBHelper extends SQLiteOpenHelper {

    public MyDBHelper(Context context){
        super(context, "TASK_DB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE default_tb(_id INTEGER PRIMARY KEY NOT NULL, category_col TEXT, " +
                "date_col TEXT, balance_col TEXT, memo_col TEXT, value_col INTEGER);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }
}
