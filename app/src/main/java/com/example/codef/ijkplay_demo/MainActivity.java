package com.example.codef.ijkplay_demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.codef.ijkplay_demo.ijkplayer.IVideoEvent;
import com.example.codef.ijkplay_demo.ijkplayer.ijkvideo;

import java.io.File;
import java.io.FileOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        ijk = new ijkvideo(this);
        ijk.setEvent(this);
        ijk.createPlayer(0, 0, 1080, 640);
        String url = "http://192.168.1.110/v.f4v";
        ijk.addShar("测试", "http://192.168.1.110/v.f4v");
        ijk.addShar("呵呵", "http://192.168.1.110/v.f4v");
        ijk.addShar("会更好的", "http://192.168.1.110/v.f4v");
        ijk.selectShar("呵呵");
//        ijk.setVideoUrl(url);

//        ijk.start();
    }

    @Override
    public void onReception() {
        ijk.start();
    }

    @Override
    public boolean onSharSwitch(String Name, String Url) {
        return false;
    }

    @Override
    public void onBackstage() {
        ijk.pause();
    }

    public String xml2ffconcat(String xmlText) {
        String cachePath = getApplicationContext().getCacheDir().getPath() + "/" + System.currentTimeMillis() + ".cae";
        try {
            File ffconcatCacheFile = new File(cachePath);
            if (!ffconcatCacheFile.exists()) {
                ffconcatCacheFile.createNewFile();
            }
            FileOutputStream bos = new FileOutputStream(ffconcatCacheFile);
            Pattern p = Pattern.compile("<file><!\\[CDATA\\[(.*?)]]></file><seconds>(.*?)</seconds>");
            Matcher m = p.matcher(xmlText);
            String tmp = "ffconcat version 1.0\n";
            bos.write(("ffconcat version 1.0\n").getBytes("UTF-8"));
            while (m.find()) {
                tmp += "file '" + m.group(1) + "'\nduration " + m.group(2) + "\n";
                bos.write(("file '" + m.group(1) + "'\nduration " + m.group(2) + "\n").getBytes("UTF-8"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cachePath;
    }

    public void btnClick(View v) {
        ijk.fullScreen();
    }

    @Override
    public void onFullScreen() {
        int a = 0;
        a = a + 2;
        Log.e(null, "error");
    }

    @Override
    public boolean onError(int i) {
        Log.e(null, "error:code:" + i);
        return false;
    }

    @Override
    public void onDestroy() {
        ijk.deletePlayer();
        super.onDestroy();
    }
}
