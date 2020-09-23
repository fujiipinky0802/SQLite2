package com.example.sqlite2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Calendar;

public class AddActivity extends AppCompatActivity {

    //部品の取得
    EditText addDate, addMemo, addCost, addCategory;
    Spinner spinner;
    Button okBtn, cancelBtn;

    //Spinner用の変数
    String[] items = {"支出", "収入"};

    //データを入れる用の変数
    String date, memo, value_str, category, time;
    int value_int;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add2);

        //部品の取得
        addDate = (EditText)findViewById(R.id.addDate);
        addMemo = (EditText)findViewById(R.id.addMemo);
        addCost = (EditText)findViewById(R.id.addCost);
        addCategory = (EditText)findViewById(R.id.addCategory);
        spinner = (Spinner)findViewById(R.id.spinner);
        okBtn = (Button)findViewById(R.id.okBtn);
        cancelBtn = (Button)findViewById(R.id.cancelBtn);

        //Spinner用の変数
        String[] items = {"収入", "支出"};

        //日付の入力の設定
        addDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Calendarインスタンスの生成
                Calendar cal = Calendar.getInstance();
                //DatePickerDialogインスタンスの生成
                DatePickerDialog dialog = new DatePickerDialog(AddActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                //日付を取得して表示
                                addDate.setText(String.format("%d/%02d/%02d", year, month+1, dayOfMonth));
                            }
                        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH));
                //DatePickerDialogの表示
                dialog.show();
            }
        });

        //AM/PM入力の設定
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddActivity.this, android.R.layout.simple_spinner_dropdown_item,
                items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //Spinnerにリスナーを登録
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //選択されたStringを取得
                time = (String)parent.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //データベースの作成
        MyDBHelper helper = new MyDBHelper(AddActivity.this);
        final SQLiteDatabase db = helper.getWritableDatabase();

        //OKボタンクリック時の処理
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //categoryとdateを取得
                date = addDate.getText().toString();
                if(time=="収入"){
                    value_str = addCost.getText().toString();
                    value_int = Integer.parseInt(value_str);
                }else{
                    value_str = addCost.getText().toString();
                    value_int = -Integer.parseInt(value_str);
                }

                memo = addMemo.getText().toString();

                category = addCategory.getText().toString();
                //ContentValuesにデータを入れる
                ContentValues val = new ContentValues();
                val.put("date_col", date);
                val.put("memo_col", memo);
                val.put("value_col", value_int);
//                val.put("value_col",value_str);
                val.put("category_col", category);
                val.put("balance_col", time);

                Log.d("time is", time);

                //データベースに登録
                db.insert("default_tb", null, val);
                //Toastを表示
                Toast.makeText(AddActivity.this, "added category: "+category+" "+" "+date+" "+time,
                        Toast.LENGTH_SHORT).show();
                //activityの終了
                finish();
            }
        });

        //CANCELボタンクリック時の処理
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //activityの終了
                finish();
            }
        });


    }
}