package com.example.stickyheadergridview;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;


public class showVedioActivity extends Activity {
    ImageView mShowVedioImage, mPlayVedio;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_vedio_activity);
        mShowVedioImage = findViewById(R.id.show_vedio_image);
        mPlayVedio = findViewById(R.id.play_vedio);
        final String path = getIntent().getStringExtra("VEDIO_PATH");
        Glide.with(this)
                .load(path)
                .into(mShowVedioImage);
        mPlayVedio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(path);
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), "video/*");
                startActivity(intent);
            }
        });

    }

}
