package com.hhh.videophoto;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Permission;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private List<Object> mList;
    private List<String> mDate;
    private PhotoAdapter mAdapter;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            mRecyclerView = findViewById(R.id.recycler_view);
            initDate();
            mAdapter = new PhotoAdapter();
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this,4);
            mRecyclerView.setLayoutManager(gridLayoutManager);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.setData(mList);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(this, "未授予权限,已退出该APP", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
    private void initDate() {
        mDate  = new ArrayList<>();
        List<EntityVideo> list = getList(this);
        if (list.size() == 0){
            Toast.makeText(this, "视频列表为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        System.out.println(list);
        Collections.sort(list, new Comparator<EntityVideo>() {
            @Override
            public int compare(EntityVideo o1, EntityVideo o2) {
                int i = o1.getAddDate().compareTo(o2.getAddDate());
                return i;
            }
        });
        String t = "";
        for(int i = list.size()-1; i >=0; i--){
            if(!t.equals(list.get(i).getAddDate())){
                mDate.add(list.get(i).getAddDate());
                t = list.get(i).getAddDate();
            }
        }
        mList = new ArrayList<>();
        for (int j=0;j<mDate.size();j++){
            mList.add(mDate.get(j));
            for(int i = 0; i < list.size(); i++){
                if(list.get(i).getAddDate().equals(mDate.get(j))){
                    mList.add(list.get(i).getThumbPath());
                }
            }
        }
    }

    public List<EntityVideo> getList(Context context) {
        List<EntityVideo> sysVideoList = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media
                        .EXTERNAL_CONTENT_URI,
                null, null, null, null);
        if (cursor == null) {
            return sysVideoList;
        }
        if (cursor.moveToFirst()) {
            do {
                String path = cursor.getString(cursor
                        .getColumnIndex(MediaStore.Video.Media.DATA));
                long dateAdded = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED));
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
                Date date1 = new Date(dateAdded*1000L);
                String format = simpleDateFormat.format(date1);
                EntityVideo info = new EntityVideo();
                info.setAddDate(format);
                info.setThumbPath(path);
                if(path.endsWith(".mp4")){
                    sysVideoList.add(info);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return sysVideoList;
    }
}
