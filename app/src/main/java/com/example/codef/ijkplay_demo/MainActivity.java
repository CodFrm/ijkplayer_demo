package com.example.codef.ijkplay_demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;

import com.example.codef.ijkplay_demo.ijkplayer.ijkvideo;

public class MainActivity extends AppCompatActivity {
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
    public void initPlayer(){
        ijk=new ijkvideo(this);
        ijk.createPlayer();
        String url3="http://192.168.1.12/video/?action=x2f&url=http://192.168.1.12/video/app/cache/m3u8/d574c6d4456c04ed48b5345ff61684ea.xml";
        String url2="http://192.168.1.12/1.ffconcat";
        String url1="http://192.168.1.12/video/app/cache/m3u8/aeb431901851cae1f3f35c76ddcd98e1.m3u8";
        String url4="http://7xk12c.media1.z0.glb.clouddn.com/H256720P30.mp4";
        ijk.setVideoUrl(url4);
        ijk.start();
    }

    public void btnClick(View v){
        ijk.fullScreen();
    }

    @Override
    public void onDestroy(){
        ijk.deletePlayer();
        super.onDestroy();
    }
    boolean isPlay=false;
    @Override
    public void onPause(){
        super.onPause();
        if (ijk.isPlaying()){
            isPlay=false;
            ijk.pause();
        }else {
            isPlay=true;
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        if(!isPlay){
            ijk.start();
        }
    }

}
