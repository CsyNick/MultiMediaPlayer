package com.htc.nick.Base;

import android.app.Activity;
import android.content.Context;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * Created by nick on 7/25/16.
 */
public abstract class BasePresenter<T> {

    protected Context context;
    protected Reference<T> reference;

    public BasePresenter(Context context){
        this.context = context;
    }

    public void attachView(T view){
        reference = new WeakReference<T>(view);
    }

    protected T getView() {
        return reference.get();
    }

    protected Activity getActivity() {
        return (Activity)context;
    }

    public void detachView() {
        if (reference != null) {
            reference.clear();
            reference = null;
        }
    }
}
