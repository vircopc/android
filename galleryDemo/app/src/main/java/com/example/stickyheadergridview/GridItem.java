package com.example.stickyheadergridview;

public class GridItem {
    private String path;
    private String time;
    private boolean isVedio;
    private int section;
    private boolean isChecked;
    private String lastModified;

    public GridItem(String path, String time ,boolean isVedio,boolean isChecked,String lastModified) {
        super();
        this.path = path;
        this.time = time;
        this.isVedio=isVedio;
        this.lastModified=lastModified;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isVedio() {
        return isVedio;
    }

    public int getSection() {
        return section;
    }

    public void setSection(int section) {
        this.section = section;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }


}
