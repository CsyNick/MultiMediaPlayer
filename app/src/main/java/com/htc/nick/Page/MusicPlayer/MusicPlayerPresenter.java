package com.htc.nick.Page.MusicPlayer;

import android.content.Context;
import android.media.MediaPlayer;

import com.htc.nick.Base.BasePresenter;

import org.androidannotations.annotations.EBean;

/**
 * Created by nick on 7/29/16.
 */
@EBean
public class MusicPlayerPresenter extends BasePresenter {
    private final MusicPlayerView view;
    public MusicPlayerPresenter(Context context) {
        super(context);
        this.view = (MusicPlayerView) context;
    }


}
