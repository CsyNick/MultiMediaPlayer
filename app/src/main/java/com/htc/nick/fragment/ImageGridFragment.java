/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.htc.nick.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.BuildConfig;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.htc.nick.Base.Constants;
import com.htc.nick.BitmapDisplay.util.ImageCache;
import com.htc.nick.BitmapDisplay.util.ImageFetcher;
import com.htc.nick.CustomView.RecyclingImageView;
import com.htc.nick.Item.PhotoItem;
import com.htc.nick.Page.PhotoViewer.GalleryActivity;
import com.htc.nick.mediaManager.PhotoManager;
import com.htc.nick.multimediaplayer.R;

import java.util.ArrayList;

/**
 * The main fragment that powers the ImageGridActivity screen. Fairly straight forward GridView
 * implementation with the key addition being the ImageWorker class w/ImageCache to load children
 * asynchronously, keeping the UI nice and smooth and caching thumbnails for quick retrieval. The
 * cache is retained over configuration changes like orientation change so the images are populated
 * quickly if, for example, the user rotates the device.
 */
public class ImageGridFragment extends Fragment implements AdapterView.OnItemClickListener {
    private static final String TAG = "ImageGridFragment";
    private static final String IMAGE_CACHE_DIR = "thumbs";

    private int mImageThumbSize;
    private int mImageThumbSpacing;
    private ImageAdapter mAdapter;
    private ImageFetcher mImageFetcher;
    private PhotoManager photoManager;
    private ArrayList<String> photoList;
    /**
     * Empty constructor as per the Fragment documentation
     */
    public ImageGridFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        photoManager = new PhotoManager(getContext());
        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
        mImageThumbSpacing = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);


        int permissionCheck = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.WRITE_EXTERNAL_STORAGE);
        } else {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    photoList = new ArrayList<>();
                    for (PhotoItem itemPath : photoManager.getPhotoList()){
                        photoList.add(itemPath.getPath());
                    }
                    mAdapter = new ImageAdapter(getActivity(), photoManager.getPhotoList());
                }
            });

        }


        ImageCache.ImageCacheParams cacheParams =
                new ImageCache.ImageCacheParams(getActivity(), IMAGE_CACHE_DIR);

        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new ImageFetcher(getActivity(), mImageThumbSize);
        mImageFetcher.setLoadingImage(R.mipmap.empty_pokemon);
        mImageFetcher.addImageCache(getActivity().getSupportFragmentManager(), cacheParams);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.image_grid_fragment, container, false);
        final GridView mGridView = (GridView) v.findViewById(R.id.gridView);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(this);
        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                // Pause fetcher to ensure smoother scrolling when flinging
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    // Before Honeycomb pause image loading on scroll to help with performance

                } else {
                    mImageFetcher.setPauseWork(false);
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });

        // This listener is used to get the final width of the GridView and then calculate the
        // number of columns and the width of each column. The width of each column is variable
        // as the GridView has stretchMode=columnWidth. The column width is used to set the height
        // of each view so we get nice square thumbnails.
        mGridView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @TargetApi(VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onGlobalLayout() {
                        if (mAdapter.getmNumColums() == 0) {
                            final int numColumns = (int) Math.floor(
                                    mGridView.getWidth() / (mImageThumbSize + mImageThumbSpacing));
                            if (numColumns > 0) {
                                final int columnWidth =
                                        (mGridView.getWidth() / numColumns) - mImageThumbSpacing;
                                mAdapter.setmNumColums(numColumns);
                                mAdapter.setmItemHeight(columnWidth);
                                if (BuildConfig.DEBUG) {
                                    Log.d(TAG, "onCreateView - numColumns set to " + numColumns);
                                }

                                mGridView.getViewTreeObserver()
                                        .removeGlobalOnLayoutListener(this);

                            }
                        }
                    }
                });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("NickFragment","onResume-"+TAG);
        mImageFetcher.setExitTasksEarly(false);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        mImageFetcher.setPauseWork(false);
        mImageFetcher.setExitTasksEarly(true);
        mImageFetcher.flushCache();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mImageFetcher.closeCache();
    }

    @TargetApi(VERSION_CODES.JELLY_BEAN)
    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        Intent i = new Intent(getContext(), GalleryActivity.class);
        i.putExtra("position", position-3);
        i.putStringArrayListExtra(GalleryActivity.EXTRA_NAME, photoList);
        getContext().startActivity(i);

    }


    /**
     * Created by nickchung on 2016/8/18.
     */
    private class ImageAdapter extends BaseAdapter {

        private final Context mContext;
        private int mItemHeight = 0;
        private int mNumColums = 0;
        private int mActionBarHeight = 0;
        private GridView.LayoutParams mImageViewLayoutParams;
        private ArrayList<PhotoItem> mPhooItem;

        public ImageAdapter(Context context, ArrayList<PhotoItem> photoItems) {
            super();
            this.mContext = context;
            mImageViewLayoutParams = new GridView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            //Calculate ActionBar Height
            TypedValue tv = new TypedValue();
            if (context.getTheme().resolveAttribute(
                    android.R.attr.actionBarSize, tv, true)) {
                mActionBarHeight = TypedValue.complexToDimensionPixelSize(
                        tv.data, context.getResources().getDisplayMetrics());
            }

            this.mPhooItem = photoItems;

        }


        @Override
        public int getCount() {

            if (getmNumColums() == 0) {
                return 0;
            }
            return mPhooItem.size() + mNumColums;
        }

        @Override
        public Object getItem(int pos) {
            return pos < mNumColums ? 0 : mPhooItem.get(pos - mNumColums);
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
                        ViewGroup.LayoutParams.MATCH_PARENT, 0));

                return convertView;
            }
            // Now handle the main ImageView thumbnails
            ImageView imageview;
            if (convertView == null) { // if it's not recycled, instantiate and initialize
                imageview = new RecyclingImageView(mContext);
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
           mImageFetcher.loadImage(mPhooItem.get(pos- mNumColums).getThumbnailUri()
                   , imageview);
//            Log.d(TAG,mPhooItem.get(pos- mNumColums).getThumbnailUri());
//            mImageFetcher.loadImage(Images.imageThumbUrls[pos- mNumColums], imageview);
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
                    new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mItemHeight);
            mImageFetcher.setImageSize(height);
            notifyDataSetChanged();

        }
    }
}