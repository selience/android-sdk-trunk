package com.iresearch.android.audio;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

/***
 * A simple audio player wrapper for Android
 ***/
public class AudioWife {

	private static final String TAG = AudioWife.class.getSimpleName();

	/***
	 * Keep a single copy of this in memory
	 ****/
	private static AudioWife mAudioWife;

	/****
	 * Playback progress update time in milliseconds
	 ****/
	private static final int AUDIO_PROGRESS_UPDATE_TIME = 100;

	private Handler mHandler;

	private MediaPlayer mMediaPlayer;

	private SeekBar mSeekBar;
	private TextView mPlaybackTime;
	private View mPlayButton;
	private View mPauseButton;

	/****
	 * Array to hold custom completion listeners
	 ****/
	private ArrayList<OnCompletionListener> mCompletionListeners = new ArrayList<MediaPlayer.OnCompletionListener>();
	
	private ArrayList<View.OnClickListener> mPlayListeners = new ArrayList<View.OnClickListener>();
	
	private ArrayList<View.OnClickListener> mPauseListeners = new ArrayList<View.OnClickListener>();

	/***
	 * Audio URI
	 ****/
	private static Uri mUri;

	public static AudioWife getInstance() {

		if (mAudioWife == null)
			mAudioWife = new AudioWife();

		return mAudioWife;
	}

	private Runnable mUpdateProgress = new Runnable() {
		public void run() {

			if (mHandler != null && mMediaPlayer.isPlaying()) {
				mSeekBar.setProgress((int) mMediaPlayer.getCurrentPosition());
				updatePlaytime(mMediaPlayer.getCurrentPosition());
				// repeat the process
				mHandler.postDelayed(this, AUDIO_PROGRESS_UPDATE_TIME);
			} else {
				// DO NOT update UI if the player is paused
			}
		}
	};

	/***
	 * Start playing the audio. Calling this method, if the audio is already
	 * playing, has no effect.
	 ****/
	public void play() {

		if (mUri == null)
			throw new IllegalStateException(
					"Uri cannot be null. Call init() before calling play()");

		if (mMediaPlayer == null)
			throw new IllegalStateException("Call init() before calling play()");

		if (mMediaPlayer.isPlaying())
			return;

		mHandler.postDelayed(mUpdateProgress, AUDIO_PROGRESS_UPDATE_TIME);

		mMediaPlayer.start();

		setPausable();
	}

	/***
	 * Pause the audio being played. Calling this method has no effect if the
	 * audio is already paused
	 */
	public void pause() {

		if(mMediaPlayer == null)
			return;
		
		if (mMediaPlayer.isPlaying()) {
			mMediaPlayer.pause();
			setPlayable();
		}
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private void updatePlaytime(int currentTime) {
		long totalDuration = 0;

		if (mMediaPlayer != null) {
			try {
				totalDuration = mMediaPlayer.getDuration();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		StringBuilder playbackStr = new StringBuilder();

		// set the current time
		// its ok to show 00:00 in the UI
		playbackStr.append(String.format(
				"%02d:%02d",
				TimeUnit.MILLISECONDS.toMinutes((long) currentTime),
				TimeUnit.MILLISECONDS.toSeconds((long) currentTime)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
								.toMinutes((long) currentTime))));

		playbackStr.append("/");

		// set total time as the audio is being played
		if (totalDuration != 0) {
			playbackStr.append(String.format(
					"%02d:%02d",
					TimeUnit.MILLISECONDS.toMinutes((long) totalDuration),
					TimeUnit.MILLISECONDS.toSeconds((long) totalDuration)
							- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
									.toMinutes((long) totalDuration))));
		} else
			Log.w(TAG, "Something strage this audio track duration in zero");

		mPlaybackTime.setText(playbackStr);

		// DebugLog.i(currentTime + " / " + totalDuration);
	}

	private void setPlayable() {
		if (mPlayButton != null)
			mPlayButton.setVisibility(View.VISIBLE);

		if (mPauseButton != null)
			mPauseButton.setVisibility(View.GONE);
	}

	private void setPausable() {
		if (mPlayButton != null)
			mPlayButton.setVisibility(View.GONE);
		if (mPauseButton != null)
			mPauseButton.setVisibility(View.VISIBLE);
	}

	/***
	 * Initialize the audio player. This method should be the first one to be
	 * called before starting to play audio using {@link AudioWife}
	 * 
	 * @param ctx
	 *            {@link Activity} Context
	 * @param uri
	 *            Uri of the audio to be played.
	 ****/
	public AudioWife init(Context ctx, Uri uri) {

		if (uri == null)
			throw new IllegalArgumentException("Uri cannot be null");

		if (mAudioWife == null)
			mAudioWife = new AudioWife();

		mUri = uri;

		mHandler = new Handler();

		initPlayer(ctx);

		return this;
	}

	/***
	 * You can set {@link Button} or an {@link ImageView} as Play control
	 ****/
	public AudioWife setPlayView(View play) {
		mPlayButton = play;
		
		initOnPlayClick();
		return this;
	}

	private void initOnPlayClick() {
		if(mPlayButton == null)
			throw new NullPointerException("Play view cannot be null");
		
		// add default click listener to the top
		// so that it is the one that gets fired first
		mPlayListeners.add(0, new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				play();
			}
		});
		
		// Fire all the attached listeners
		// when the play button is actually clicked
		mPlayButton.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				for (View.OnClickListener listener : mPlayListeners) {
					listener.onClick(v);
				}
			}
		});
	}

	/***
	 * You can set {@link Button} or an {@link ImageView} as the Pause control
	 * Pause playback functionality will be unavailable if this method is not
	 * called.
	 ****/
	public AudioWife setPauseView(View pause) {
		mPauseButton = pause;
		
		initOnPauseClick();
		return this;
	}
	
	private void initOnPauseClick() {
		if(mPauseButton == null)
			throw new NullPointerException("Pause view cannot be null");
		
		// add default click listener to the top
		// so that it is the one that gets fired first
		mPauseListeners.add(0, new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				pause();
			}
		});
		
		// Fire all the attached listeners
		// when the pause button is actually clicked
		mPauseButton.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				for (View.OnClickListener listener : mPauseListeners) {
					listener.onClick(v);
				}
			}
		});
	}

	/***
	 * Set current playback time. Use this if you have a playback time counter
	 * in the UI.
	 ****/
	public AudioWife setPlaytime(TextView playTime) {
		mPlaybackTime = playTime;
		
		// initialize the playtime to 0
		updatePlaytime(0);
		return this;
	}

	public AudioWife setSeekBar(SeekBar seekbar) {
		mSeekBar = seekbar;
		initMediaSeekBar();
		return this;
	}

	/****
	 * Add custom playback completion listener. Adding multiple listeners will
	 * queue up all the listeners and fire them on media playback completes.
	 */
	public AudioWife addOnCompletionListener(
			MediaPlayer.OnCompletionListener listener) {
		
		// add default click listener to the top
		// so that it is the one that gets fired first
		mCompletionListeners.add(0 ,listener);

		return this;
	}
	
	/****
	 * Add custom play view click listener. Adding multiple listeners will
	 * queue up all the listners and fire them all together when the event occurs.
	 */
	public AudioWife addOnPlayClickListener(
			View.OnClickListener listener) {

		mPlayListeners.add(listener);

		return this;
	}

	/****
	 * Add custom pause view click listener. Adding multiple listeners will
	 * queue up all the listners and fire them all together when the event occurs.
	 */
	public AudioWife addOnPauseClickListener(
			View.OnClickListener listener) {

		mPauseListeners.add(listener);

		return this;
	}
	
	/****
	 * Initialize and prepare the audio player
	 ****/
	private void initPlayer(Context ctx) {

		mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

		try {
			mMediaPlayer.setDataSource(ctx, mUri);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			mMediaPlayer.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		mMediaPlayer.setOnCompletionListener(mOnCompletion);
	}

	private MediaPlayer.OnCompletionListener mOnCompletion = new MediaPlayer.OnCompletionListener() {

		@Override
		public void onCompletion(MediaPlayer mp) {
			// set UI when audio finished playing
			int currentPlayTime = 0;
			mSeekBar.setProgress((int) currentPlayTime);
			updatePlaytime(currentPlayTime);
			setPlayable();
			// ensure that our completion listener fires first.
			// This will provide the developer to over-ride our
			// completion listener functionality

			fireCustomCompletionListeners(mp);
		}
	};

	private void initMediaSeekBar() {

		// update seekbar
		long finalTime = mMediaPlayer.getDuration();
		mSeekBar.setMax((int) finalTime);
		
		mSeekBar.setProgress(0);

		mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				mMediaPlayer.seekTo(seekBar.getProgress());
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {

			}
		});
	}

	private void fireCustomCompletionListeners(MediaPlayer mp) {
		for (OnCompletionListener listener : mCompletionListeners) {
			listener.onCompletion(mp);
		}
	}

	/***
	 * Releases the allocated resources.
	 * 
	 * <p>
	 * Call {@link #init(Context, Uri, SeekBar, View, View, TextView)} before
	 * calling {@link #play()}
	 * </p>
	 * */
	public void release() {

		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;
			mHandler = null;
		}
	}
}
