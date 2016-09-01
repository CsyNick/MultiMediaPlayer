package com.htc.nick.Page.MusicPlayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.htc.nick.Base.BaseActivity;
import com.htc.nick.mediaManager.SongManager;
import com.htc.nick.multimediaplayer.R;
import com.htc.nick.util.Utilities;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by nick on 7/29/16.
 */
@EActivity(R.layout.activity_music_player)
public class MusicPlayerActivity extends BaseActivity<MusicPlayerView, MusicPlayerPresenter> implements MusicPlayerView ,
        MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "MusicPlayerActivity";
    private MediaPlayer mp;
    private SongManager songManager;
    private Handler mHandler = new Handler();
    private Utilities utils;
    private ArrayList<HashMap<String, String>> songList;
    private int currentSongIndex;
    @Extra
    protected int songIndex;

    private boolean isShuffle = false;
    private boolean isRepeat = false;

    @ViewById
    protected ImageButton play;
    @ViewById
    protected ImageButton next;
    @ViewById
    protected ImageButton previous;
    @ViewById
    protected ImageButton repeat;
    @ViewById
    protected ImageButton shuffle;
    @ViewById
    protected TextView songTitle;
    @ViewById
    protected SeekBar songProgressBar;
    @ViewById
    protected TextView songTotalDurationLabel;
    @ViewById
    protected TextView songCurrentDurationLabel;


    private boolean mAudioFocusGranted = false;
    private boolean mAudioIsPlaying = false;
    private MediaPlayer mPlayer;
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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mp = new MediaPlayer();
        utils = new Utilities();
        songManager = new SongManager(this);


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
    protected void init(){

        songProgressBar.setOnSeekBarChangeListener(this);
        mp.setOnCompletionListener(this);

        songList = songManager.getSongList();

        currentSongIndex = songIndex;
        playSong(currentSongIndex);
    }
    @Click
    @Override
    public void play() {

            if (mp.isPlaying() && mp != null) {
                pause();
            } else {

                // 1. Acquire audio focus
                if (!mAudioFocusGranted && requestAudioFocus()) {
                    // 2. Kill off any other play back sources
                    forceMusicStop();
                    // 3. Register broadcast receiver for player intents
                    setupBroadcastReceiver();
                }

                mp.start();
                mAudioIsPlaying = true;
                play.setImageResource(R.drawable.btn_pause);
            }



    }

    private void pause(){

            mp.pause();
            play.setImageResource(R.drawable.btn_play);


    }

    public void stop() {
        // 1. Stop play back
        if (mAudioFocusGranted && mAudioIsPlaying) {
            mPlayer.stop();
            mPlayer = null;
            mAudioIsPlaying = false;
            // 2. Give up audio focus
            abandonAudioFocus();
        }
    }


    @Override
    public void backward() {
     // TODO: 2016/8/1 backward
    }

    @Override
    public void forward() {
        // TODO: 2016/8/1 forward
    }

    @Click
    @Override
    public void next() {
        if(isShuffle){
            Random rand = new Random();
            currentSongIndex = rand.nextInt((songList.size() - 1) - 0 + 1) + 0;
            playSong(currentSongIndex);
        }else {
            if (currentSongIndex < (songList.size() - 1)) {
                playSong(currentSongIndex + 1);
                currentSongIndex = currentSongIndex + 1;
            } else {
                playSong(0);
                currentSongIndex = 0;
            }
        }
    }

    @Click
    @Override
    public void previous() {
        if(isShuffle){
            Random rand = new Random();
            currentSongIndex = rand.nextInt((songList.size() - 1) - 0 + 1) + 0;
            playSong(currentSongIndex);
        }else {
            if (currentSongIndex > 0) {
                playSong(currentSongIndex - 1);
                currentSongIndex = currentSongIndex - 1;
            } else {
                playSong(songList.size() - 1);
                currentSongIndex = songList.size() - 1;
            }
        }
    }

    @Click
    @Override
    public void repeat() {
        if(isRepeat){
            isRepeat = false;
            repeat.setImageResource(R.drawable.btn_repeat);
            Toast.makeText(getApplicationContext(), "Repeat OFF", Toast.LENGTH_SHORT).show();
        }else{
            isRepeat = true;
            isShuffle = false;
            repeat.setImageResource(R.drawable.btn_repeat_focused);
            shuffle.setImageResource(R.drawable.btn_shuffle);
            Toast.makeText(getApplicationContext(), "Repeat ON", Toast.LENGTH_SHORT).show();
        }
    }

    @Click
    @Override
    public void shuffle() {
        if(isShuffle){
            isShuffle = false;
            shuffle.setImageResource(R.drawable.btn_shuffle);
            Toast.makeText(getApplicationContext(), "Shuffle OFF", Toast.LENGTH_SHORT).show();
        }else{
            isShuffle= true;
            isRepeat = false;
            shuffle.setImageResource(R.drawable.btn_shuffle_focused);
            repeat.setImageResource(R.drawable.btn_repeat);
            Toast.makeText(getApplicationContext(), "Shuffle ON", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void playSong(int index) {

            try {
                // 1. Acquire audio focus
                if (!mAudioFocusGranted && requestAudioFocus()) {
                    // 2. Kill off any other play back sources
                    forceMusicStop();
                    // 3. Register broadcast receiver for player intents
                    setupBroadcastReceiver();
                }

                mp.reset();
                mp.setDataSource(songManager.getSongList().get(index).get("songPath"));
                mp.prepare();
                mp.start();

                songTitle.setText(songList.get(index).get("songTitle"));

                play.setImageResource(R.drawable.btn_pause);

                songProgressBar.setProgress(0);
                songProgressBar.setMax(100);

                updateProgressBar();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


    }

    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = mp.getDuration();
            long currentDuration = mp.getCurrentPosition();

            songTotalDurationLabel.setText(""+utils.milliSecondsToTimer(totalDuration));

            songCurrentDurationLabel.setText(""+utils.milliSecondsToTimer(currentDuration));

            int progress = utils.getProgressPercentage(currentDuration, totalDuration);

            songProgressBar.setProgress(progress);

            mHandler.postDelayed(this, 100);
        }
    };
    @Override
    protected MusicPlayerPresenter createPresenter() {
        return MusicPlayerPresenter_.getInstance_(this);
    }


    @Override
    public void onCompletion(MediaPlayer mp) {  // song finished
        if(isRepeat){
            playSong(currentSongIndex);
        } else if(isShuffle){
            Random rand = new Random();
            currentSongIndex = rand.nextInt((songList.size() - 1) - 0 + 1) + 0;
            Log.d("song-currentSongIndex",currentSongIndex+"");
            playSong(currentSongIndex);
        } else{
            if(currentSongIndex < (songList.size() - 1)){
                playSong(currentSongIndex + 1);
                currentSongIndex = currentSongIndex + 1;
            }else{
                playSong(0);
                currentSongIndex = 0;
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), mp.getDuration());
        mp.seekTo(currentPosition);
        updateProgressBar();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mHandler.removeCallbacks(mUpdateTimeTask);
        mp.release();
        abandonAudioFocus();

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
