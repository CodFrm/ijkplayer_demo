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
        String url3="http://7xk12c.media1.z0.glb.clouddn.com/hop.mp4";
        String url2="http://192.168.1.12/bd51f964a36e61ad58c605bdb0f4128a.mp4";
        String url1="https://v0.baidupcs.com/video/netdisk-videotran-yangquan/ec7d5a1ccaad3b03e6e9ac94c0da050a_8242_1_ts/bd51f964a36e61ad58c605bdb0f4128a?ehps=1&iv=0&uo=ct&to=pvb00&range=378680-2186928&xcode=a317d15c46995e6dffd43be2db37bef68f3de4bd386d78bf0047804bae6a1ada&fid=0364bcae33bb6ec5e0b00777ea16ec2a-725899816&ouk=725899816&len=1808249&time=1503167938&r=114948126&sign=BOUTHNF-F3530edecde9cd71b79378b290804a96-17eeXpA90hYaYUvLVxW6GEL1fSI%253D&sta_dx=329&sta_cs=56&app_id=250528&logid=5356237056603872503&region=yangquan&isplayer=&devuid=&etag=bd51f964a36e61ad58c605bdb0f4128a";
        String url4="http://192.168.1.12/streaming.m3u8";
        String url5="http://video.visha.cc/?action=x2f&url=http://video.visha.cc/app/cache/m3u8/f7a1b6fea84d115d96a46d6edacfb83e.xml";
        ijk.setVideoUrl(url3);
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
