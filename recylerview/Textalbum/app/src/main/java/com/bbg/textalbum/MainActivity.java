package com.bbg.textalbum;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import com.truizlop.sectionedrecyclerview.SectionedSpanSizeLookup;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    RecyclerView RV;


   public static List<Photo> photos;
   public static List<String> photoDates;
    ContentResolver resolver;
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
   RVAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findView();
        getPhotos();
    }

    public void findView() {
        RV = (RecyclerView) findViewById(R.id.RV);
        resolver = getContentResolver();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Bundle bundle = msg.getData();
                    photos = (ArrayList<Photo>) bundle.getParcelableArrayList("photos").get(0);
                    photoDates = (ArrayList<String>) bundle.getParcelableArrayList("photos").get(1);

                     adapter = new RVAdapter(photos, photoDates, MainActivity.this);

                    GridLayoutManager manager = new GridLayoutManager(MainActivity.this, 3);
                    SectionedSpanSizeLookup lookup = new SectionedSpanSizeLookup(adapter, manager);
                    manager.setSpanSizeLookup(lookup);
                    RV.setLayoutManager(manager);
                    RV.setAdapter(adapter);
                    adapter.setOnItemClickListener(new RVAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Toast.makeText(MainActivity.this,position+": "+photos.get(position).getPath(),Toast.LENGTH_LONG).show();
                        }
                    });
            }
        }
    };

    public void getPhotos() {
        new Thread() {
            @Override
            public void run() {
                List<Photo> list = new ArrayList<Photo>();
                List<String> list1 = new ArrayList<String>();
                super.run();
                Cursor cursor = resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Images.Media.DATE_TAKEN + " desc");
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                        long date = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN));
                        long id=cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                        String name=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
                        String dec=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                        Date date1 = new Date(date);
                        String Sdate = format.format(date1);

                        Photo photo = new Photo(path, Sdate,id,dec,name);
                        if (!list1.contains(Sdate)) {
                            list1.add(Sdate);
                        }
                        list.add(photo);
                    } while (cursor.moveToNext());
                    cursor.close();
                    Message msg = new Message();
                    msg.what = 1;
                    Bundle bundle = new Bundle();
                    ArrayList Alist = new ArrayList();
                    Alist.add(list);
                    Alist.add(list1);
                    bundle.putParcelableArrayList("photos", Alist);


                    msg.setData(bundle);
                    handler.sendMessage(msg);
                }

            }
        }.start();
    }
}
