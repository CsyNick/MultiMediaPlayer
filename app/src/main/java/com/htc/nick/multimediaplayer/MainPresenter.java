package com.htc.nick.multimediaplayer;

import android.content.Context;

import com.htc.nick.Base.BasePresenter;

import org.androidannotations.annotations.EBean;

/**
 * Created by nick on 7/25/16.
 */
@EBean
public class MainPresenter extends BasePresenter{
    private final MainView view;
    public MainPresenter(Context context) {
        super(context);
        this.view = (MainView) context;
    }
}
