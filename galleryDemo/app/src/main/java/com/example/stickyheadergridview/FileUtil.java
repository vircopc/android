package com.example.stickyheadergridview;


import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class FileUtil {
    static SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
    static String url = Environment.getExternalStorageDirectory().toString() + "/DCIM/Camera";

    public static ArrayList getAllFile() {
        ArrayList<GridItem> GirdList =new <GridItem>ArrayList();
        File albumdir = new File(url);
        File[] imgfile = albumdir.listFiles();
        if (imgfile == null) {
            return GirdList;
        }
        int len = imgfile.length;
        boolean isVedio = false;
        GirdList.clear();
        for (int i = 0; i < len; i++) {
            if (MediaFile.isImageFileType(imgfile[i].getAbsolutePath())) {
                isVedio = false;
            } else if (MediaFile.isVideoFileType(imgfile[i].getAbsolutePath())) {
                isVedio = true;
            } else {
                continue;
            }
            GridItem mGridItem = new GridItem(imgfile[i].getAbsolutePath(), paserTimeToYM(imgfile[i].lastModified()), isVedio, false,String.valueOf(imgfile[i].lastModified()));
            GirdList.add(mGridItem);
        }
        return GirdList;
    }

    public static String paserTimeToYM(long time) {
        //System.setProperty("user.timezone", "Asia/Shanghai");
        //TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");
        //TimeZone.setDefault(tz);
        return format.format(new Date(time));
    }

    public static String getDuration(String path){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(path); //在获取前，设置文件路径（应该只能是本地路径）
        String duration =retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        retriever.release(); //释放
        if(!TextUtils.isEmpty(duration)){
            long dur = Long.parseLong(duration);
            int totalSeconds = (int) (dur / 1000);
            int seconds = totalSeconds % 60;
            int minutes = (totalSeconds / 60) % 60;
            int hours = totalSeconds / 3600;
            return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
        }
        return "";
    }

}
