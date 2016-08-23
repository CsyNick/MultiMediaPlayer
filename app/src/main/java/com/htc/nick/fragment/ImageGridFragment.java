package com.htc.nick.fragment;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;

import com.htc.nick.Adapter.ImageAdapter;

/**
 * Created by nickchung on 2016/8/18.
 */
public class ImageGridFragment extends Fragment implements AdapterView.OnItemClickListener {

    private static final String TAG = "ImageGridFragment";
    private static final String IMAGE_CACHE_DIR = "thumbs";

    private int mImageThumbSize;
    private int mImageThumbSpacing;
    private ImageAdapter imageAdapter;

    public ImageGridFragment() {
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}
