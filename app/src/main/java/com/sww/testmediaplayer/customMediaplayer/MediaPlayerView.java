package com.sww.testmediaplayer.customMediaplayer;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

import com.sww.testmediaplayer.R;

import java.io.IOException;

/**
 * @author ShaoWenWen
 * @date 2019-12-16
 */
public class MediaPlayerView extends FrameLayout implements SurfaceHolder.Callback,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnInfoListener, MediaPlayer.OnPreparedListener,
        MediaPlayer.OnVideoSizeChangedListener, MediaPlayer.OnSeekCompleteListener,
        View.OnClickListener, MediaPlayer.OnBufferingUpdateListener {

    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private MediaPlayer player;

    private final static int IDLE = 0;
    private final static int INITIALZED = 1;
    private final static int PREPARED = 2;
    private final static int STARTED = 3;
    private final static int COMPLETED = 4;
    private final static int PREPARING = 5;
    private final static int STOP = 6;
    private final static int PAUSE = 7;
    private final static int ERROR = 8;
    private final static int RELEASE = 9;

    private static int CURRENT = -1;

    private int position = 0;

    public MediaPlayerView(Context context) {
        this(context, null);
    }

    public MediaPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context,R.layout.view_media_player,this);
        surfaceView = findViewById(R.id.SurfaceView01);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        player = new MediaPlayer();
        player.setLooping(true);
        CURRENT = IDLE;
        setAttrs(player);
    }

    public void setAttrs(MediaPlayer player) {
        System.out.println("XXXMainActivity.setAttrs");
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
        player.setOnInfoListener(this);
        player.setOnPreparedListener(this);
        player.setOnSeekCompleteListener(this);
        player.setOnVideoSizeChangedListener(this);
        player.setOnBufferingUpdateListener(this);

    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        CURRENT = COMPLETED;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        player.setDisplay(surfaceHolder);
        CURRENT = PREPARED;
        player.start();
        if (position != 0) {
            player.seekTo(position);
        }
        System.out.println("XXXXonPrepared+ " + Thread.currentThread().getName());
        CURRENT = STARTED;
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            surfaceHolder.setFixedSize(320, 220);
            opertion(IDLE);
            CURRENT = INITIALZED;
            player.setDataSource("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4");
            //            player.setDataSource("https://biz-site.zone1.meitudata.com/e7764d4aa5d969c8213f14d05e92b588-6749.mp4");
            CURRENT = PREPARING;
            player.prepareAsync();
        } catch (Exception e) {

        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onClick(View v) {

    }

    public void opertion(int current) {
        switch (current) {
            case IDLE:
                CURRENT = IDLE;
                break;
            case INITIALZED:
                break;
            case PREPARED:

                break;
            case STARTED:
                if (CURRENT == PAUSE) {
                    player.start();
                    CURRENT = STARTED;
                } else if (CURRENT == STOP) {
                    CURRENT = PREPARING;
                    player.prepareAsync();
                }
                break;
            case COMPLETED:
                break;
            case PREPARING:

                break;
            case STOP:
                if (CURRENT == STOP) {
                    return;
                }
                if (CURRENT == STARTED || CURRENT == PAUSE || CURRENT == PREPARED || CURRENT == COMPLETED) {
                    player.stop();
                    CURRENT = STOP;
                    position = 0;
                }
                break;
            case PAUSE:

                if (CURRENT == PAUSE) {
                    return;
                }
                if ((CURRENT == STARTED) || (player.isLooping() && CURRENT == COMPLETED)) {
                    player.pause();
                    CURRENT = PAUSE;
                }
                break;
            case ERROR:
                player.reset();
                opertion(IDLE);
                player.release();
                player = new MediaPlayer();
                setAttrs(player);
                player.setLooping(true);
                CURRENT = IDLE;
                try {
                    CURRENT = INITIALZED;
                    player.setDataSource("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                surfaceHolder.setFixedSize(320, 220);
                surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
                CURRENT = PREPARING;
                player.prepareAsync();
                break;
            case RELEASE:
                if (player != null) {
                    CURRENT = RELEASE;
                    player.reset();
                    player.release();
                }
                break;
        }
    }

    public void onPause() {
        position = player.getCurrentPosition();
        opertion(PAUSE);
    }

    public void onStop() {
        opertion(STOP);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        surfaceHolder.getSurface().release();
        opertion(RELEASE);
    }
}
