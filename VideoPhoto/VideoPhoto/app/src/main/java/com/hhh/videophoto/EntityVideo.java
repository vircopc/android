package com.hhh.videophoto;

public class EntityVideo {
    String thumbPath;
    String addDate;
    public String getThumbPath() {
        return thumbPath;
    }

    public String getAddDate() {
        return addDate;
    }

    public void setAddDate(String addDate) {
        this.addDate = addDate;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }

    @Override
    public String toString() {
        return "EntityVideo{" +
                "thumbPath='" + thumbPath + '\'' +
                ", addDate='" + addDate + '\'' +
                '}';
    }
}
