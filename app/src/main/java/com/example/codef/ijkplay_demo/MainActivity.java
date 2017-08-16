package com.example.codef.ijkplay_demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
        String url1="http://192.168.1.12/video/?action=iqiyi_m3u8&url=aHR0cDovL2RhdGEudmlkZW8uaXFpeWkuY29tL3ZpZGVvcy92MC8yMDE3MDUxMS8wZC81Mi85OGE3MWNiNDFmMGQ1YTAxZmI1ZDFjMWUxYWFkNDZjYy5mNHY/cWRfdHZpZD02MjYyODg2MDAmcWRfdmlwcmVzPTAmcWRfaW5kZXg9MSZxZF9haWQ9MjA0MjE4OTAxJnFkX3N0ZXJ0PTAmcWRfc2NjPWNiOTlmYTgwOTFmMDljMzMxYjYwOWRhMTY4N2NkZTIxJnFkX3NjPTE5MjY1YzJkYTBmNTNjNjY3NDcxNGVkYWEzYmJiMGUzJnFkX3A9NzFmNzMyNmUmcWRfaXA9NzFmNzMyNmUmcWRfaz03NWZmMDk1ZDBkZTFhMzQwMzYzNWY2OTAyNmJjOGZiZiZxZF9zcmM9MDEwMTAwMzEwMTAwMDAwMDAwMDAmcWRfdmlwZHluPTAmcWRfdWlkPTAmcWRfdG09MTUwMjY3NzY5NjUzNyZxZF92aXA9MQ==";
        String url4="http://7xk12c.media1.z0.glb.clouddn.com/H256720P30.mp4";
        ijk.setVideoUrl(url4);
        ijk.start();
    }

    public void btnClick(View v){
        ijk.fullScreen();
    }
}
