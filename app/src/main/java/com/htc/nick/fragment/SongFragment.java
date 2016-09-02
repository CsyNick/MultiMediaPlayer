package com.htc.nick.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
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

import com.htc.nick.Base.Constants;
import com.htc.nick.Page.MusicPlayer.MusicPlayerActivity_;
import com.htc.nick.mediaManager.SongManager;
import com.htc.nick.multimediaplayer.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by nick_chung on 2016/7/26.
 */

public class SongFragment extends Fragment {
    private static final String TAG = "SongFragment";
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
        setRetainInstance(true);
        Log.d("NickFragment","onCreate-"+TAG);
        songManager = new SongManager(getContext());
        songsListData = new ArrayList<>();
        songsListPath = new ArrayList<>();

        int permissionCheck = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {

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
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null) {
            Log.e("Fragment", "SongFragment");
            mRootView = inflater.inflate(R.layout.fragment_audio, container, false);
            audioListView = (ListView) mRootView.findViewById(R.id.audioList);
        }
        audioListView.setAdapter(mAdapter);

        audioListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int songIndex = position;
                MusicPlayerActivity_.intent(getContext()).extra("songIndex",songIndex).start();

            }
        });
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("NickFragment","onResume-"+TAG);

    }

    private void init(){




    }


}
