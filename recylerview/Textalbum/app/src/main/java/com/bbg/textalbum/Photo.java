package com.bbg.textalbum;

/**
 * Created by administrator on 2017/10/19.
 */

public class Photo {
    private String path;
    private String date;
    private long id;
    private String discr;
    private String name;

    public Photo(String path, String date, long id, String discr, String name) {
        this.path = path;
        this.date = date;
        this.id = id;
        this.discr = discr;
        this.name = name;
    }

    public Photo(String path, String date) {
        this.path = path;
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDiscr() {
        return discr;
    }

    public void setDiscr(String discr) {
        this.discr = discr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
