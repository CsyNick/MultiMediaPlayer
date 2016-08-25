package com.htc.nick.Page.VideoPlayer;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.htc.nick.mediaManager.VideoStream;
import com.htc.nick.multimediaplayer.R;
import com.htc.nick.util.Utilities;
import com.htc.nick.util.Utils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_video_player)
public class VideoPlayerActivity extends Activity implements SurfaceHolder.Callback {

    private static final String TAG = "VideoPlayer";
//    private MediaPlayer player;

    private SurfaceHolder holder;

    private Bundle extras;

    @ViewById
    protected TextView textViewPlayed;
    @ViewById
    protected TextView textViewLength;
    @ViewById
    protected SeekBar seekBarProgress;

    @ViewById
    protected LinearLayout linearLayoutMediaController;
    @ViewById
    protected SurfaceView surface;
    @ViewById
    protected TextView videoTitle;

    @ViewById
    protected ImageView play;

    @ViewById
    protected ImageView VideoThumbNails;
    private static final String URL = "url";
    private static final String TITLE = "title";
    private static final String THUMBNAILS = "thumbnails";
    private VideoStream player;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        extras = getIntent().getExtras();
    }

    @AfterViews
    protected void init() {
        holder = surface.getHolder();
        holder.addCallback(this);
        Glide.with(this)
                .load(extras.getString(THUMBNAILS))
                .thumbnail(0.1f)
                .into(VideoThumbNails);
        textViewPlayed = (TextView) findViewById(R.id.textViewPlayed);
        textViewLength = (TextView) findViewById(R.id.textViewLength);

        videoTitle.setText(extras.getString(TITLE));

        try {
            player = new VideoStream(this);
            Log.d(TAG,extras.getString(URL));
            player.setVideoController(videoTitle,linearLayoutMediaController,play);
            player.setSeekBar(seekBarProgress,textViewPlayed,textViewLength);
            player.setUpVideoFrom(extras.getString(URL));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showToast(final String string) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(VideoPlayerActivity.this, string, Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
            player.release();

    }



    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

        Log.d(TAG,"surfaceCreated");
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
    }

    @Click
    public void play(){
        try {
            Log.d(TAG,"Play");
            VideoThumbNails.setVisibility(View.GONE);
            if(!player.getmPlayer().isPlaying()){
                player.setDisplay(surface, holder);
                player.play();
                play.setImageResource(R.mipmap.pause);
                player.showMediaController();
            } else if (player.getmPlayer().isPlaying()){
                play.setImageResource(R.mipmap.play_button);
                player.pause();
            }

        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Click
    public void surface(){
        Log.d(TAG,"Surface is clicked");


    }


        @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"onPause()");
        player.pause();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
            // for example the width of a layout
            player.setUpVideoDimensions();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
            player.setUpVideoDimensions();
        }
    }

}