package com.example.sqlite2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
//import android.icu.util.Calendar;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    //日付を格納する変数
    String today_str;
    Calendar today_cal;

    FloatingActionButton fab;
    TextView showDate, prevBtn, nextBtn;;
    LinearLayout inLayout, outLayout;

    //added by fujii
    TextView sum, sum_income, sum_expense, sum_month;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showDate = (TextView) findViewById(R.id.showDate);
        fab = (FloatingActionButton)findViewById(R.id.fab);
        inLayout = (LinearLayout)findViewById(R.id.inLayout);
        outLayout = (LinearLayout)findViewById(R.id.outLayout);
        prevBtn = (TextView) findViewById(R.id.prevBtn);
        nextBtn = (TextView) findViewById(R.id.nextBtn);

        //added by fujii
        sum = (TextView)findViewById(R.id.sum);
        sum_income = (TextView)findViewById(R.id.sum_income);
        sum_expense = (TextView)findViewById(R.id.sum_expense);
        sum_month = (TextView)findViewById(R.id.sum_month);

        showDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //もうこれは暗記したほうが良いものと気づいた
                //ここに処理を記入
                //Calendarインスタンスの取得
                Calendar cal = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener(){
                            @Override
                            //下のyearとかmonthとかdayは"選ばれた年・月・日"→私が指定するまでもなく決まっている
                            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                                //ここに日付を設定したときに行いたい処理を書く
                                today_str = String.format("%d/%02d/%02d", year, month+1, dayOfMonth);
                                today_cal.set(year, month, dayOfMonth);
//                                Log.d("fujii", "test"+year);
//                                Log.d("fujii", "test"+month);
//                                Log.d("fujii", "test"+dayOfMonth);
//                                test_cal.set(2020, 8, 9);
//                                Log.d("fujii", "onDateSet: ");
//                                today_cal.set(year, month, dayOfMonth); //　←落ちてしまう
//                                test_cal.set(year, month, dayOfMonth);
//                                Log.d("fujii", "onDateSet: ");
                                showDate.setText(today_str);
                                //データベースを表示
                                showDB();
                            }
                        },cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        });

        //矢印ボタンにリスナーを設定
        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Calendar変数を前日に設定する
                today_cal.add(Calendar.DAY_OF_MONTH, -1);
                today_str = calToStr(today_cal);
                //日付を表示
                showDate.setText(today_str);
                //データベースを表示
                showDB();
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Calendar変数を翌日に設定する
                today_cal.add(Calendar.DAY_OF_MONTH, 1);
                today_str = calToStr(today_cal);
                //日付を表示
                showDate.setText(today_str);
                //データベースを表示
                showDB();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intentインスタンスの生成
                //ここはApplicationのコンテクストなのか.....あとでしっかり調べよう
                Intent intent = new Intent(getApplication(), AddActivity.class);
                //activityの開始
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        //今日の日付を取得
        today_cal = Calendar.getInstance();
        today_str = calToStr(today_cal);
        //日付を表示
        showDate.setText(today_str);

        //データベースを表示
        showDB();
    }

    //CalendarからStringに変換するメソッド
    protected String calToStr(Calendar cal){
        String str = String.format("%d/%02d/%02d", cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH)+1, cal.get(Calendar.DAY_OF_MONTH));
        return str;
    }

    protected void showDB(){
        //データベースの取得
        MyDBHelper helper = new MyDBHelper(MainActivity.this);
        SQLiteDatabase db = helper.getWritableDatabase();

        //表示のクリア
        inLayout.removeAllViews();
        outLayout.removeAllViews();

        final String FORMAT = "%-9s %6d円";
        //検索して表示
        Cursor c = db.query("default_tb", new String[]{"category_col","value_col"}, "date_col =? AND balance_col =?", new String[] {today_str, "収入"},
                null, null, null);
        boolean bool = c.moveToFirst();
//        final String FORMAT = "%-9s %,6d円";
        while(bool){
            TextView tv = new TextView(MainActivity.this);
//            tv.setText(c.getString(0)+c.getInt(1));
            tv.setText(String.format(FORMAT,c.getString(0),c.getInt(1)));

            Log.d("fujii_debug0", c.getString(0) );
            Log.d("fujii_debug1", c.getString(1) );
//            Log.d("fujii_debug2", String.format(FORMAT,c.getString(0),c.getString(1)));
            inLayout.addView(tv);
            bool = c.moveToNext();
        }
        c.close();

        c = db.query("default_tb", new String[]{"category_col","value_col"},
                "date_col =? AND balance_col =?", new String[] {today_str, "支出"},
                null, null, null);
        bool = c.moveToFirst();
        while(bool){
            TextView tv = new TextView(MainActivity.this);
//            tv.setText(c.getString(0));
            tv.setText(String.format(FORMAT,c.getString(0),c.getInt(1)));
            outLayout.addView(tv);
            bool = c.moveToNext();
        }
        c.close();


        //収入の合計を計算
        Cursor income_c = db.rawQuery("SELECT SUM(value_col) FROM default_tb " +
                "WHERE date_col = ? AND balance_col =? ", new String[] {today_str, "収入"});
        if(income_c.moveToFirst()) {
            sum_income.setText(String.valueOf(income_c.getInt(0)));
        }
        income_c.close();

        //収入の合計を計算
        Cursor expense_c = db.rawQuery("SELECT SUM(value_col) FROM default_tb " +
                "WHERE date_col = ? AND balance_col =? ", new String[] {today_str, "支出"});
        if(expense_c.moveToFirst()) {
            sum_expense.setText(String.valueOf(expense_c.getInt(0)));
        }
        income_c.close();


        //1行1列のCursorを返す↓
        //全体の合計を計算
//        Cursor cursor = db.rawQuery("SELECT SUM(value_col) FROM default_tb WHERE date_col = today_str", null);
        Cursor cursor = db.rawQuery("SELECT SUM(value_col) FROM default_tb " +
                "WHERE date_col = ?", new String[] {today_str});
//        Cursor cursor = db.rawQuery("SELECT SUM(value_col) FROM default_tb", null);
        if(cursor.moveToFirst()) {
////            return cursor.getInt(0);
            sum.setText(String.valueOf(cursor.getInt(0)));
        }
        cursor.close();

        //全体の合計を計算
//        Cursor cursor = db.rawQuery("SELECT SUM(value_col) FROM default_tb WHERE date_col = today_str", null);
        Cursor month_c = db.rawQuery("SELECT SUM(value_col) FROM default_tb " +
                "WHERE substr(date_col,1,7) = ?", new String[] {today_str.substring(0,7)});
        if(month_c.moveToFirst()) {
            sum_month.setText(String.valueOf(month_c.getInt(0)));
        }
        cursor.close();
        Log.d("test", today_str.substring(0,7));


        Cursor deleteme = db.rawQuery("SELECT * FROM default_tb",null);
        if(deleteme.moveToFirst()) {
            Log.d("test1", deleteme.getString(2).substring(0,7));
        }

    }
}