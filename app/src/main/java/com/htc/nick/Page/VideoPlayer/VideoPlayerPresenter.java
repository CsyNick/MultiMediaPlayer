package com.htc.nick.Page.VideoPlayer;

import android.content.Context;

import com.htc.nick.Base.BasePresenter;
import com.htc.nick.Page.MusicPlayer.MusicPlayerView;

import org.androidannotations.annotations.EBean;

/**
 * Created by nick on 7/29/16.
 */
@EBean
public class VideoPlayerPresenter extends BasePresenter {
    private final VideoPlayerView view;
    public VideoPlayerPresenter(Context context) {
        super(context);
        this.view = (VideoPlayerView) context;
    }


}
