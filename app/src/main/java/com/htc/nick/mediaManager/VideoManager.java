package com.htc.nick.mediaManager;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

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
        videoList.clear();
        if (cursor.moveToFirst()) {
            do {

                String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
                String videoPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                String thumbnailsUri = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA));
                Log.d("VideoManager",thumbnailsUri);
                videoItem = new VideoItem(name,videoPath,thumbnailsUri);
                videoList.add(videoItem);
            } while (cursor.moveToNext());
        }
        return videoList;
    }


}
