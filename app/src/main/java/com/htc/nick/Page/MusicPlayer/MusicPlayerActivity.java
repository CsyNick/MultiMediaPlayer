package com.htc.nick.Page.MusicPlayer;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
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
        songManager = new SongManager(this);
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
        if(mp.isPlaying()&& mp!=null){
                mp.pause();
                play.setImageResource(R.drawable.btn_play);
        }else{
                mp.start();
                play.setImageResource(R.drawable.btn_pause);
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
            currentSongIndex = rand.nextInt(songList.size()+ 1);
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
    }
}
