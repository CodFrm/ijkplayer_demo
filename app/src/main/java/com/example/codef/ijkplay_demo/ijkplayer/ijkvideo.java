package com.example.codef.ijkplay_demo.ijkplayer;

import android.app.Activity;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.codef.ijkplay_demo.R;

import java.io.IOException;
import java.lang.reflect.Field;
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
    private ImageButton playBack;
    private TextView playTime;
    private TextView playAllTime;
    private myGestureListener mGesture;
    private RelativeLayout mViewLight;
    private RelativeLayout mViewSound;
    private RelativeLayout mStatus;
    private ProgressBar barLight;
    private ProgressBar barSound;
    private ImageView loadingView;
    private Button sharBtn;
    private ListView sharList;
    private LinearLayout sharLayout;

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

    public int getLeft() {
        return mLeft;
    }

    public int getTop() {
        return mTop;
    }

    public int getHeight() {
        return mHeight;
    }

    public int getWidth() {
        return mWidth;
    }

    /**
     * 通过反射的方式获取状态栏高度
     *
     * @return
     */
    private int getStatusBarHeight() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if ((WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS & ((Activity) mContext).getWindow().getAttributes().flags)
                        == WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS) {
                    Class<?> c = Class.forName("com.android.internal.R$dimen");
                    Object obj = c.newInstance();
                    Field field = c.getField("status_bar_height");
                    int x = Integer.parseInt(field.get(obj).toString());
                    return mContext.getResources().getDimensionPixelSize(x);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public GestureDetector mGestureDetector;

    public View createPlayer(int left, int top, int width, int height) {
        mWidth = width;
        mHeight = height;
        mTop = top;
        mLeft = left;
        return createPlayer();
    }

    private View playView;

    public void setTitle(String title) {
        mTitleText.setText(title);
    }

    public View createPlayer() {
        isCreate = true;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        playView = inflater.inflate(R.layout.play, null);
        mViewHolder = (RelativeLayout) playView.findViewById(R.id.combineCtrl);
        mVideoView = (IjkVideoView) mViewHolder.findViewById(R.id.video_view);
        mPlayTop = (LinearLayout) mViewHolder.findViewById(R.id.play_top);
        mPlayController = (LinearLayout) mViewHolder.findViewById(R.id.play_controller);
        mViewLight = (RelativeLayout) mViewHolder.findViewById(R.id.view_light);
        mViewLight.setVisibility(View.INVISIBLE);
        barLight = (ProgressBar) mViewHolder.findViewById(R.id.bar_light);
        mViewSound = (RelativeLayout) mViewHolder.findViewById(R.id.view_sound);
        mViewSound.setVisibility(View.INVISIBLE);
        barSound = (ProgressBar) mViewHolder.findViewById(R.id.bar_sound);
        playTime = (TextView) mViewHolder.findViewById(R.id.now_time);
        playAllTime = (TextView) mViewHolder.findViewById(R.id.all_time);
        mTitleText = (TextView) mViewHolder.findViewById(R.id.play_title);
        mStatus = (RelativeLayout) mViewHolder.findViewById(R.id.statusLayout);
        mStatus.setVisibility(View.INVISIBLE);
        loadingView = (ImageView) mViewHolder.findViewById(R.id.loading);
        sharLayout = (LinearLayout) mViewHolder.findViewById(R.id.shar_layout);
        sharLayout.setVisibility(View.INVISIBLE);
        sharBtn = (Button) mViewHolder.findViewById(R.id.shar_btn);
        sharList = (ListView) mViewHolder.findViewById(R.id.shar_list);
        aptShar = new ArrayAdapter<shar>(((Activity) mContext), R.layout.shar_list_item);
        sharList.setAdapter(aptShar);
//        String[] datas = {"4K","蓝光(1080P)", "高清", "流畅"};
//        sharList.setAdapter(new ArrayAdapter<String>(((Activity) mContext), R.layout.shar_list_item, datas));
//        sharList.setSelection(1);

        FrameLayout.LayoutParams rllp = new FrameLayout.LayoutParams(mWidth, mHeight);
        rllp.leftMargin = mLeft;
        rllp.topMargin = mTop;
        ((Activity) mContext).addContentView(mViewHolder, rllp);
        toggleAspectRatio(1);
        mGesture = new myGestureListener();
        mGestureDetector = new GestureDetector(mViewHolder.getContext(), mGesture);
        mViewHolder.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == 1 && mGesture.isScroll) {//GestureDetector的onScroll竟然不会触发手指抬起事件,只好自己来实现了
                    return mGesture.onSingleTapUp(motionEvent);
                }
                return mGestureDetector.onTouchEvent(motionEvent);
            }
        });
        mVideoView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_BACK: {
                        if (isFull) {
                            fullScreen();
                            return true;
                        }
                    }
                }
                return false;
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
        sharBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVideoView != null) {
                    hidden();
                    sharList.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sharList.requestFocusFromTouch();
                            sharList.setSelection(sharSelectIndex);
                        }
                    }, 500);
                    sharLayout.setVisibility(View.VISIBLE);
                }
            }
        });
        playBack = (ImageButton) mViewHolder.findViewById(R.id.play_back);
        playBack.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if (isFull) {
                    fullScreen();
                } else {
                    try {
                        Runtime runtime = Runtime.getRuntime();
                        runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
                    } catch (IOException e) {

                    }
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
        seekBar.setMax(10000);//为了拖动的精确,设置最大为1w
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isClick = false;
                int value = (int) Math.floor((double) allTime * ((double) seekBar.getProgress() / 10000));
                mVideoView.seekTo(value);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isClick = true;
                delay = 5000;
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
            }
        });
        mVideoView.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
                return mIjkEvent.onError(i);
            }
        });
        mVideoView.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
                if (i == IMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START) {
                    allTime = mVideoView.getDuration();
                    Timer timer = new Timer();
                    playAllTime.setText(timeLengthToTime(allTime));
                    timer.schedule(new TimerTask() {
                        public void run() {
                            if (!isCreate) {
                                cancel();
                                return;
                            }
                            Message message = Message.obtain();
                            message.arg1 = IMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START;
                            mHandler.sendMessage(message);//发送消息
                        }
                    }, 1000, 1000);
                } else if (i == IMediaPlayer.MEDIA_INFO_BUFFERING_START) {
                    mStatus.setVisibility(View.VISIBLE);
                    loading();
                    Rotation = 0;
                    isLoading = true;
                } else if (i == IMediaPlayer.MEDIA_INFO_BUFFERING_END) {
                    mStatus.setVisibility(View.INVISIBLE);
                    isLoading = false;
                    loadingView.setRotation(0);
                }
                return false;
            }
        });
        sharList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectShar(aptShar.getItem(position).getName());
            }
        });
        sharList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                selectShar(aptShar.getItem(position).getName());
                return false;
            }
        });

        barLight.setProgress(getLight());
        AudioManager am = (AudioManager) ((Activity) mContext).getSystemService(Context.AUDIO_SERVICE);
        barSound.setMax(am.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        barSound.setProgress(am.getStreamVolume(AudioManager.STREAM_MUSIC));
        show();
        Rotation = 0;

        ((Activity) mContext).getApplication().registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            public int mPlan;

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {
                mPlan++;
                if (mPlan == 1) {
                    mIjkEvent.onReception();
                }
            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                mPlan--;
                if (mPlan == 0) {
                    mIjkEvent.onBackstage();
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
        return playView;
    }

    public class shar {
        public String Name;
        public String Url;

        shar(String n, String u) {
            Name = n;
            Url = u;
        }

        @Override
        public String toString() {
            return Name;
        }

        public String getName() {
            return Name;
        }

        public String getUrl() {
            return Url;
        }
    }

    private ArrayAdapter<shar> aptShar;

    public void removeAllShar() {
        aptShar.clear();
    }

    public void addShar(String Name, String Url) {
        aptShar.add(new shar(Name, Url));
    }

    private int sharSelectIndex = 0;

    public void selectShar(String Name) {
        sharLayout.setVisibility(View.INVISIBLE);
        for (int i = 0; i < aptShar.getCount(); i++) {
            shar tmp = aptShar.getItem(i);
            if (tmp.toString() == Name) {
                if(!mIjkEvent.onSharSwitch(tmp.getName(),tmp.getUrl())) {
                    sharSelectIndex = i;
                    sharList.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sharList.requestFocusFromTouch();
                            sharList.setSelection(sharSelectIndex);
                        }
                    }, 500);
                    int nowTime = mVideoView.getCurrentPosition();
                    setVideoUrl(tmp.getUrl());
                    seekTo(nowTime);
                    start();
                }
                break;
            }

        }
    }

    private boolean isLoading = false;
    private int Rotation = 0;

    private void loading() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (isLoading) {
                    loading();
                    loadingView.setRotation(Rotation += 12);
                    if (Rotation >= 360) Rotation -= 360;
                }
            }
        }, 50);
    }

    private int getLight() {
        Window window = ((Activity) mContext).getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        if (lp.screenBrightness == -1) {
            return getScreenBrightness();
        }
        return (int) Math.floor(lp.screenBrightness * 255f);
    }

    public void setLight(int brightness) {
        Window window = ((Activity) mContext).getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        if (brightness == -1) {
            lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
        } else {
            lp.screenBrightness = (brightness <= 0 ? 1 : brightness) / 255f;
        }
        window.setAttributes(lp);
    }

    private int getScreenBrightness() {
        ContentResolver contentResolver = ((Activity) mContext).getContentResolver();
        int defVal = 125;
        return Settings.System.getInt(contentResolver,
                Settings.System.SCREEN_BRIGHTNESS, defVal);
    }

    private boolean isCreate = false;

    public void deletePlayer() {
        isCreate = false;
        mVideoView.release(true);
        mViewHolder.removeAllViews();
    }

    public boolean isFull = false;
    IVideoEvent mIjkEvent;

    public void setEvent(IVideoEvent ev) {
        mIjkEvent = ev;
    }

    public void fullScreen() {
        //mIev.onFullScreen();
        if (isFull) {
            WindowManager.LayoutParams attr = ((Activity) mContext).getWindow().getAttributes();
            attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            ((Activity) mContext).getWindow().setAttributes(attr);
            ((Activity) mContext).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            showNavigation();
            if (((Activity) mContext).getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                ((Activity) mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
            move(mLeft, mTop, mWidth, mHeight);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                fullButton.setBackground(mVideoView.getResources().getDrawable(R.drawable.play_btn_full, null));
            }
            isFull = false;
        } else {
            WindowManager.LayoutParams params = ((Activity) mContext).getWindow().getAttributes();
            params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            ((Activity) mContext).getWindow().setAttributes(params);
            ((Activity) mContext).getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            hiddenNavigation();
            if (((Activity) mContext).getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                ((Activity) mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
            move(0, 0, -1, -1);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                fullButton.setBackground(mVideoView.getResources().getDrawable(R.drawable.play_btn_full, null));
            }
            isFull = true;
        }
        show();
    }

    //隐藏虚拟按键
    private void hiddenNavigation() {
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        ((Activity) mContext).getWindow().getDecorView().setSystemUiVisibility(uiOptions);
    }

    private void showNavigation() {
        int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
        ((Activity) mContext).getWindow().getDecorView().setSystemUiVisibility(uiOptions);
    }

    public void move(int left, int top, int width, int height) {
        FrameLayout.LayoutParams rllp = (FrameLayout.LayoutParams) mViewHolder.getLayoutParams();
        rllp.width = width;
        rllp.height = height;
        rllp.leftMargin = left;
        rllp.topMargin = top;
        mViewHolder.setLayoutParams(rllp);
    }

    public boolean isPlaying() {
        return mVideoView.isPlaying();
    }

    public void pause() {
        if (mVideoView.isPlaying()) {
            mVideoView.pause();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                playButton.setBackground(mVideoView.getResources().getDrawable(R.drawable.play_btn_play, null));
            }
        } else {
            mVideoView.start();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                playButton.setBackground(mVideoView.getResources().getDrawable(R.drawable.play_btn_pause, null));
            }
        }
    }

    private int allTime;
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.arg1) {
                case IMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START: {
                    int nowTime = mVideoView.getCurrentPosition();
                    seekBar.setSecondaryProgress(mVideoView.getBufferPercentage() * 100);
                    if (!mGesture.isScroll && mGesture.Touch != myGestureListener.TOUCH_X) {
                        playTime.setText(timeLengthToTime(nowTime));
                        int value = (int) Math.floor(((double) nowTime / (double) allTime) * 10000);
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

        private boolean isScroll = false;
        private int nowTime = 0;

        @Override
        public boolean onDown(MotionEvent event) {//手指按下
            if (isShowing()) {
                isClick = true;
                delay = 5000;
            }
            if (sharLayout.getVisibility() == View.VISIBLE) {
                sharLayout.setVisibility(View.INVISIBLE);
            }
            return true;
        }

        public static final int TOUCH_NULL = -1;//啥都没
        public static final int TOUCH_X = 0;//手指横向滑动
        public static final int TOUCH_LEFT_Y = 1;//手指左边纵向滑动
        public static final int TOUCH_RIGHT_Y = 2;//手指右边纵向滑动
        public int Touch = TOUCH_NULL;
        private int Progress = 0;//记录初始进度
        private int delay = 0;

        private void sleepHide(final View v) {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    if (delay > 0) {
                        if (Touch != TOUCH_NULL) {
                            delay = 2000;
                        } else {
                            delay -= 1000;
                        }
                        sleepHide(v);
                    } else {
                        v.setVisibility(View.INVISIBLE);
                    }
                }
            }, 1000);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {//手指在触摸屏上滑动
            if (Touch == TOUCH_NULL) {
                Touch = getTouch(e1.getX(), e1.getY(), e2.getX(), e2.getY());
                isClick = true;
                isScroll = true;
                if (Touch == TOUCH_X) {
                    Progress = seekBar.getProgress();
                    nowTime = mVideoView.getCurrentPosition();
                } else if (Touch == TOUCH_LEFT_Y) {
                    Progress = barLight.getProgress();
                    mViewLight.setVisibility(View.VISIBLE);
                    delay = 2000;
                    sleepHide(mViewLight);
                } else if (Touch == TOUCH_RIGHT_Y) {
                    AudioManager am = (AudioManager) ((Activity) mContext).getSystemService(Context.AUDIO_SERVICE);
                    barSound.setProgress(am.getStreamVolume(AudioManager.STREAM_MUSIC));
                    Progress = barSound.getProgress();
                    mViewSound.setVisibility(View.VISIBLE);
                    delay = 2000;
                    sleepHide(mViewSound);
                }
            } else if (Touch == TOUCH_X) {
                //从左到右滚一次90秒
                ViewGroup.LayoutParams rllp = mViewHolder.getLayoutParams();
                float time = (90 / (float) getWidth()) * (e1.getX() - e2.getX());
                int x = (int) Math.floor(time * (10000 / (allTime / 1000)));
                seekBar.setProgress((int) (Progress - (int) (x)));
                playTime.setText(timeLengthToTime((int) Math.floor((float) nowTime - time * 1000)));//计算时间
            } else if (Touch == TOUCH_LEFT_Y) {
                float x = (127.5f / (float) getHeight()) * (e1.getY() - e2.getY());
                barLight.setProgress((int) (Progress + (int) Math.floor(x)));
                setLight(Progress + (int) Math.floor(x));
            } else if (Touch == TOUCH_RIGHT_Y) {
                AudioManager am = (AudioManager) ((Activity) mContext).getSystemService(Context.AUDIO_SERVICE);
                float x = (am.getStreamMaxVolume(AudioManager.STREAM_MUSIC) / 2 / (float) getHeight()) * (e1.getY() - e2.getY());
                barSound.setProgress((int) (Progress + (int) Math.floor(x)));
                am.setStreamVolume(AudioManager.STREAM_MUSIC, (int) (Progress + (int) Math.floor(x)), AudioManager.FLAG_PLAY_SOUND);
            }
            return true;
        }

        private int getWidth() {
            ViewGroup.LayoutParams rllp = mViewHolder.getLayoutParams();
            if (rllp.width == -1) {
                Resources resources = mViewHolder.getResources();
                DisplayMetrics dm = resources.getDisplayMetrics();
                return dm.widthPixels;
            }
            return rllp.width;
        }

        private int getHeight() {
            ViewGroup.LayoutParams rllp = mViewHolder.getLayoutParams();
            if (rllp.height == -1) {
                Resources resources = mViewHolder.getResources();
                DisplayMetrics dm = resources.getDisplayMetrics();
                return dm.heightPixels;
            }
            return rllp.height;
        }

        private int getTouch(float x, float y, float lx, float ly) {
            if (Math.abs(y - ly) < 20) {//判断y,绝对值小于20则认为是横向滑动
                return TOUCH_X;
            } else if (Math.abs(y - ly) > 20) {
                Resources resources = mContext.getResources();
                DisplayMetrics dm = resources.getDisplayMetrics();
                if ((dm.widthPixels / 2) > x) {
                    return TOUCH_LEFT_Y;
                }
                return TOUCH_RIGHT_Y;
            }
            return TOUCH_NULL;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {//手指抬起
            Log.e(TAG, "手指抬起");
            isClick = false;
            isScroll = false;
            if (Touch != TOUCH_NULL) {
                if (Touch == TOUCH_X) {
                    int value = (int) Math.floor((double) allTime * ((double) seekBar.getProgress() / 10000));
                    mVideoView.seekTo(value);
                }
            }
            Touch = TOUCH_NULL;
            return true;
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

    public void seekTo(int msec) {
        mVideoView.seekTo(msec);
    }

    public void start() {
        mVideoView.start();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            playButton.setBackground(mVideoView.getResources().getDrawable(R.drawable.play_btn_pause, null));
        }
    }

    public int toggleAspectRatio(int aspect_ratio) {
        return mVideoView.toggleAspectRatio(aspect_ratio);
    }

    public void hidden() {
        delay = 0;
        mPlayTop.setVisibility(View.INVISIBLE);
        mPlayController.setVisibility(View.INVISIBLE);
        //全屏隐藏虚拟按键
        if (isFull) {
            hiddenNavigation();
        }
    }

    private int delay = 0;
    private boolean isClick = false;

    public void show() {
        show(5000);
    }

    public void show(int time) {
        if (isFull) {
            showNavigation();
        }
        mPlayTop.setVisibility(View.VISIBLE);
        mPlayController.setVisibility(View.VISIBLE);
        if (delay >= 0) {
            if (delay == 0) {
                delay = time;
                sleepHide();
            } else {
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
