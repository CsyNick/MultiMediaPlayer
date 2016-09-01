package com.htc.nick.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

/**
 * Created by nickchung on 2016/9/1.
 */
public class AudioFocusUtils {

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

    private Context _context;
    public AudioFocusUtils(Context context){
        this._context = context;
    }

}
