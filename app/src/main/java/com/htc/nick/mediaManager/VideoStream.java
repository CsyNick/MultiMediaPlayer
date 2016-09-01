package com.htc.nick.mediaManager;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.annotation.UiThread;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.htc.nick.multimediaplayer.R;

public class VideoStream implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, OnSeekBarChangeListener,
        OnSeekCompleteListener {

  private int STATUS = 0;
  private final int STATUS_STOPED = 1;
  private final int STATUS_PLAYING = 2;
  private final int STATUS_PAUSED = 3;
  private boolean isMediaControllerOpened ;
  private Context ctx;
  private WakeLock wakeLock;
  private MediaPlayer mPlayer;
  private SeekBar seekBar = null;
  private SurfaceView surfaceView;
  private TextView lblCurrentPosition = null;
  private TextView lblDuration = null;
  private TextView videoTitle = null;
  private LinearLayout linearLayoutMediaController = null;
  private Timer timer = null;
  private ImageView play_button;
  private static VideoStream sInstance;
  public VideoStream(Context ctx) {
    this.ctx = ctx;

    mPlayer = new MediaPlayer();
    mPlayer.setOnCompletionListener(this);
    mPlayer.setOnPreparedListener(this);

    PowerManager powerManager = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);
    wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyMediaPlayer");
  }
  public static VideoStream getInstance(Context context) {
    if (sInstance == null) {
      sInstance = new VideoStream(context);
    }
    return sInstance;
  }
  /**
   * Sets up the surface dimensions to display
   * the video on it.
   */
  public void setUpVideoDimensions() {
    // Get the dimensions of the video
    int videoWidth = mPlayer.getVideoWidth();
    int videoHeight = mPlayer.getVideoHeight();
    float videoProportion = (float) videoWidth / (float) videoHeight;

    // Get the width of the screen
    int screenWidth = ((Activity) ctx).getWindowManager().getDefaultDisplay().getWidth();
    int screenHeight = ((Activity) ctx).getWindowManager().getDefaultDisplay().getHeight();
    float screenProportion = (float) screenWidth / (float) screenHeight;

    if (surfaceView != null){

    // Get the SurfaceView layout parameters
    android.view.ViewGroup.LayoutParams lp = surfaceView.getLayoutParams();

    if (videoProportion > screenProportion) {
      lp.width = screenWidth;
      lp.height = (int) ((float) screenWidth / videoProportion);
    } else {
      lp.width = (int) (videoProportion * (float) screenHeight);
      lp.height = screenHeight;
    }

    // Commit the layout parameters
    surfaceView.setLayoutParams(lp);
    }
  }

  /**
   * Pause the video playback.
   */
  public void pause() {
    if (mPlayer.isPlaying()) {
      mPlayer.pause();
      STATUS = STATUS_PAUSED;
      showMediaController();
      wakeLockRelease();
    }
  }

  /**
   * Start the video playback.
   * @throws IOException
   * @throws IllegalStateException
   */
  public void play() throws IllegalStateException, IOException {

    if (STATUS != STATUS_PLAYING) {
      wakeLockAcquire();

      if (STATUS == STATUS_PAUSED )
        mPlayer.start();
      else {
        mPlayer.prepare();
        mPlayer.start();
      }

      STATUS = STATUS_PLAYING;
    }
  }

  /**
   * Sets up the video source.
   * @param source - The video address
   * @throws IllegalArgumentException
   * @throws IllegalStateException
   * @throws IOException
   */
  public void setUpVideoFrom(String source) throws IllegalArgumentException, IllegalStateException, IOException {
    mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

    mPlayer.setDataSource(source);


  }

  /**
   * Release the video object.
   * This will stops the playback and release the memory used.
   */
  public void release() {
    reset();
    if (mPlayer!=null) {
      mPlayer.release();
      mPlayer = null;
    }
  }

  /**
   * Reset the seekbar.
   */
  private void reset() {
    if (seekBar != null && timer != null) {
      seekBar.setProgress(0);
      timer.cancel();
      lblCurrentPosition.setText(ctx.getResources().getString(R.string.empty_message));
    }
  }

  /**
   * Set up the surface to display the video on it.
   * @param holder - The surface to display the video.
   */
  public void setDisplay(SurfaceView surfaceView, SurfaceHolder holder) {
    this.surfaceView = surfaceView;
    mPlayer.setDisplay(holder);
  }

  /**
   * Set up a listener to execute when the video is ready to playback.
   * @param listener - The Listener.
   */
  public void setOnPrepared(MediaPlayer.OnPreparedListener listener){
    mPlayer.setOnPreparedListener(listener);
  }

  /**
   * Sets up a seekbar and two labels to display the video progress.
   * @param seekBar
   * @param lblCurrentPosition
   * @param lblDuration
   */
  public void setSeekBar(SeekBar seekBar, TextView lblCurrentPosition, TextView lblDuration) {
    this.seekBar = seekBar;
    this.lblCurrentPosition = lblCurrentPosition;
    this.lblDuration = lblDuration;

    seekBar.setOnSeekBarChangeListener(this);
    seekBar.setProgress(0);
  }

  /**
   * Stop the video playback.
   */
  public void stop(){
    if (STATUS != STATUS_STOPED) {
      mPlayer.stop();
      STATUS = STATUS_STOPED;
      hideMediaController();
      reset();
      wakeLockRelease();
    }
  }

  /**
   * Get a string with the video's duration.
   * The format of the string is hh:mm:ss
   * @param sec - The seconds to convert.
   * @return A string formated.
   */
  private String getDurationInSeconds(int sec){
    sec = sec / 1000;
    int hours = sec / 3600;
    int minutes = (sec / 60) - (hours * 60);
    int seconds = sec - (hours * 3600) - (minutes * 60) ;
    String formatted = String.format("%d:%02d:%02d", hours, minutes, seconds);

    return formatted;
  }

  /**
   * Set the current position of the video in the seekbar
   * @param progress - The seconds to seek the bar
   */
  private void setCurrentPosition(int progress){
    lblCurrentPosition.setText(getDurationInSeconds(progress));
  }

  /**
   * Acquire wakelock the screen.
   */
  private void wakeLockAcquire() {
    wakeLock.acquire();
  }

  /**
   * Release the wakelock.
   */
  private void wakeLockRelease() {
    if (wakeLock!=null){
      if (wakeLock.isHeld())
      wakeLock.release();
    }

  }

  /**
   * Update the seekbar while the video is playing.
   */
  private void updateMediaProgress() {
    timer = new Timer("progress Updater");
    timer.scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
        ((Activity) ctx).runOnUiThread(new Runnable() {
          public void run() {
            if (mPlayer != null){
              seekBar.setProgress(mPlayer.getCurrentPosition());
              setCurrentPosition(mPlayer.getCurrentPosition());
            }
          }
        });
      }
    }, 0, 1000);
  }

  @Override
  public void onCompletion(MediaPlayer mp) {
    Log.d("Nick-VideoPlayer","onCompletion()");
    if (STATUS >0){
      stop();
    }
  }

  @Override
  public void onPrepared(MediaPlayer mp) {

    if (seekBar != null) {
      mPlayer.setOnSeekCompleteListener(this);

      int duration = (int) mp.getDuration();
      seekBar.setMax(duration);
      lblDuration.setText(getDurationInSeconds(duration));

      updateMediaProgress();
    }

    setUpVideoDimensions();
  }

  @Override
  public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    setCurrentPosition(progress);
  }

  @Override
  public void onStartTrackingTouch(SeekBar seekBar) {

  }

  @Override
  public void onStopTrackingTouch(SeekBar seekBar) {
    mPlayer.seekTo(seekBar.getProgress() );
  }

  @Override
  public void onSeekComplete(MediaPlayer mp) {

  }

  public MediaPlayer getmPlayer() {
    return mPlayer;
  }

  public void  setVideoController(TextView videoTitle, LinearLayout linearLayoutMediaController, ImageView play_button){
            this.videoTitle = videoTitle;
            this.linearLayoutMediaController = linearLayoutMediaController;
            this.play_button = play_button;
  }
  @UiThread
  public void hideMediaController() {
    if (checkMediaControllerValid()){
      videoTitle.setVisibility(View.GONE);
      linearLayoutMediaController.setVisibility(View.GONE);
      isMediaControllerOpened = false;
      if(STATUS == STATUS_STOPED){
        play_button.setImageResource(R.mipmap.play_button);
      } else if (STATUS == STATUS_PLAYING) {
        play_button.setVisibility(View.GONE);
      }

    }
  }
  @UiThread
  public void showMediaController() {
    if (checkMediaControllerValid()) {
      videoTitle.setVisibility(View.VISIBLE);
      isMediaControllerOpened = true;
      if (STATUS == STATUS_PLAYING || STATUS == STATUS_PAUSED) {
        play_button.setVisibility(View.VISIBLE);
      }
      if ( STATUS > 0 ){
        linearLayoutMediaController.setVisibility(View.VISIBLE);
      }
    }
  }
  private boolean checkMediaControllerValid(){
    return videoTitle != null && linearLayoutMediaController !=null;
  }

  public boolean isMediaControllerOpened(){
    return isMediaControllerOpened;
  }

}