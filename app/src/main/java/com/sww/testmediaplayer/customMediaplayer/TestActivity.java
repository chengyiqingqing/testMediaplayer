package com.sww.testmediaplayer.customMediaplayer;

import android.os.Bundle;

import com.sww.testmediaplayer.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @author ShaoWenWen
 * @date 2019-12-16
 */
public class TestActivity extends AppCompatActivity {

    private MediaPlayerView mediaPlayerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_media_player);
        mediaPlayerView = findViewById(R.id.meida);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayerView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mediaPlayerView.onStop();
    }

}
