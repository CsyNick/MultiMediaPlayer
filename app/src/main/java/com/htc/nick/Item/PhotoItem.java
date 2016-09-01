package com.htc.nick.Item;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by nick on 8/2/16.
 */
public class PhotoItem implements Parcelable {
    private String name;
    private String path;
    private String thumbnailUri;
    private String description;
    private String date;
    private String contentType;
    private String width;
    private String height;
    private long size; //KB
    public PhotoItem(String name,String path, String thumbnailUri) {
        this.name = name;
        this.path = path;
        this.thumbnailUri = thumbnailUri;
    }

    public PhotoItem(String name, String path, String thumbnailUri, String description, String date, String contentType, String width, String height,long size) {
        this.name = name;
        this.path = path;
        this.thumbnailUri = thumbnailUri;
        this.description = description;
        this.date = date;
        this.contentType = contentType;
        this.width = width;
        this.height = height;
        this.size = size / 1024;
    }


    protected PhotoItem(Parcel in) {
        name = in.readString();
        path = in.readString();
        thumbnailUri = in.readString();
        description = in.readString();
        date = in.readString();
        contentType = in.readString();
        width = in.readString();
        height = in.readString();
        size = in.readLong();
    }

    public static final Creator<PhotoItem> CREATOR = new Creator<PhotoItem>() {
        @Override
        public PhotoItem createFromParcel(Parcel in) {
            return new PhotoItem(in);
        }

        @Override
        public PhotoItem[] newArray(int size) {
            return new PhotoItem[size];
        }
    };

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


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }


    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getResolution(){
        return width+"X"+height;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(path);
        parcel.writeString(thumbnailUri);
        parcel.writeString(description);
        parcel.writeString(date);
        parcel.writeString(contentType);
        parcel.writeString(width);
        parcel.writeString(height);
        parcel.writeLong(size);
    }
}
