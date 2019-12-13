package com.sww.testmediaplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    SurfaceView mSvVideoPlayer;

    private MediaPlayer mMediaPlayer;
    private int mPosition = 0;
    private boolean hasActiveHolder = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSvVideoPlayer = findViewById(R.id.sv_video_player);
        playVideo();
    }

    /**
     * 播放视频
     */
    public void playVideo() {
        if (mMediaPlayer == null) {
            //实例化MediaPlayer对象
            mMediaPlayer = new MediaPlayer();
            mSvVideoPlayer.setVisibility(View.VISIBLE);
            boolean mHardwareDecoder = false;
            // 不维持自身缓冲区，直接显示
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB && mHardwareDecoder) {
                mSvVideoPlayer.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            }
            mSvVideoPlayer.getHolder().setFixedSize(getScreenWidth(), getScreenHeight());
            mSvVideoPlayer.getHolder().setKeepScreenOn(true);//保持屏幕常亮
            mSvVideoPlayer.getHolder().addCallback(new SurFaceCallback());
        }
    }

    /**
     * 向player中设置dispay，也就是SurfaceHolder。但此时有可能SurfaceView还没有创建成功，所以需要监听SurfaceView的创建事件
     */
    private final class SurFaceCallback implements SurfaceHolder.Callback {

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if (mMediaPlayer == null) {
                return;
            }
            if (!hasActiveHolder) {
                play(mPosition);
                hasActiveHolder = true;
            }
            if (mPosition > 0) {
                play(mPosition);
                mPosition = 0;
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (mMediaPlayer == null) {
                return;
            }
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
                mPosition = mMediaPlayer.getCurrentPosition();
            }
        }

        private void play(int position) {
            try {
                //添加播放视频的路径与配置MediaPlayer
//                AssetFileDescriptor fileDescriptor = getResources().openRawResourceFd(R.raw.info);
                mMediaPlayer.reset();
                //给mMediaPlayer添加预览的SurfaceHolder，将播放器和SurfaceView关联起来
                mMediaPlayer.setDisplay(mSvVideoPlayer.getHolder());

                /*mMediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(),
                        fileDescriptor.getStartOffset(),
                        fileDescriptor.getLength());*/
                mMediaPlayer.setDataSource("https://biz-site.zone1.meitudata.com/e7764d4aa5d969c8213f14d05e92b588-6749.mp4");
//                mMediaPlayer.setDataSource("https://biz-site.zone1.meitudata.com/c5146bc3dd8b611b020fd3e75e951bfd-6232.mp4");
                // 缓冲
//                mMediaPlayer.prepare();
                mMediaPlayer.prepareAsync();
                mMediaPlayer.setOnBufferingUpdateListener(new BufferingUpdateListener());
                mMediaPlayer.setOnPreparedListener(new PreparedListener(position));
                mMediaPlayer.setOnCompletionListener(new CompletionListener());
                mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int errorCode, int errorMsg) {
                        Log.e(MainActivity.TAG, "onError: errorCode:" + errorCode + " errorMsg:" + errorMsg);
                        return false;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 缓冲变化时回调
     */
    private final class BufferingUpdateListener implements MediaPlayer.OnBufferingUpdateListener {

        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
        }
    }

    /**
     * 准备完成回调
     * 只有当播放器准备好了之后才能够播放，所以播放的出发只能在触发了prepare之后
     */
    private final class PreparedListener implements MediaPlayer.OnPreparedListener {
        private int position;

        public PreparedListener(int position) {
            this.position = position;
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            mMediaPlayer.start();
            if (position > 0) {
                mMediaPlayer.seekTo(position);
            }
        }
    }

    /**
     * 播放结束时回调
     */
    private final class CompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            mMediaPlayer.start();
        }
    }

    @Override
    public void onDestroy() {
        //释放内存，MediaPlayer底层是运行C++的函数方法，不使用后必需释放内存
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
            }
            mMediaPlayer.release();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        super.onDestroy();
    }

    private int getScreenWidth() {
        return ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
    }

    private int getScreenHeight() {
        return ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                .getHeight();
    }
}
