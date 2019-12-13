package com.sww.testmediaplayer.testLearnImage;

import androidx.appcompat.app.AppCompatActivity;

/**
 * @author ShaoWenWen
 * @date 2019-12-13
 */
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import com.sww.testmediaplayer.R;

import java.io.IOException;

public class MediaPlayerImageActivity extends AppCompatActivity implements SurfaceHolder.Callback,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnVideoSizeChangedListener, MediaPlayer.OnSeekCompleteListener, View.OnClickListener, MediaPlayer.OnBufferingUpdateListener {


    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private MediaPlayer player;


    private Button mStart;
    private Button mPause;
    private Button mStop;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_media_player);

        mStart = findViewById(R.id.start);
        mPause = findViewById(R.id.pause);
        mStop = findViewById(R.id.stop);


        mStart.setOnClickListener(this);
        mPause.setOnClickListener(this);
        mStop.setOnClickListener(this);
        surfaceView = findViewById(R.id.SurfaceView01);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        player = new MediaPlayer();
        player.setLooping(true);
        CURRENT = IDLE;
        setAttrs(player);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            surfaceHolder.setFixedSize(320, 220);
            opertion(IDLE);
            CURRENT = INITIALZED;
//            player.setDataSource("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4");
            player.setDataSource("https://biz-site.zone1.meitudata.com/e7764d4aa5d969c8213f14d05e92b588-6749.mp4");
            CURRENT = PREPARING;
            player.prepareAsync();
        } catch (Exception e) {
            player.release();
            player = null;
            player = new MediaPlayer();
            setAttrs(player);
            player.setLooping(true);
            opertion(IDLE);
            try {
                CURRENT = INITIALZED;
//                player.setDataSource("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4");
                player.setDataSource("https://biz-site.zone1.meitudata.com/e7764d4aa5d969c8213f14d05e92b588-6749.mp4");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            CURRENT = PREPARING;
            player.prepareAsync();
        }

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
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        CURRENT = COMPLETED;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        opertion(ERROR);
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }


    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

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


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.start) {
            opertion(STARTED);
        } else if (v.getId() == R.id.pause) {
            opertion(PAUSE);
        } else if (v.getId() == R.id.stop) {
            opertion(STOP);
        }
    }

    @Override
    protected void onPause() {
        position = player.getCurrentPosition();
        opertion(PAUSE);
        super.onPause();
    }

    @Override
    protected void onStop() {
        opertion(STOP);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        opertion(RELEASE);
    }

    private static final String TAG = "MediaPlayerImageActivit";

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        Log.e(TAG, "onBufferingUpdate: " );
    }

}
