package com.example.myaddresslist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etName, etNumber, etEmail;
    private Button btnSave, btnDelete, btnQuery, btnUpdate;
    private TextView tvContent;
    private MyDBHelper dbHelper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        dbHelper = new MyDBHelper(this);
        initEvent();
    }

    private void initEvent() {
        btnUpdate.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnQuery.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
    }

    private void initView() {
        etNumber = findViewById(R.id.et_number);
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        btnDelete = findViewById(R.id.btn_delete);
        btnQuery = findViewById(R.id.btn_query);
        btnSave = findViewById(R.id.btn_save);
        btnUpdate = findViewById(R.id.btn_update);
        tvContent = findViewById(R.id.tv_content);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_delete:
                delete();
                break;
            case R.id.btn_save:
                save();
                break;
            case R.id.btn_query:
                query();
                break;
            case R.id.btn_update:
                update();
                break;
        }
    }

    private void update() {
        String name = etName.getText().toString();
        String number = etNumber.getText().toString();
        String email = etEmail.getText().toString();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MyDBHelper.FIELD_NUMBER, number);
        //whereClause :  name =?
        int result = db.update(MyDBHelper.TABLE_NAME, values, MyDBHelper.FIELD_NAME+"=?", new String[]{name});
        if (result > 0){
            Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "修改失败", Toast.LENGTH_SHORT).show();
        }
        db.close();
    }

    private void query() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(MyDBHelper.TABLE_NAME, null, null, null, null, null, null);
        if (cursor.getCount() == 0){
            Toast.makeText(this, "没有数据", Toast.LENGTH_SHORT).show();
            tvContent.setText("");
        } else {
            String result = "";
            while (cursor.moveToNext()){
                String name = cursor.getString(1);
                String number = cursor.getString(2);
                String email = cursor.getString(3);
                result += name+"\t\t"+number+"\t\t"+email+"\n";
            }
            cursor.close();
            db.close();
            tvContent.setText(result);
        }
    }

    private void save() {
        String name = etName.getText().toString();
        String number = etNumber.getText().toString();
        String email = etEmail.getText().toString();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MyDBHelper.FIELD_NAME, name);
        values.put(MyDBHelper.FIELD_NUMBER, number);
        values.put(MyDBHelper.FIELD_EMAIL, email);
        long result = db.insert(MyDBHelper.TABLE_NAME, null, values);
        if (result > 0){
            Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "添加失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void delete() {
        String name = etName.getText().toString();
        String number = etNumber.getText().toString();
        String email = etEmail.getText().toString();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = db.delete(MyDBHelper.TABLE_NAME, MyDBHelper.FIELD_NAME+"=?", new String[]{name});
        if (result > 0){
            Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "删除失败", Toast.LENGTH_SHORT).show();
        }
    }
}