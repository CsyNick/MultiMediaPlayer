package com.htc.nick.CustomView;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.htc.nick.BitmapDisplay.util.RecyclingBitmapDrawable;

/**
 * Created by nickchung on 2016/8/18.
 */
public class RecyclingImageView extends ImageView {

    public RecyclingImageView(Context context) {super(context);}

    public RecyclingImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        final Drawable previousDrawable = getDrawable();
        //Call super to set new Drawable
        super.setImageDrawable(drawable);

        notifyDrawable(drawable,true);

        notifyDrawable(previousDrawable, false);

    }

    @Override
    protected void onDetachedFromWindow() {
        setImageDrawable(null);
        super.onDetachedFromWindow();
    }

    /**
     * Notifies the drawable that it's displayed state has changed
     *
     *
     */
    private static void notifyDrawable(Drawable drawable, final boolean isDisplayed) {
        if (drawable instanceof RecyclingBitmapDrawable) {
            ((RecyclingBitmapDrawable)drawable).setIsDisplayed(isDisplayed);
        } else if (drawable instanceof LayerDrawable) {
            LayerDrawable layerDrawable = (LayerDrawable) drawable;
            for (int i = 0, z = layerDrawable.getNumberOfLayers(); i < z; i++) {
                notifyDrawable(layerDrawable.getDrawable(i),isDisplayed);
            }
        }



    }
}
