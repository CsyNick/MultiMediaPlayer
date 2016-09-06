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
import android.content.res.Configuration;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.BuildConfig;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ScrollView;

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
    private GridView mGridView;
    /**
     * Empty constructor as per the Fragment documentation
     */
    public ImageGridFragment() {
    }
    private static ImageGridFragment instance = new ImageGridFragment();

        public static ImageGridFragment getInstance () {
            return instance;
        }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Log.d("NickFragment","onCreate-"+TAG);
        photoManager = new PhotoManager(getContext());
        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
        mImageThumbSpacing = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);




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
        mGridView = (GridView) v.findViewById(R.id.gridView);
        new Thread(new Runnable() {  // download是耗時的動作，在另外建立一個thread來執行，所以，下一行的run()，這在個thread.start()後，會在另一個thread(worker thread)執行
            public void run() {

                int permissionCheck = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    photoList = new ArrayList<>();
                    for (PhotoItem itemPath : photoManager.getPhotoList()){
                        photoList.add(itemPath.getPath());
                    }
                    mAdapter = new ImageAdapter(getActivity(), photoManager.getPhotoList());
                }
                mGridView.post(new Runnable() {  // -> 利用ui元件進行post，下面那行的run會執行在ui元件所使用的thread上(Main Thread)
                    public void run() {
                        int gridViewEntrySize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
                        int gridViewSpacing = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);
                        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
                        Display display = wm.getDefaultDisplay();

                        int numColumns = (display.getWidth() - gridViewSpacing) / (gridViewEntrySize + gridViewSpacing);
                        if (numColumns > 0) {
                            final int columnWidth =
                                    (mGridView.getWidth() / numColumns) - mImageThumbSpacing;
                            Log.d(TAG, numColumns + "-numColumns columnWidth-" + columnWidth);
                            mAdapter.setmNumColums(3);
                            mAdapter.setmItemHeight(columnWidth);
                        }
                        mGridView.setAdapter(mAdapter);

                    }
                });
            }
        }).start();

        //mGridView.setAdapter(mAdapter);
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



        ViewGroup parent = (ViewGroup) v.getParent();
        if (parent != null) {
            parent.removeView(v);
        }
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("NickFragment","onResume-"+TAG);
//        refreshGridView();
        mImageFetcher.setExitTasksEarly(false);
        if(mAdapter!=null)
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

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        Intent i = new Intent(getContext(), GalleryActivity.class);
        i.putExtra("position", position-mAdapter.getmNumColums());
        i.putStringArrayListExtra(GalleryActivity.IMAGES_PATH, photoList);
        getContext().startActivity(i);

    }
//    private void refreshGridView() {
//
//
//       int gridViewEntrySize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
//       int gridViewSpacing = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);
//        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
//        Display display = wm.getDefaultDisplay();
//
//        int numColumns = (display.getWidth() - gridViewSpacing) / (gridViewEntrySize + gridViewSpacing);
//
//        mGridView.setNumColumns(numColumns);
//        Log.d(TAG,numColumns +"numColumns" );
//    }
//
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // This listener is used to get the final width of the GridView and then calculate the
        // number of columns and the width of each column. The width of each column is variable
        // as the GridView has stretchMode=columnWidth. The column width is used to set the height
        // of each view so we get nice square thumbnails.
        int gridViewEntrySize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
        int gridViewSpacing = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        int numColumns = (display.getWidth() - gridViewSpacing) / (gridViewEntrySize + gridViewSpacing);
        if (numColumns > 0) {
            mAdapter.setmNumColums(numColumns);
        }
    }



    /**
     * Created by nickchung on 2016/8/18.
     */
    private class ImageAdapter extends BaseAdapter {

        private final Context mContext;
        private int mItemHeight = 0;
        private int mNumColums = 0;

        private GridView.LayoutParams mImageViewLayoutParams;
        private ArrayList<PhotoItem> mPhooItem;

        public ImageAdapter(Context context, ArrayList<PhotoItem> photoItems) {
            super();
            this.mContext = context;
            mImageViewLayoutParams = new GridView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);


            this.mPhooItem = photoItems;

        }


        @Override
        public int getCount() {

            if (getmNumColums() == 0) {
                return 0;
            }
            return mPhooItem.size() + getmNumColums();
        }

        @Override
        public Object getItem(int pos) {
            return pos < mNumColums ? 0 : mPhooItem.get(pos - getmNumColums());
        }

        @Override
        public long getItemId(int pos) {
            return pos < getmNumColums() ? 0 : pos - getmNumColums();
        }

        @Override
        public int getViewTypeCount() {
            // Two types of views, the normal ImageView and the top row of empty views
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            return (position < getmNumColums()) ? 1 : 0;
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
                        ViewGroup.LayoutParams.MATCH_PARENT,  ViewGroup.LayoutParams.MATCH_PARENT));

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
           mImageFetcher.loadImage(mPhooItem.get(pos- getmNumColums()).getThumbnailUri()
                   , imageview);

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