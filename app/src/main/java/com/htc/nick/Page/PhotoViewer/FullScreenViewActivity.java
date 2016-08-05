package com.htc.nick.Page.PhotoViewer;

import android.content.Intent;
import android.support.v4.view.ViewPager;

import com.htc.nick.Adapter.FullScreenImageAdapter;
import com.htc.nick.Base.BaseActivity;
import com.htc.nick.mediaManager.PhotoManager;
import com.htc.nick.multimediaplayer.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * Created by nick on 8/2/16.
 */

@EActivity(R.layout.activity_fullscreen_view)
public class FullScreenViewActivity extends BaseActivity<FullScreenView,FullScreenViewPresenter> implements FullScreenView {

    private FullScreenImageAdapter adapter;

    @ViewById
    protected ViewPager pager;

    PhotoManager photoManager;
    @Override
    protected FullScreenViewPresenter createPresenter() {
        return FullScreenViewPresenter_.getInstance_(this);
    }

    @AfterViews
    protected void init(){

        Intent i = getIntent();
        int position = i.getIntExtra("position", 0);
        photoManager = new PhotoManager(this);
        adapter = new FullScreenImageAdapter(this,
                photoManager.getPhotoList());

        pager.setAdapter(adapter);

        // displaying selected image first
        pager.setCurrentItem(position);
    }


}
