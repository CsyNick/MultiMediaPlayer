package com.htc.nick.media;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.util.Log;

import com.htc.nick.Item.PhotoItem;

import java.util.ArrayList;
import java.util.HashMap;

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
                new String[]{MediaStore.Images.Media._ID,MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATA}, null, null,
                "LOWER(" + MediaStore.Images.Media.TITLE + ") ASC");

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
                String uri = cursor.getString( cursor.getColumnIndex( MediaStore.Images.Thumbnails.DATA ) );
                photoList.add(new PhotoItem(name,uri));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return photoList;
    }


}
