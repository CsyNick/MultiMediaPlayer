package com.htc.nick.Page.VideoPlayer;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.htc.nick.multimediaplayer.R;
import com.htc.nick.util.Utilities;
import com.htc.nick.util.Utils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_video_player)
public class VideoPlayerActivity extends Activity implements
        OnBufferingUpdateListener, OnCompletionListener,
        OnPreparedListener, OnVideoSizeChangedListener, SurfaceHolder.Callback,
        SeekBar.OnSeekBarChangeListener,MediaPlayer.OnSeekCompleteListener,View.OnClickListener {

    private static final String TAG = "VideoPlayer";
    private MediaPlayer player;

    private SurfaceHolder holder;

    private Bundle extras;

    private Timer updateTimer;

    @ViewById
    protected TextView textViewPlayed;
    @ViewById
    protected TextView textViewLength;
    @ViewById
    protected SeekBar seekBarProgress;
    @ViewById
    protected ImageView imageViewPauseIndicator;
    @ViewById
    protected ProgressBar progressBarWait;
    @ViewById
    protected LinearLayout linearLayoutMediaController;
    @ViewById
    protected SurfaceView surface;
    @ViewById
    protected TextView videoTitle;

    private static final String URL = "url";
    private static final String TITLE = "title";


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        extras = getIntent().getExtras();
        player = new MediaPlayer();
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnBufferingUpdateListener(this);
        player.setOnSeekCompleteListener(this);
        player.setScreenOnWhilePlaying(true);
        player.setOnVideoSizeChangedListener(this);


    }

    @AfterViews
    protected void init() {
        holder = surface.getHolder();
        holder.addCallback(this);
        surface.setOnClickListener(this);
        surface.setClickable(false);
        linearLayoutMediaController.setVisibility(View.GONE);
        imageViewPauseIndicator.setVisibility(View.GONE);
        if (player != null) {
            if (!player.isPlaying()) {
                imageViewPauseIndicator.setVisibility(View.VISIBLE);
            }
        }

        textViewPlayed = (TextView) findViewById(R.id.textViewPlayed);
        textViewLength = (TextView) findViewById(R.id.textViewLength);


        seekBarProgress.setOnSeekBarChangeListener(this);
        seekBarProgress.setProgress(0);

        progressBarWait = (ProgressBar) findViewById(R.id.progressBarWait);
        videoTitle.setText(extras.getString(TITLE));
    }

    private void playVideo() {
        if (extras.getString(URL).equals("VIDEO_URI")) {
            showToast("Please, set the video URI in HelloAndroidActivity.java in onClick(View v) method");
        } else {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        seekBarProgress.setProgress(0);
                        player.setDataSource(extras.getString(URL));
                        player.setDisplay(holder);
                        player.prepare();
                    } catch (IllegalArgumentException e) {
                        showToast("Error while playing video");
                        Log.i(TAG, "========== IllegalArgumentException ===========");
                        e.printStackTrace();
                    } catch (IllegalStateException e) {
                        showToast("Error while playing video");
                        Log.i(TAG, "========== IllegalStateException ===========");
                        e.printStackTrace();
                    } catch (IOException e) {
                        showToast("Error while playing video. Please, check your network connection.");
                        Log.i(TAG, "========== IOException ===========");
                        e.printStackTrace();
                    }
                }
            }).start();
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

    private void hideMediaController() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(5000);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            videoTitle.setVisibility(View.GONE);
                            imageViewPauseIndicator.setVisibility(View.GONE);
                            linearLayoutMediaController.setVisibility(View.GONE);
                        }
                    });
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Log.i(TAG, "========== onProgressChanged : " + progress + " from user: " + fromUser);
        if (!fromUser) {
            textViewPlayed.setText(Utils.durationInSecondsToString(progress));
        }
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        // if (player.isPlaying()) {
        progressBarWait.setVisibility(View.VISIBLE);
        player.seekTo(seekBar.getProgress() * 1000);
        Log.i(TAG, "========== SeekTo : " + seekBar.getProgress());
        // }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // TODO Auto-generated method stub

    }

    public void surfaceCreated(SurfaceHolder holder) {
        playVideo();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
    }

    public void onPrepared(MediaPlayer mp) {
        Log.i(TAG, "========== onPrepared ===========");
        int duration = mp.getDuration() / 1000; // duration in seconds
        seekBarProgress.setMax(duration);
        textViewLength.setText(Utils.durationInSecondsToString(duration));
        progressBarWait.setVisibility(View.GONE);

        // Get the dimensions of the video
        int videoWidth = player.getVideoWidth();
        int videoHeight = player.getVideoHeight();
        float videoProportion = (float) videoWidth / (float) videoHeight;
        Log.i(TAG, "VIDEO SIZES: W: " + videoWidth + " H: " + videoHeight + " PROP: " + videoProportion);

        // Get the width of the screen
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        float screenProportion = (float) screenWidth / (float) screenHeight;
        Log.i(TAG, "VIDEO SIZES: W: " + screenWidth + " H: " + screenHeight + " PROP: " + screenProportion);

        // Get the SurfaceView layout parameters
        android.view.ViewGroup.LayoutParams lp = surface.getLayoutParams();

        if (videoProportion > screenProportion) {
            lp.width = screenWidth;
            lp.height = (int) ((float) screenWidth / videoProportion);
        } else {
            lp.width = (int) (videoProportion * (float) screenHeight);
            lp.height = screenHeight;
        }

        // Commit the layout parameters
        surface.setLayoutParams(lp);

        // Start video
        if (!player.isPlaying()) {

            player.start();
            updateMediaProgress();
            linearLayoutMediaController.setVisibility(View.VISIBLE);
            hideMediaController();
        }
        surface.setClickable(true);
    }

    public void onCompletion(MediaPlayer mp) {
        mp.stop();
        if (updateTimer != null) {
            updateTimer.cancel();
        }
//        finish();
    }

    /**
     * Change progress of mediaController
     */
    private void updateMediaProgress() {
        updateTimer = new Timer("progress Updater");
        updateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        if(player!=null) {
                            seekBarProgress.setProgress(player.getCurrentPosition() / 1000);
                        }
                    }
                });
            }
        }, 0, 1000);
    }

    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        int progress = (int) ((float) mp.getDuration() * ((float) percent / (float) 100));
        seekBarProgress.setSecondaryProgress(progress / 1000);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.surface) {
            if (linearLayoutMediaController.getVisibility() == View.GONE) {
                linearLayoutMediaController.setVisibility(View.VISIBLE);
                imageViewPauseIndicator.setVisibility(View.VISIBLE);
                videoTitle.setVisibility(View.VISIBLE);
                hideMediaController();
            } else if (player != null) {
                if (player.isPlaying()) {
                    player.pause();
                    videoTitle.setVisibility(View.VISIBLE);
                    imageViewPauseIndicator.setVisibility(View.VISIBLE);
                } else {
                    player.start();
                    imageViewPauseIndicator.setVisibility(View.GONE);
                    videoTitle.setVisibility(View.GONE);
                }
            }
        }
    }

    public void onSeekComplete(MediaPlayer mp) {
        progressBarWait.setVisibility(View.GONE);
    }


    @Override
    public void onVideoSizeChanged(MediaPlayer mediaPlayer, int width, int height) {
        Log.d(TAG,"onVideoSizeChanged called");
        if(width == 0 || height ==0){
            Log.e(TAG,"invalid video width("+width+")"+"or height("+height+")");
        }

    }

    private void releaseMediaPlayer() {
        if (player != null) {
            player.release();
            player = null;
            seekBarProgress.setProgress(0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (updateTimer != null) {
            updateTimer.cancel();
        }
        releaseMediaPlayer();
    }


}