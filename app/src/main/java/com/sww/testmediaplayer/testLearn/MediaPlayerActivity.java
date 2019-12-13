package com.sww.testmediaplayer.testLearn;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.sww.testmediaplayer.R;

import java.io.IOException;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @author ShaoWenWen
 * @date 2019-12-12
 */
public class MediaPlayerActivity extends AppCompatActivity {

    private Button play, pause, stop;
    private Boolean noPlay = true;//定义播放状态
    private MediaPlayer mediaPlayer;
    private SurfaceHolder surfaceHolder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_player_learn);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                , WindowManager.LayoutParams.FLAG_FULLSCREEN);//全屏显示
        initView();
        initConfig();
        initListener();
    }

    private void initView() {
        //控制视频的按钮
        play = findViewById(R.id.play);
        pause = findViewById(R.id.pasue);
        stop = findViewById(R.id.stop);
        SurfaceView surfaceView = findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        mediaPlayer = new MediaPlayer();
    }

    private void initConfig() {
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    private void initListener() {
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play();
            }
        });
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                }
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                    noPlay = true;
                }
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Toast.makeText(MediaPlayerActivity.this, "视频播放完毕", Toast.LENGTH_SHORT).show();
            }
        });
        // 1.准备状态；
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer.start();
            }
        });
    }

    public void play() {
        if (noPlay) {
            noPlay = false;
            mediaPlayer.reset(); // 重置MediaPlayer
            mediaPlayer.setDisplay(surfaceHolder); // 把视频画面输出到SurfaceView中
            try {
                mediaPlayer.setDataSource("https://biz-site.zone1.meitudata.com/e7764d4aa5d969c8213f14d05e92b588-6749.mp4");
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            mediaPlayer.start();
        }
    }



    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
        }
        super.onDestroy();

    }
}
