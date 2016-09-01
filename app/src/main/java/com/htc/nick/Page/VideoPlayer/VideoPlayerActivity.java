package com.htc.nick.Page.VideoPlayer;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

    private static final String TAG = "Nick-VideoPlayer";
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


    private boolean mAudioFocusGranted = false;
    private boolean mAudioIsPlaying = false;;
    private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener;
    private BroadcastReceiver mIntentReceiver;
    private boolean mReceiverRegistered = false;

    private static final String CMD_NAME = "command";
    private static final String CMD_PAUSE = "pause";
    private static final String CMD_STOP = "pause";
    private static final String CMD_PLAY = "play";

    // Jellybean
    private static String SERVICE_CMD = "com.sec.android.app.music.musicservicecommand";
    private static String PAUSE_SERVICE_CMD = "com.sec.android.app.music.musicservicecommand.pause";
    private static String PLAY_SERVICE_CMD = "com.sec.android.app.music.musicservicecommand.play";

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        extras = getIntent().getExtras();


        runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                              mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {

                                  @Override
                                  public void onAudioFocusChange(int focusChange) {
                                      switch (focusChange) {
                                          case AudioManager.AUDIOFOCUS_GAIN:
                                              Log.i(TAG, "AUDIOFOCUS_GAIN");
                                              play();
                                              break;
                                          case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
                                              Log.i(TAG, "AUDIOFOCUS_GAIN_TRANSIENT");
                                              break;
                                          case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK:
                                              Log.i(TAG, "AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK");
                                              break;
                                          case AudioManager.AUDIOFOCUS_LOSS:
                                              Log.e(TAG, "AUDIOFOCUS_LOSS");
                                              pause();
                                              break;
                                          case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                                              Log.e(TAG, "AUDIOFOCUS_LOSS_TRANSIENT");
                                              pause();
                                              break;
                                          case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                                              Log.e(TAG, "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                                              break;
                                          case AudioManager.AUDIOFOCUS_REQUEST_FAILED:
                                              Log.e(TAG, "AUDIOFOCUS_REQUEST_FAILED");
                                              break;
                                          default:
                                              //
                                      }
                                  }
                              };
                          }
                      }
        );
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

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy()");
            player.release();
        abandonAudioFocus();

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
                // 1. Acquire audio focus
                if (!mAudioFocusGranted && requestAudioFocus()) {
                    // 2. Kill off any other play back sources
                    forceMusicStop();
                    // 3. Register broadcast receiver for player intents
                    setupBroadcastReceiver();
                }
                player.setDisplay(surface, holder);
                player.play();
                play.setImageResource(R.mipmap.pause);
                player.showMediaController();
            } else if (player.getmPlayer().isPlaying()){
                pause();
            }

        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void pause(){
        play.setImageResource(R.mipmap.play_button);
        player.pause();
    }
    @Click
    public void surface(){
        Log.d(TAG,"Surface is clicked");
        if (player.isMediaControllerOpened()){
            player.hideMediaController();
        }else {
            player.showMediaController();
        }

    }


        @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"onPause()");
            player.pause();
            play.setImageResource(R.mipmap.play_button);
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Log.d(TAG,"onConfigurationChanged()");
            // Checks the orientation of the screen
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
                // for example the width of a layout
                player.setUpVideoDimensions();
            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
                player.setUpVideoDimensions();
            }

    }

    private boolean requestAudioFocus() {
        if (!mAudioFocusGranted) {
            AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
            // Request audio focus for play back
            int result = am.requestAudioFocus(mOnAudioFocusChangeListener,
                    // Use the music stream.
                    AudioManager.STREAM_MUSIC,
                    // Request permanent focus.
                    AudioManager.AUDIOFOCUS_GAIN);

            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                mAudioFocusGranted = true;
            } else {
                // FAILED
                Log.e(TAG,
                        ">>>>>>>>>>>>> FAILED TO GET AUDIO FOCUS <<<<<<<<<<<<<<<<<<<<<<<<");
            }
        }
        return mAudioFocusGranted;
    }

    private void abandonAudioFocus() {
        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        int result = am.abandonAudioFocus(mOnAudioFocusChangeListener);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mAudioFocusGranted = false;
        } else {
            // FAILED
            Log.e(TAG,
                    ">>>>>>>>>>>>> FAILED TO ABANDON AUDIO FOCUS <<<<<<<<<<<<<<<<<<<<<<<<");
        }
        mOnAudioFocusChangeListener = null;
    }

    private void setupBroadcastReceiver() {
        mIntentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                String cmd = intent.getStringExtra(CMD_NAME);
                Log.i(TAG, "mIntentReceiver.onReceive " + action + " / " + cmd);

                if (PAUSE_SERVICE_CMD.equals(action)
                        || (SERVICE_CMD.equals(action) && CMD_PAUSE.equals(cmd))) {
                    play();
                }

                if (PLAY_SERVICE_CMD.equals(action)
                        || (SERVICE_CMD.equals(action) && CMD_PLAY.equals(cmd))) {
                    pause();
                }
            }
        };

        // Do the right thing when something else tries to play
        if (!mReceiverRegistered) {
            IntentFilter commandFilter = new IntentFilter();
            commandFilter.addAction(SERVICE_CMD);
            commandFilter.addAction(PAUSE_SERVICE_CMD);
            commandFilter.addAction(PLAY_SERVICE_CMD);
            registerReceiver(mIntentReceiver, commandFilter);
            mReceiverRegistered = true;
        }
    }

    private void forceMusicStop() {
        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        if (am.isMusicActive()) {
            Intent intentToStop = new Intent(SERVICE_CMD);
            intentToStop.putExtra(CMD_NAME, CMD_STOP);
            sendBroadcast(intentToStop);
        }
    }
}