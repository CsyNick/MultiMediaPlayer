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

import com.htc.nick.Adapter.VideoGridViewAdapter;
import com.htc.nick.Base.Constants;
import com.htc.nick.Item.VideoItem;
import com.htc.nick.Page.VideoPlayer.VideoPlayerActivity_;
import com.htc.nick.mediaManager.VideoManager;
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
    private static final String URL = "url";
    private static final String TITLE = "title";
    private static final String THUMBNAILS = "thumbnails";
    ArrayList<VideoItem> videoList = null;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        videoManager = new VideoManager(getContext());

    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null){
            Log.e("Fragment","VideoFragment");
            mRootView = inflater.inflate(R.layout.fragment_video,container,false);
            gridView = (GridView) mRootView.findViewById(R.id.videoGridView);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    Intent intent = new Intent( view.getContext() , VideoPlayerActivity_.class);
                    intent.putExtra(TITLE,videoManager.getVideoList().get(position).getFileName());
                    intent.putExtra	(URL, videoManager.getVideoList().get(position).getPath());
                    intent.putExtra(THUMBNAILS, videoManager.getVideoList().get(position).getThumbnail());
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
        Log.d("VideoFragment","onResume");
        int permissionCheck = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.WRITE_EXTERNAL_STORAGE);
        } else {
            videoList = videoManager.getVideoList();
            gridViewAdapter = new VideoGridViewAdapter(getContext(), R.layout.grid_item,videoList);
            gridView.setAdapter(gridViewAdapter);
        }

    }
}
