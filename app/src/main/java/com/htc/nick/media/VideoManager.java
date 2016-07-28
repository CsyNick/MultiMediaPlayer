package com.htc.nick.media;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.htc.nick.Item.VideoItem;

import java.util.ArrayList;
import java.util.HashMap;

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
        int count = cursor.getCount();
        String[] videos = new String[count];
        String[] videoPath = new String[count];
        Bitmap [] thumbnails = new Bitmap[count];
        int i = 0;
        if (cursor.moveToFirst()) {
            do {

                videos[i] = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
                videoPath[i] = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                int videoId = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
                ContentResolver cr = context.getContentResolver();
                BitmapFactory.Options options=new BitmapFactory.Options();
                options.inDither = false;
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;

                thumbnails[i] = MediaStore.Video.Thumbnails.getThumbnail(cr, videoId, MediaStore.Video.Thumbnails.MINI_KIND, options);
                videoItem = new VideoItem(videos[i],videoPath[i],thumbnails[i]);
                videoList.add(videoItem);
                i++;
            } while (cursor.moveToNext());
        }
        return videoList;
    }


}
