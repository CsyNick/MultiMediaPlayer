package com.htc.nick.Page.SlideShowPlayer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.htc.nick.Item.PhotoItem;

import java.util.ArrayList;


public class SlideShow extends FrameLayout
{

  private long mAnimDuration=1000;
  private long mAnimDelay=1000;
  private static int images[];
  private static int imgCounter=-1;
  private static ArrayList<PhotoItem> photoList;
  private boolean mShowFirstView=true;


  ImageView ivFirst;
  ImageView ivSecond;


  public SlideShow(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    addViews();
  }

  public SlideShow(Context context, AttributeSet attrs) {
    super(context, attrs);
    addViews();
  }

  public SlideShow(Context context) {
    super(context);
    addViews();
  }

  public void setImageArray(int[] images) {
    this.images=images;
  }

  public void setPhotoList(ArrayList<PhotoItem> photoList){
    this.photoList = photoList;
  }

  private void setAnimDuration(long animDuration) {
    this.mAnimDuration=animDuration;
  }

  private void setAnimDelay(long animDelay) {
    this.mAnimDelay=animDelay;
  }

  private void addViews() {
    ivFirst=new ImageView(getContext());
    LayoutParams lpflo=new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
    ivFirst.setScaleType(ImageView.ScaleType.CENTER_CROP);
    ivFirst.setLayoutParams(lpflo);

    ivSecond=new ImageView(getContext());
    ivSecond.setScaleType(ImageView.ScaleType.CENTER_CROP);
    ivSecond.setLayoutParams(lpflo);

    addView(ivFirst);
    addView(ivSecond);
  }

  public void start(long animDuration,long animDelay) {

    setAnimDuration(animDuration);
    setAnimDelay(animDelay);
    // Decide which view to hide and which to show.
    final View showView = mShowFirstView ? ivFirst : ivSecond;
    final View hideView = mShowFirstView ? ivSecond : ivFirst;

    // Set the "show" view to 0% opacity but visible, so that it is visible
    // (but fully transparent) during the animation.
    showView.setAlpha(0f);
    showView.setVisibility(View.VISIBLE);

    // Animate the "show" view to 100% opacity, and clear any animation listener set on
    // the view. Remember that listeners are not limited to the specific animation
    // describes in the chained method calls. Listeners are set on the
    // ViewPropertyAnimator object for the view, which persists across several
    // animations.
    showView.animate()
            .alpha(1f)
            .setStartDelay(mAnimDelay)
            .setDuration(mAnimDuration)
            .setListener(null);

    // Animate the "hide" view to 0% opacity. After the animation ends, set its visibility
    // to GONE as an optimization step (it won't participate in layout passes, etc.)
    hideView.animate()
            .alpha(0f)
            .setStartDelay(mAnimDelay)
            .setDuration(mAnimDuration)
            .setListener(new AnimatorListenerAdapter() {
              @Override
              public void onAnimationEnd(Animator animation) {
                hideView.setVisibility(View.GONE);
                restart(hideView);
              }
            });

  }
  private void restart(View hideView)
  {
    //setNextImage from array before fadein
    ((ImageView)hideView).setImageBitmap(getNextBitmap());
    mShowFirstView = !mShowFirstView;
    start(mAnimDuration,mAnimDelay);

  }

  private Bitmap getNextBitmap()
  {
    //get screen width and height
    Display display = ((Activity)getContext()).getWindowManager().getDefaultDisplay();
    Point size = new Point();
    display.getSize(size);
    int width = size.x;
    int height = size.y;
    width=ivFirst.getWidth();
    height=ivFirst.getHeight();
    imgCounter=(imgCounter>=photoList.size()-1)?0:imgCounter+1;

    //get image width and height
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = false;
    options.inSampleSize = 2;
    Bitmap bmpSrc=BitmapFactory.decodeFile(photoList.get(imgCounter).getPath(), options);

    int imageHeight = options.outHeight;
    int imageWidth = options.outWidth;

    //scale bitmap size to imageView size so that we can scale down most of high res images and avoid outofmemoryError
    float screenToPicRatio=(float)imageHeight/(float)height;
    Bitmap scaledBmp=null;
    if(bmpSrc!=null)
      scaledBmp=Bitmap.createScaledBitmap(bmpSrc,(int)(imageWidth/screenToPicRatio),(int)(imageHeight/screenToPicRatio),true);

    return scaledBmp;
  }
}