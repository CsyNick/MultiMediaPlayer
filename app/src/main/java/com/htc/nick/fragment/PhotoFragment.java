package com.htc.nick.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.htc.nick.Adapter.PhotoGridViewAdapter;
import com.htc.nick.media.PhotoManager;
import com.htc.nick.multimediaplayer.R;

/**
 * Created by nick_chung on 2016/7/26.
 */


public class PhotoFragment extends Fragment {
    private View mRootView;
    private PhotoManager photoManager;
    private PhotoGridViewAdapter adapter;
    private GridView gridView;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        photoManager = new PhotoManager(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null){
            Log.e("Fragment","PhotoFragment");
            mRootView = inflater.inflate(R.layout.fragment_photo,container,false);
            gridView = (GridView) mRootView.findViewById(R.id.photoGridView);
            adapter = new PhotoGridViewAdapter(getContext(),R.layout.photo_grid_item,photoManager.getPhotoList());
            gridView.setAdapter(adapter);
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
