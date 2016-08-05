package com.htc.nick.mediaManager;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import com.htc.nick.Item.PhotoItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by nick on 7/28/16.
 */
public class PhotoManager {

    private ArrayList<PhotoItem> photoList = new ArrayList<>();
    private Context context;

    public PhotoManager(Context context) {
        this.context = context;
    }

    public ArrayList<PhotoItem> getPhotoList() {
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{
                          MediaStore.Images.Media._ID
                        , MediaStore.Images.Media.DISPLAY_NAME
                        , MediaStore.Images.Thumbnails.DATA
                        , MediaStore.Images.Media.DATA
                        , MediaStore.Images.Media.MIME_TYPE
                        , MediaStore.Images.Media.DESCRIPTION
                        , MediaStore.Images.Media.SIZE
                        , MediaStore.Images.Media.DATE_ADDED
                        , MediaStore.Images.Media.WIDTH
                        , MediaStore.Images.Media.HEIGHT
                        , MediaStore.Images.Media.TITLE}, null, null,
                "LOWER(" + MediaStore.Images.Media.TITLE + ") ASC");

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
                String thumbnailUri = cursor.getString( cursor.getColumnIndex( MediaStore.Images.Thumbnails.DATA ) );
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                String description = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DESCRIPTION));
                String createTime = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
                String contentType = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE));
                String width = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.WIDTH));
                String height = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.HEIGHT));
                String size = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.SIZE));
                SimpleDateFormat sf = new SimpleDateFormat("yy年MM月dd日HH:mm");
                Date d = new Date(Long.valueOf(createTime)*1000);
                createTime = sf.format(d);
                photoList.add(new PhotoItem(name,path,thumbnailUri,description,createTime,contentType,width,height,Long.parseLong(size)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return photoList;
    }


}
