package com.htc.nick.BitmapDisplay.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.design.BuildConfig;
import android.util.Log;

/**
 * Created by nickchung on 2016/8/18.
 */
public class RecycleingBitmapDrawable extends BitmapDrawable {

    static final String TAG = "CountingBitmapDrawable";

    private int mCacheRefCount = 0;
    private int mDisplayRefCount = 0;

    private boolean mHasBeenDisplayed;

    public RecycleingBitmapDrawable(Resources res, Bitmap bitmap) { super(res, bitmap); }

    public void setIsCached(boolean isCached) {

        synchronized (this) {
            if (isCached) {
                mCacheRefCount ++;
            } else {
                mCacheRefCount --;
            }
        }
    }

    public void setIsDisplayed(boolean isDisplayed) {

        synchronized (this) {
            if (isDisplayed){
                mDisplayRefCount ++;
                mHasBeenDisplayed = true;
            } else {
                mDisplayRefCount --;
            }
        }
    }

    private synchronized void checkState() {
        // If the drawable cache and display ref counts = 0, and this drawable
        // has been displayed, then recycle
        if (mCacheRefCount <= 0 && mDisplayRefCount <= 0 && mHasBeenDisplayed && hasValidBitmap()) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "No longer being used or cached so recycling. "
                        + toString());
            }

            getBitmap().recycle();
        }
    }
    private synchronized boolean hasValidBitmap(){
        Bitmap bitmap = getBitmap();
        return bitmap !=null && !bitmap.isRecycled();
    }

}
