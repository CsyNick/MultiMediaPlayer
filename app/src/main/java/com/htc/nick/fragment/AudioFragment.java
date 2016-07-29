package com.htc.nick.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.htc.nick.Adapter.PhotoGridViewAdapter;
import com.htc.nick.Base.Constants;
import com.htc.nick.media.SongManager;
import com.htc.nick.multimediaplayer.R;

import org.androidannotations.annotations.EFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by nick_chung on 2016/7/26.
 */
public class AudioFragment extends Fragment {

    private View mRootView;
    private ArrayList<HashMap<String,String>> mSongList;
    ArrayList<String> songsListData;
    ArrayList<String> songsListPath;
    private String[] mAudioPath;
    private ListView audioListView;
    private SongManager songManager;
    private ArrayAdapter<String> mAdapter;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        songManager = new SongManager(getContext());
        songsListData = new ArrayList<>();
        songsListPath = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null) {
            Log.e("Fragment", "AudioFragment");
            mRootView = inflater.inflate(R.layout.fragment_audio, container, false);
            audioListView = (ListView) mRootView.findViewById(R.id.audioList);
        }
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        int permissionCheck = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.WRITE_EXTERNAL_STORAGE);
        } else {
            init();
        }

    }

    private void init(){

        mSongList = songManager.getSongList();
        if (songsListData.size()==0) {
            for (int i = 0; i < mSongList.size(); i++) {

                HashMap<String, String> song = mSongList.get(i);

                songsListData.add(song.get("songTitle"));
                songsListPath.add(song.get("songPath"));

            }
        }
         mAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1, songsListData);
        audioListView.setAdapter(mAdapter);

        audioListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int songIndex = position;
                playMusic(songsListPath.get(songIndex));
            }
        });
    }


    private void playMusic(String path) {

        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
//mMediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
