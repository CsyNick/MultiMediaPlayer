package com.htc.nick.Page.PhotoViewer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.htc.nick.Item.PhotoItem;
import com.htc.nick.mediaManager.PhotoManager;
import com.htc.nick.multimediaplayer.R;

import junit.framework.Assert;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class GalleryActivity extends AppCompatActivity {
    public static final String TAG = "GalleryActivity";
    public static final String EXTRA_NAME = "images";
    public static final String IMAGES_PATH = "images_path";

    private GalleryPagerAdapter _adapter;
    private ArrayList<PhotoItem> _images;
    private ArrayList<String> _imagesp_path;
    @InjectView(R.id.pager)
    ViewPager _pager;
    @InjectView(R.id.btn_close)
    ImageButton _closeButton;

    @InjectView(R.id.moreInfo)
    ImageView moreInfo;

    private boolean isOpenSystemUI = false;
    View decorView;

    private PhotoManager photoManager = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        ButterKnife.inject(this);
         decorView = getWindow().getDecorView();
        int position =  getIntent().getIntExtra("position", 0);
         Log.d(TAG,position+"..");
        _imagesp_path =  getIntent().getStringArrayListExtra(IMAGES_PATH);
        Assert.assertNotNull(_imagesp_path);
        hideSystemUI();
        _adapter = new GalleryPagerAdapter(this);
        _pager.setAdapter(_adapter);
        _pager.setOffscreenPageLimit(6); // how many images to load into memory
        _pager.setCurrentItem(position);
        _closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Close clicked");
                finish();
            }
        });
        _pager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "_touch_frameLayout clicked");
                if (!isOpenSystemUI){
                    showSystemUI();
                    isOpenSystemUI=true;
                }else {
                    hideSystemUI();
                    isOpenSystemUI=false;
                }
            }
        });

        photoManager = PhotoManager.getInstance(this);
        HandlerThread thread = new HandlerThread("myThread");
        thread.start(); // thread 要start後，才能取得looper
        Handler handler = new Handler(thread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                // 耗時的工作可以在這裡做
                _images =  photoManager.getPhotoList();
            }
        };
        handler.sendEmptyMessage(0); // handler發送的訊息，會觸發handler.handleMessage()。

    }

    class GalleryPagerAdapter extends PagerAdapter {

        Context _context;
        LayoutInflater _inflater;

        public GalleryPagerAdapter(Context context) {
            _context = context;
            _inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return _imagesp_path.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View itemView = _inflater.inflate(R.layout.pager_gallery_item, container, false);
            container.addView(itemView);

            final SubsamplingScaleImageView imageView =
                    (SubsamplingScaleImageView) itemView.findViewById(R.id.image);
            moreInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new MaterialDialog.Builder(GalleryActivity.this).title("File Name: "+_images.get(position).getName())
                            .content("Resolution: "+_images.get(position).getResolution()+"\n" +
                                    "Size: "+_images.get(position).getSize()+"mb"+"\n"+ "Date: "+_images.get(position).getDate()).show();
                }
            });
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "_touch_frameLayout clicked");
                    if (!isOpenSystemUI){
                        showSystemUI();
                        isOpenSystemUI=true;
                    }else {
                        hideSystemUI();
                        isOpenSystemUI=false;
                    }
                }
            });
            Glide.with(_context)
                    .load(_imagesp_path.get(position))
                    .asBitmap()
                    .override(720, 240)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                            imageView.setImage(ImageSource.bitmap(bitmap));
                        }
                    });

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }

    // This snippet hides the system bars.
    private void hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
        moreInfo.setVisibility(View.GONE);
    }

    // This snippet shows the system bars. It does this by removing all the flags
// except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        moreInfo.setVisibility(View.VISIBLE);
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @Override
    protected void onResume() {
        super.onResume();


    }
}
