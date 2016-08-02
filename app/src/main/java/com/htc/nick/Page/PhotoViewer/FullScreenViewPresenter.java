package com.htc.nick.Page.PhotoViewer;

import android.content.Context;

import com.htc.nick.Base.BasePresenter;

import org.androidannotations.annotations.EBean;

/**
 * Created by nick on 8/2/16.
 */
@EBean
public class FullScreenViewPresenter extends BasePresenter {
    private final FullScreenView view;
    public FullScreenViewPresenter(Context context) {
        super(context);
        this.view = (FullScreenView) context;
    }
}
