package com.htc.nick.media;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import com.htc.nick.Item.VideoItem;

import java.util.ArrayList;

/**
 * Created by nick on 7/28/16.
 */
public class VideoManager {
    private Context context;
    private ArrayList<VideoItem> videoList = new ArrayList<>();
    private VideoItem videoItem;
    public VideoManager(Context context){
        this.context = context;
    }

    public ArrayList<VideoItem> getVideoList() {
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Video.Media._ID,MediaStore.Video.Media.DISPLAY_NAME,MediaStore.Video.VideoColumns.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        int i = 0;
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
                String videoPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                String thumbnailsUri = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA));
                videoItem = new VideoItem(name,videoPath,thumbnailsUri);
                videoList.add(videoItem);
                i++;
            } while (cursor.moveToNext());
        }
        return videoList;
    }


}
