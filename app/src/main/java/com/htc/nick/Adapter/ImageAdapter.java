package com.htc.nick.Adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.htc.nick.CustomView.RecycleingImageView;
import com.htc.nick.Item.PhotoItem;

import java.util.ArrayList;

/**
 * Created by nickchung on 2016/8/18.
 */
public class ImageAdapter extends BaseAdapter {

    private final Context mContext;
    private int mItemHeight = 0;
    private int mNumColums = 0;
    private int mActionBarHeight = 0;
    private GridView.LayoutParams mImageViewLayoutParams;
    private ArrayList<PhotoItem> mPhooItem;
    public ImageAdapter(Context context, ArrayList<PhotoItem> photoItems){
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

        this.mPhooItem = photoItems;

    }



    @Override
    public int getCount() {

        if(getmNumColums() == 0){
            return 0;
        }
        return mPhooItem.size() + mNumColums;
    }

    @Override
    public Object getItem(int pos) {
        return pos < mNumColums ? 0 : mPhooItem.get(pos-mNumColums);
    }

    @Override
    public long getItemId(int pos) {
        return pos < mNumColums ? 0 : pos - mNumColums;
    }

    @Override
    public int getViewTypeCount() {
        // Two types of views, the normal ImageView and the top row of empty views
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return (position < mNumColums) ? 1 : 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup container) {

        if (pos < mNumColums) {
            if (convertView == null) {
                convertView = new View(mContext);
            }
            convertView.setLayoutParams(new AbsListView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,mActionBarHeight));

            return convertView;
        }
        // Now handle the main ImageView thumbnails
        ImageView imageview;
        if (convertView == null) { // if it's not recycled, instantiate and initialize
            imageview = new RecycleingImageView(mContext);
            imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageview.setLayoutParams(mImageViewLayoutParams);
        } else {
            imageview = (ImageView) convertView;
        }

        // Check the height matches our calculated column width
        if (imageview.getLayoutParams().height != mItemHeight) {
            imageview.setLayoutParams(mImageViewLayoutParams);
        }
        // Finally load the image asynchronously into the ImageView, this also takes care of
        // setting a placeholder image while the background thread runs
        //Todo mImageFetcher.loadImage(Images.imageThumbUrls[position - mNumColumns], imageView);
        return imageview;

    }


    public int getmNumColums() {
        return mNumColums;
    }

    public void setmNumColums(int numColums) {
        this.mNumColums = numColums;
    }

    public void setmItemHeight(int height) {

        if (height == mItemHeight) {
            return;
        }
        this.mItemHeight = height;
        mImageViewLayoutParams =
                new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,mItemHeight);
        // TODO: 2016/8/18 mImageFetcher.setImageSize(height);
        notifyDataSetChanged();

    }
}
