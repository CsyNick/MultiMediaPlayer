package com.htc.nick.Page.SlideShowPlayer;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.htc.nick.mediaManager.PhotoManager;
import com.htc.nick.multimediaplayer.R;

/**
 * Created by nick on 8/4/16.
 */
public class SlideShowPlayerActivity  extends Activity {

    /**
     * Called when the activity is first created.
     */
    PhotoManager photoManager;
    SlideShow slideShow;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_slideshow);
        photoManager = new PhotoManager(this);

        slideShow=(SlideShow)findViewById(R.id.slideShow);
        slideShow.setPhotoList(photoManager.getPhotoList());
        slideShow.start(2000,0);
    }



}
