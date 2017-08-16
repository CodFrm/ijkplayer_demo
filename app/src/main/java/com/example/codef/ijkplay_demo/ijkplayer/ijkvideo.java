package com.example.codef.ijkplay_demo.ijkplayer;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.codef.ijkplay_demo.R;

import java.util.Timer;
import java.util.TimerTask;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by codef on 2017/8/14.
 */

public class ijkvideo {
    private static final String TAG = "ijkvideo";
    private ImageButton fullButton;
    private TextView mTitleText;

    public ijkvideo(Context context) {
        mContext = context;
        initView();
    }

    private IjkVideoView mVideoView = null;
    private RelativeLayout mViewHolder = null;
    private Context mContext = null;
    private int mWidth, mHeight, mLeft, mTop;

    private ImageButton playButton;
    private SeekBar seekBar;
    private LinearLayout mPlayTop, mPlayController;

    private void initView() {
        //init player
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        mLeft = 0;
        mTop = 0;
        Resources resources = mContext.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        mWidth = dm.widthPixels;
        mHeight = (int) (mWidth * 0.5);

    }

    public GestureDetector mGestureDetector;

    public void createPlayer() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        mViewHolder = inflater.inflate(R.layout.play, null).findViewById(R.id.combineCtrl);
        mVideoView = (IjkVideoView) mViewHolder.findViewById(R.id.video_view);
        mPlayTop = (LinearLayout) mViewHolder.findViewById(R.id.play_top);
        mPlayController = (LinearLayout) mViewHolder.findViewById(R.id.play_controller);
        mTitleText=(TextView)mViewHolder.findViewById(R.id.play_title);
        RelativeLayout.LayoutParams rllp = new RelativeLayout.LayoutParams(mWidth, mHeight);
        rllp.leftMargin = mLeft;
        rllp.topMargin = mTop;
        ((Activity) mContext).addContentView(mViewHolder, rllp);
        hidden();
        toggleAspectRatio(1);
        mGestureDetector = new GestureDetector(mViewHolder.getContext(), new myGestureListener());
        mViewHolder.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return mGestureDetector.onTouchEvent(motionEvent);
            }
        });
        playButton = (ImageButton) mViewHolder.findViewById(R.id.play_play);
        playButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if (mVideoView != null) {
                    pause();
                    show();
                }
            }
        });
        fullButton = (ImageButton) mViewHolder.findViewById(R.id.play_full);
        fullButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if (mVideoView != null) {
                    fullScreen();
                }
            }
        });
        seekBar = (SeekBar) mViewHolder.findViewById(R.id.play_seekbar);
        seekBar.setMax(100);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isClick = false;
                int value = (int) Math.floor((double) allTime * ((double) seekBar.getProgress() / 100));
                mVideoView.seekTo(value);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isClick = true;
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
            }
        });
        mVideoView.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
                if (i == IMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START) {
                    allTime = mVideoView.getDuration();
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        public void run() {
                            Message message = Message.obtain();
                            message.arg1 = IMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START;
                            mHandler.sendMessage(message);//发送消息
                        }
                    }, 1000, 1000);
                }
                return false;
            }
        });
        mVideoView.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer iMediaPlayer) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    playButton.setImageDrawable(mVideoView.getResources().getDrawable(R.drawable.btn_play, null));
                }
            }
        });
    }

    private boolean isFull = false;
    public void setTitle(String str){
        mTitleText.setText(str);
    }
    public void fullScreen() {
        show();
        if (isFull) {
            WindowManager.LayoutParams attr = ((Activity) mContext).getWindow().getAttributes();
            attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            ((Activity) mContext).getWindow().setAttributes(attr);
            ((Activity) mContext).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            if (((Activity) mContext).getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                ((Activity) mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
            move(mLeft,mTop,mWidth,mHeight);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                fullButton.setImageDrawable(mVideoView.getResources().getDrawable(R.drawable.btn_full, null));
            }
            isFull=false;
        } else {
            WindowManager.LayoutParams params = ((Activity) mContext).getWindow().getAttributes();
            params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            ((Activity) mContext).getWindow().setAttributes(params);
            ((Activity) mContext).getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            if (((Activity) mContext).getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                ((Activity) mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
//            ViewGroup.LayoutParams rllp = mViewHolder.getLayoutParams();
//            rllp.height = -1;
//            rllp.width = -1;
//            mViewHolder.setLayoutParams(rllp);
            move(0,0,-1,-1);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                fullButton.setImageDrawable(mVideoView.getResources().getDrawable(R.drawable.btn_mini, null));
            }
            isFull = true;
        }
    }

    public void move(int left, int top, int width, int height) {
        ViewGroup.LayoutParams rllp =  mViewHolder.getLayoutParams();
        rllp.width=width;
        rllp.height=height;
        mViewHolder.setLayoutParams(rllp);
    }

    public void pause() {
        if (mVideoView.isPlaying()) {
            mVideoView.pause();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                playButton.setImageDrawable(mVideoView.getResources().getDrawable(R.drawable.btn_play, null));
            }
        } else {
            mVideoView.start();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                playButton.setImageDrawable(mVideoView.getResources().getDrawable(R.drawable.btn_pause, null));
            }
        }
    }

    private int allTime;
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.arg1) {
                case IMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START: {
                    int nowTime = mVideoView.getCurrentPosition();
//                    playTime.setText(timeLengthToTime(nowTime));
                    if (!isClick) {
                        int value = (int) Math.floor(((double) nowTime / (double) allTime) * 100);
                        seekBar.setProgress(value);
                    }
                    break;
                }
            }

        }
    };

    private String timeLengthToTime(int length) {
        String retStr = new String();
        length /= 1000;
        int tmp = (int) Math.floor(length / 3600);
        length %= 3600;
        if (tmp > 0) {
            retStr = int2str(tmp) + ":";
        }
        tmp = (int) Math.floor(length / 60);
        length %= 60;
        retStr += int2str(tmp) + ":" + int2str(length);
        return retStr;
    }

    private String int2str(int value) {
        String ret = String.valueOf(value);
        while (ret.length() < 2) {
            ret = "0" + ret;
        }
        return ret;
    }

    class myGestureListener extends GestureDetector.SimpleOnGestureListener {
        public myGestureListener() {
            super();
        }

        @Override
        public boolean onDown(MotionEvent event) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2,      //滑动事件
                               float velocityX, float velocityY) {
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {//双击
            pause();
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {//单纯点击
            if (isShowing()) {
                hidden();
            } else {
                show();
            }
            return true;
        }
    }

    public boolean isShowing() {
        if (delay > 0) {
            return true;
        }
        return false;
    }

    public void setVideoUrl(String url) {
        mVideoView.setVideoPath(url);
    }

    public void start() {
        mVideoView.start();
    }

    public int toggleAspectRatio(int aspect_ratio) {
        return mVideoView.toggleAspectRatio(aspect_ratio);
    }

    public void hidden() {
        delay = 0;
        mPlayTop.setVisibility(View.INVISIBLE);
        mPlayController.setVisibility(View.INVISIBLE);
    }

    private int delay = 0;
    private boolean isClick = false;

    public void show() {
        show(5000);
    }

    public void show(int time) {
        mPlayTop.setVisibility(View.VISIBLE);
        mPlayController.setVisibility(View.VISIBLE);
        if (delay >= 0) {
            if (delay == 0) {
                delay = time;
                sleepHide();
            }else{
                delay = time;
            }
        }
    }

    private void sleepHide() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (delay > 0) {
                    if (isClick) {
                        delay = 5000;
                    } else {
                        delay -= 1000;
                    }
                    sleepHide();
                } else {
                    hidden();
                }
            }
        }, 1000);
    }
}
