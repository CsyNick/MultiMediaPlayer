package com.htc.nick.Page.MusicPlayer;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.htc.nick.Base.BaseActivity;
import com.htc.nick.media.SongManager;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mp = new MediaPlayer();
        utils = new Utilities();
    }

    @AfterViews
    protected void init(){

        songManager = new SongManager(this);
        //Listeners
        songProgressBar.setOnSeekBarChangeListener(this);
        mp.setOnCompletionListener(this);
        //data
        songList = songManager.getSongList();

        currentSongIndex = songIndex;
        playSong(currentSongIndex);
    }
    @Click
    @Override
    public void play() {
        if(mp.isPlaying()){
            if(mp!=null){
                mp.pause();
                play.setImageResource(R.drawable.btn_play);
            }
        }else{
            // Resume song
            if(mp!=null){
                mp.start();
                play.setImageResource(R.drawable.btn_pause);
            }
        }
    }


    @Override
    public void backward() {

    }

    @Override
    public void forward() {

    }

    @Click
    @Override
    public void next() {
        if(currentSongIndex < (songList.size() - 1)){
            playSong(currentSongIndex + 1);
            currentSongIndex = currentSongIndex + 1;
        }else {
            playSong(0);
            currentSongIndex = 0;
        }
    }

    @Click
    @Override
    public void previous() {
        if(currentSongIndex > 0){
            playSong(currentSongIndex - 1);
            currentSongIndex = currentSongIndex -1;
        }else {
            playSong(songList.size() - 1);
            currentSongIndex = songList.size() - 1;
        }
    }

    @Click
    @Override
    public void repeat() {
        if(isRepeat){
            isRepeat = false;
            Toast.makeText(getApplicationContext(), "Repeat is OFF", Toast.LENGTH_SHORT).show();
            repeat.setImageResource(R.drawable.btn_repeat);
        }else{
            // make repeat to true
            isRepeat = true;
            Toast.makeText(getApplicationContext(), "Repeat is ON", Toast.LENGTH_SHORT).show();
            // make shuffle to false
            isShuffle = false;
            repeat.setImageResource(R.drawable.btn_repeat_focused);
            shuffle.setImageResource(R.drawable.btn_shuffle);
        }
    }

    @Click
    @Override
    public void shuffle() {
        if(isShuffle){
            isShuffle = false;
            Toast.makeText(getApplicationContext(), "Shuffle is OFF", Toast.LENGTH_SHORT).show();
            shuffle.setImageResource(R.drawable.btn_shuffle);
        }else{
            // make repeat to true
            isShuffle= true;
            Toast.makeText(getApplicationContext(), "Shuffle is ON", Toast.LENGTH_SHORT).show();
            // make shuffle to false
            isRepeat = false;
            shuffle.setImageResource(R.drawable.btn_shuffle_focused);
            repeat.setImageResource(R.drawable.btn_repeat);
        }
    }

    @Override
    public void playSong(int index) {

            // Play song
            try {
                mp.reset();
                mp.setDataSource(songManager.getSongList().get(index).get("songPath"));
                mp.prepare();
                mp.start();

                String title = songList.get(index).get("songTitle");
                songTitle.setText(title);

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

    /**
     * Update timer on seekbar
     * */
    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    /**
     * Background Runnable thread
     * */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = mp.getDuration();
            long currentDuration = mp.getCurrentPosition();

            // Displaying Total Duration time
            songTotalDurationLabel.setText(""+utils.milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            songCurrentDurationLabel.setText(""+utils.milliSecondsToTimer(currentDuration));

            // Updating progress bar
            int progress = utils.getProgressPercentage(currentDuration, totalDuration);
            //Log.d("Progress", ""+progress);
            songProgressBar.setProgress(progress);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };
    @Override
    protected MusicPlayerPresenter createPresenter() {
        return MusicPlayerPresenter_.getInstance_(this);
    }


    @Override
    public void onCompletion(MediaPlayer mp) {
        // check for repeat is ON or OFF
        if(isRepeat){
            // repeat is on play same song again
            playSong(currentSongIndex);
        } else if(isShuffle){
            // shuffle is on - play a random song
            Random rand = new Random();
            currentSongIndex = rand.nextInt((songList.size() ) - 0 + 1);
            playSong(currentSongIndex);
        } else{
            // no repeat or shuffle ON - play next song
            if(currentSongIndex < (songList.size() - 1)){
                playSong(currentSongIndex + 1);
                currentSongIndex = currentSongIndex + 1;
            }else{
                // play first song
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
        int totalDuration = mp.getDuration();
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        mp.seekTo(currentPosition);

        // update timer progress again
        updateProgressBar();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mHandler.removeCallbacks(mUpdateTimeTask);
        mp.release();
    }
}
