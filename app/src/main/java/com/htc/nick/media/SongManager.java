package com.htc.nick.media;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by nick on 7/28/16.
 */
public class SongManager {

    private ArrayList<HashMap<String, String>> songsList = new ArrayList<>();
    private Context context;

    public SongManager(Context context) {
        this.context = context;
    }

    public ArrayList<HashMap<String, String>> getSongList() {
        HashMap<String, String> song;
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DATA}, null, null,
                "LOWER(" + MediaStore.Audio.Media.TITLE + ") ASC");

        int count = cursor.getCount();

        String[] songs = new String[count];
        String[] mAudioPath = new String[count];
        int i = 0;
        if (cursor.moveToFirst()) {
            do {
                song = new HashMap<>();
                songs[i] = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                mAudioPath[i] = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                if(songs[i].endsWith(".mp3") || songs[i].endsWith(".MP3")) {
                    song.put("songTitle", songs[i].substring(0, (songs[i].length() - 4)));
                    song.put("songPath", mAudioPath[i]);
                    songsList.add(song);
                }
                i++;

            } while (cursor.moveToNext());
        }

        cursor.close();
        return songsList;
    }


}
