package com.example.codef.ijkplay_demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;

import com.example.codef.ijkplay_demo.ijkplayer.IVideoEvent;
import com.example.codef.ijkplay_demo.ijkplayer.ijkvideo;

public class MainActivity extends AppCompatActivity implements IVideoEvent {
    ijkvideo ijk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        initPlayer();
    }

    public void initPlayer() {
//        onCreate(null);
        ijk = new ijkvideo(this);
        ijk.createPlayer(0, 0, 1080, 640);
        String url = "http://192.168.1.9/v.f4v";
        ijk.setVideoUrl(url);
        ijk.setEvent(this);
        ijk.start();
    }

    public void btnClick(View v) {
        ijk.fullScreen();
    }
    @Override
    public void onFullScreen(){
        int a=0;
        a=a+2;
        Log.e(null,"error");
    }

    @Override
    public void onDestroy() {
        ijk.deletePlayer();
        super.onDestroy();
    }

    boolean isPlay = false;

    @Override
    public void onPause() {
        super.onPause();
        if (ijk.isPlaying()) {
            isPlay = false;
            ijk.pause();
        } else {
            isPlay = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isPlay) {
            ijk.start();
        }
    }

}
