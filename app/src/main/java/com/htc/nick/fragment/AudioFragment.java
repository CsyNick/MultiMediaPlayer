package com.htc.nick.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.htc.nick.multimediaplayer.R;

/**
 * Created by nick_chung on 2016/7/26.
 */
public class AudioFragment extends Fragment{

    private View mRootView;
    private String[] mMusicList;
    private ListView audioList;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null){
            Log.e("Fragment","AudioFragment");
            mRootView = inflater.inflate(R.layout.fragment_audio,container,false);
             audioList = (ListView) mRootView.findViewById(R.id.audioList);
        }
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null){
            parent.removeView(mRootView);
        }
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        mMusicList = getMusic();
        ArrayAdapter<String> mAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1, mMusicList);
        audioList.setAdapter(mAdapter);

        for (int i = 0 ; i<mMusicList.length;i++){
            Log.d("AudioFragment",mMusicList[i].toString());
        }
    }

    private String[] getMusic() {
        final Cursor mCursor = getActivity().getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Audio.Media.DISPLAY_NAME }, null, null,
                "LOWER(" + MediaStore.Audio.Media.TITLE + ") ASC");

        int count = mCursor.getCount();

        String[] songs = new String[count];
        int i = 0;
        if (mCursor.moveToFirst()) {
            do {
                songs[i] = mCursor.getString(0);
                i++;
            } while (mCursor.moveToNext());
        }

        mCursor.close();

        return songs;
    }
}
