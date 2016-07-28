package com.htc.nick.Item;

import android.graphics.Bitmap;

/**
 * Created by nick on 7/28/16.
 */
public class VideoItem {
    private String fileName;
    private String path;
    private Bitmap thumbnail;


    public VideoItem(String fileName,String path,Bitmap thumbnail) {
        super();
        this.fileName = fileName;
        this.path = path;
        this.thumbnail = thumbnail;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
