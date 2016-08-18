package com.htc.nick.Adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

/**
 * Created by nickchung on 2016/8/18.
 */
public class ImageAdapter extends BaseAdapter {

    private final Context mContext;
    private int mItemHeight = 0;
    private int mNumColums = 0;
    private int mActionBarHeight = 0;
    private GridView.LayoutParams mImageViewLayoutParams;

    public ImageAdapter(Context context){
        super();
        this.mContext = context;
        mImageViewLayoutParams = new GridView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //Calculate ActionBar Height
        TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(
                android.R.attr.actionBarSize,tv,true)) {
            mActionBarHeight = TypedValue.complexToDimensionPixelSize(
                    tv.data,context.getResources().getDisplayMetrics());
        }
    }


    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }
}
