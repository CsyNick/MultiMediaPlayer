package com.htc.nick.Item;

/**
 * Created by nick on 8/2/16.
 */
public class PhotoItem {
    private String name;
    private String path;
    private String thumbnailUri;

    public PhotoItem(String name,String path, String thumbnailUri) {
        this.name = name;
        this.path = path;
        this.thumbnailUri = thumbnailUri;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumbnailUri() {
        return thumbnailUri;
    }

    public void setThumbnailUri(String thumbnailUri) {
        this.thumbnailUri = thumbnailUri;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
