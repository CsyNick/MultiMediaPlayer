package com.htc.nick.media;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by nick on 7/28/16.
 */
public class PhotoManager {

    private ArrayList<Bitmap> photoList = new ArrayList<>();
    private Context context;

    public PhotoManager(Context context) {
        this.context = context;
    }

    public ArrayList<Bitmap> getPhotoList() {
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID,MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATA}, null, null,
                "LOWER(" + MediaStore.Images.Media.TITLE + ") ASC");

        int count = cursor.getCount();

        Bitmap[] photos = new Bitmap[count];

        int i = 0;
        if (cursor.moveToFirst()) {
            do {
                int photoId = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                ContentResolver cr = context.getContentResolver();
                BitmapFactory.Options options=new BitmapFactory.Options();
                options.inDither = false;
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                photos[i] =  MediaStore.Images.Thumbnails.getThumbnail(cr, photoId, MediaStore.Images.Thumbnails.MINI_KIND, options);
                photoList.add(photos[i]);
                i++;

            } while (cursor.moveToNext());
        }

        cursor.close();
        return photoList;
    }


}
