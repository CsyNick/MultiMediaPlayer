package com.htc.nick.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.htc.nick.Adapter.VideoGridViewAdapter;
import com.htc.nick.media.VideoManager;
import com.htc.nick.multimediaplayer.R;

/**
 * Created by nick_chung on 2016/7/26.
 */


public class VideoFragment extends Fragment {
    private View mRootView;
    private VideoManager videoManager;
    private VideoGridViewAdapter gridViewAdapter;
    private GridView gridView;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        videoManager = new VideoManager(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null){
            Log.e("Fragment","VideoFragment");
            mRootView = inflater.inflate(R.layout.fragment_video,container,false);
            gridView = (GridView) mRootView.findViewById(R.id.videoGridView);
            gridViewAdapter = new VideoGridViewAdapter(getContext(), R.layout.grid_item, videoManager.getVideoList());
            gridView.setAdapter(gridViewAdapter);
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
