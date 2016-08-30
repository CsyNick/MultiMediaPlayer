/*
  Copyright 2013 Google Inc. All Rights Reserved.
  
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
  
      http://www.apache.org/licenses/LICENSE-2.0
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */
package com.htc.nick.mediaManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import com.htc.nick.multimediaplayer.R;

/**
 * This music player class illustrates some best practices with respect to audio
 * focus and audio play back.
 */
public class MusicPlayer {

	private static final String TAG = "BEST";
	private static MusicPlayer sInstance;

	private static final String CMD_NAME = "command";
	private static final String CMD_PAUSE = "pause";
	private static final String CMD_STOP = "pause";
	private static final String CMD_PLAY = "play";

	// Jellybean
        private static String SERVICE_CMD = "com.sec.android.app.music.musicservicecommand";
	private static String PAUSE_SERVICE_CMD = "com.sec.android.app.music.musicservicecommand.pause";
	private static String PLAY_SERVICE_CMD = "com.sec.android.app.music.musicservicecommand.play";
	
	// Honeycomb
	{
		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
			SERVICE_CMD = "com.android.music.musicservicecommand";
	                PAUSE_SERVICE_CMD = "com.android.music.musicservicecommand.pause";
     	                PLAY_SERVICE_CMD = "com.android.music.musicservicecommand.play";
		}
	};

	public static MusicPlayer getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new MusicPlayer(context);
		}
		return sInstance;
	}

	private Context mContext;
	private boolean mAudioFocusGranted = false;
	private boolean mAudioIsPlaying = false;
	private MediaPlayer mPlayer;
	private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener;
	private BroadcastReceiver mIntentReceiver;
	private boolean mReceiverRegistered = false;

	private MusicPlayer(Context context) {
		mContext = context;

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

	public void play() {
		if (!mAudioIsPlaying) {
			if (mPlayer == null) {
				//mPlayer = MediaPlayer.create(mContext, R.raw.krispy_mixdown);
				mPlayer.setLooping(true);
			}

			// 1. Acquire audio focus
			if (!mAudioFocusGranted && requestAudioFocus()) {
				// 2. Kill off any other play back sources
				forceMusicStop();
				// 3. Register broadcast receiver for player intents
				setupBroadcastReceiver();
			}
			// 4. Play music
			mPlayer.start();
			mAudioIsPlaying = true;
		}
	}

	public void pause() {
		// 1. Suspend play back
		if (mAudioFocusGranted && mAudioIsPlaying) {
			mPlayer.pause();
			mAudioIsPlaying = false;
		}
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

	private boolean requestAudioFocus() {
		if (!mAudioFocusGranted) {
			AudioManager am = (AudioManager) mContext
					.getSystemService(Context.AUDIO_SERVICE);
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
		AudioManager am = (AudioManager) mContext
				.getSystemService(Context.AUDIO_SERVICE);
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
			mContext.registerReceiver(mIntentReceiver, commandFilter);
			mReceiverRegistered = true;
		}
	}

	private void forceMusicStop() {
		AudioManager am = (AudioManager) mContext
				.getSystemService(Context.AUDIO_SERVICE);
		if (am.isMusicActive()) {
			Intent intentToStop = new Intent(SERVICE_CMD);
			intentToStop.putExtra(CMD_NAME, CMD_STOP);
			mContext.sendBroadcast(intentToStop);
		}
	}
}