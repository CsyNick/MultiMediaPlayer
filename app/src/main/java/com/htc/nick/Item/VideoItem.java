package com.htc.nick.Item;

import android.graphics.Bitmap;

/**
 * Created by nick on 7/28/16.
 */
public class VideoItem {
    private String fileName;
    private String path;
    private String thumbnailUri;


    public VideoItem(String fileName,String path,String thumbnailUri) {
        super();
        this.fileName = fileName;
        this.path = path;
        this.thumbnailUri = thumbnailUri;
    }

    public String getThumbnail() {
        return thumbnailUri;
    }

    public void setThumbnailUri(String thumbnailUri) {
        this.thumbnailUri = thumbnailUri;
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
