package com.htc.nick.Page.MusicPlayer;

/**
 * Created by nick on 7/29/16.
 */
public interface MusicPlayerView {
    void play();
    void backward();
    void forward();
    void next();
    void previous();
    void repeat();
    void shuffle();
    void playSong(int index);

}
