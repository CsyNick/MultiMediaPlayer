package com.htc.nick.fragment;

import android.Manifest;
import android.content.Intent;
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
import android.widget.GridView;
import android.widget.TextView;

import com.htc.nick.Adapter.PhotoGridViewAdapter;
import com.htc.nick.Adapter.VideoGridViewAdapter;
import com.htc.nick.Base.Constants;
import com.htc.nick.Item.VideoItem;
import com.htc.nick.Page.VideoPlayer.VideoPlayerActivity;
import com.htc.nick.Page.VideoPlayer.VideoPlayerActivity_;
import com.htc.nick.media.VideoManager;
import com.htc.nick.multimediaplayer.R;

import java.util.ArrayList;

/**
 * Created by nick_chung on 2016/7/26.
 */


public class VideoFragment extends Fragment {
    private View mRootView;
    private VideoManager videoManager;
    private VideoGridViewAdapter gridViewAdapter;
    private GridView gridView;
    private static final String MEDIA = "media";
    private static final String URL = "url";
    ArrayList<VideoItem> videoList = null;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        videoManager = new VideoManager(getContext());
        videoList = videoManager.getVideoList();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null){
            Log.e("Fragment","VideoFragment");
            mRootView = inflater.inflate(R.layout.fragment_video,container,false);
            gridView = (GridView) mRootView.findViewById(R.id.videoGridView);
            int permissionCheck = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.WRITE_EXTERNAL_STORAGE);
            } else {

                    gridViewAdapter = new VideoGridViewAdapter(getContext(), R.layout.grid_item,videoList);
                    gridView.setAdapter(gridViewAdapter);

            }

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    Intent intent = new Intent( view.getContext() , VideoPlayerActivity_.class);
                    intent.putExtra(MEDIA, 4);
                    intent.putExtra	(URL, videoManager.getVideoList().get(position).getPath());
                    startActivity(intent);
                }
            });
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

    }
}
