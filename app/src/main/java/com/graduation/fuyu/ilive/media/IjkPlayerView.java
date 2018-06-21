package com.graduation.fuyu.ilive.media;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.graduation.fuyu.ilive.R;
import com.graduation.fuyu.ilive.danmaku.BiliDanmukuParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import master.flame.danmaku.controller.IDanmakuView;
import master.flame.danmaku.danmaku.loader.ILoader;
import master.flame.danmaku.danmaku.loader.IllegalDataException;
import master.flame.danmaku.danmaku.loader.android.DanmakuLoaderFactory;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.BaseCacheStuffer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.model.android.SpannedCacheStuffer;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.parser.IDataSource;
import master.flame.danmaku.danmaku.util.IOUtils;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * 封装ijkplayer播放器
 * Created by root on 18-2-19.
 */

public class IjkPlayerView extends FrameLayout implements View.OnClickListener{

    private AppCompatActivity mAttachActivity;
    private IjkVideoView mVideoView;
    //存储播放uri
    private String mVideoPath;
    private AudioManager mAudioManager;
    private AndroidMediaController mediaController;
    private int mMaxVolume;
    private ImageView mIvPlay;
    private ImageView mIvReplay;
    private ImageView mIvFullScreen;
    private RelativeLayout mButtonBar;

    // 无效变量
    private static final int INVALID_VALUE = -1;
    // 当前音量
    private int mCurVolume = INVALID_VALUE;
    // 当前亮度
    private float mCurBrightness = INVALID_VALUE;

    private Handler mHandler = new Handler();
    private GestureDetector mGestureDetector;
    private FrameLayout mFlVideoBox;

    public IjkPlayerView(@NonNull Context context) {
        this(context,null);
    }

    public IjkPlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        _initView(context);
    }

    private void _initView(Context context) {
        if(context instanceof AppCompatActivity){
            mAttachActivity=(AppCompatActivity) context;
        }else {
            throw new IllegalArgumentException("Context must be AppCompatActivity");
        }
        mAttachActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        View.inflate(context, R.layout.layout_player_view,this);
        mVideoView=findViewById(R.id.video_view);
        mFlVideoBox =findViewById(R.id.fl_video_box);
        mButtonBar=findViewById(R.id.ll_bottom_bar);
        mIvPlay=findViewById(R.id.iv_play);

        mIvPlay.setSelected(false);
        mIvReplay=findViewById(R.id.iv_replay);
        mIvFullScreen=findViewById(R.id.iv_fullscreen);

        //播放监听
        mIvPlay.setOnClickListener(this);
        //重新加载
        mIvReplay.setOnClickListener(this);
    }

    /**
     * 初始化ijkplayer播放器
     * @return
     */
    public IjkPlayerView init(){
        _initMediaPlayer();
        return this;
    }

    private void _initMediaPlayer() {
        // 加载 IjkMediaPlayer 库
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        //亮度
        try {
            int e= Settings.System.getInt(mAttachActivity.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS);
            float progress=1.0F*(float)e/255.0F;
            WindowManager.LayoutParams layoutParams=mAttachActivity.getWindow().getAttributes();
            layoutParams.screenBrightness=progress;
            mAttachActivity.getWindow().setAttributes(layoutParams);
        } catch (Settings.SettingNotFoundException e1) {
            e1.printStackTrace();
        }
        //声音
        mAudioManager = (AudioManager) mAttachActivity.getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = mAudioManager != null ? mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) : 0;
        // 触摸控制
        mGestureDetector = new GestureDetector(mAttachActivity, mPlayerGestureListener);
        mFlVideoBox.setClickable(true);
        mFlVideoBox.setOnTouchListener(mPlayerTouchListener);
    }


    /**
     * 设置播放资源
     *
     * @param uri
     * @return
     */
    public IjkPlayerView setVideoPath(Uri uri) {
        mVideoPath=uri.toString();
        mVideoView.setVideoURI(uri);
        return this;
    }

    /**
     * 开始播放
     * 开启屏幕长亮
     */
    public void start(){
        mIvPlay.setSelected(false);
        mVideoView.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {
                mVideoView.start();
                mIvPlay.setSelected(true);
            }
        });
        mAttachActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public  IjkPlayerView initDanmaku(){
        //初始化弹幕
        _initDanmaku();
        return this;
    }

    /**
     * 隐藏视图Runnable
     */
    private Runnable mHideBarRunnable = new Runnable() {
        @Override
        public void run() {
            mButtonBar.setVisibility(GONE);
        }
    };
    /**
     * 开关控制栏，单击界面的时候
     */
    private void _barControl() {
        mButtonBar.setVisibility(VISIBLE);
        mHandler.postDelayed(mHideBarRunnable,5000);
    }


    /**
     * 传回全屏按钮，在activity里处理
     * @return
     */
    public ImageView getFullScreenView(){
        return mIvFullScreen;
    }

    /**
     * 切换小屏播放
     */
    public void setSmallScreen(){
        _cancleUiLayoutFullscreen();
        mAttachActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * 切换全屏播放
     */
    public void setFullScreen(){
        _setUiLayoutFullscreen();
        mAttachActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    /**
     * 沉浸式UI
     */
    private void _setUiLayoutFullscreen() {
        View decorView = mAttachActivity.getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
        mAttachActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * 取消沉浸式UI
     */
    private void _cancleUiLayoutFullscreen(){
        View decorView = mAttachActivity.getWindow().getDecorView();
        int systemUiVisibility = decorView.getSystemUiVisibility();
        int flags = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        systemUiVisibility &= ~flags;
        mAttachActivity.getWindow().getDecorView().setSystemUiVisibility(systemUiVisibility);
    }

    /**========================================================= 触屏操作处理 ================================================*/

    /**
     * 手势监听
     */
    private GestureDetector.OnGestureListener mPlayerGestureListener=new GestureDetector.SimpleOnGestureListener(){

        // 是否是按下的标识，默认为其他动作，true为按下标识，false为其他动作
        private boolean isDownTouch;
        // 是否声音控制,默认为亮度控制，true为声音控制，false为亮度控制
        private boolean isVolume;
        // 是否横向滑动，默认为纵向滑动，true为横向滑动，false为纵向滑动
        private boolean isLandscape;
        @Override
        public boolean onDown(MotionEvent e) {
            isDownTouch=true;
            return super.onDown(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            _barControl();
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float mOldX = e1.getX(), mOldY = e1.getY();
            float deltaY = mOldY - e2.getY();
//            float deltaX = mOldX - e2.getX();
            if(isDownTouch){
                isLandscape = Math.abs(distanceX) >= Math.abs(distanceY);
                isVolume = mOldX > getResources().getDisplayMetrics().widthPixels * 0.5f;
                isDownTouch=false;
            }
            if(!isLandscape){
                float percent = deltaY / mVideoView.getHeight();
                if (isVolume) {
                    _onVolumeSlide(percent);
                } else {
                    _onBrightnessSlide(percent);
                }
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    };

    /**
     * 滑动改变声音大小
     *
     * @param percent
     */
    private void _onVolumeSlide(float percent) {
        mCurVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (mCurVolume < 0) {
            mCurVolume = 0;
        }
        int index = (int) (percent * mMaxVolume) + mCurVolume;
        if (index > mMaxVolume) {
            index = mMaxVolume;
        } else if (index < 0) {
            index = 0;
        }
        // 变更声音
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);
    }

    /**
     * 滑动改变亮度大小
     *
     * @param percent
     */
    private void _onBrightnessSlide(float percent) {
        mCurBrightness = mAttachActivity.getWindow().getAttributes().screenBrightness;
        if (mCurBrightness < 0.0f) {
            mCurBrightness = 0.5f;
        } else if (mCurBrightness < 0.01f) {
            mCurBrightness = 0.01f;
        }
        WindowManager.LayoutParams attributes = mAttachActivity.getWindow().getAttributes();
        attributes.screenBrightness = mCurBrightness + percent;
        if (attributes.screenBrightness > 1.0f) {
            attributes.screenBrightness = 1.0f;
        } else if (attributes.screenBrightness < 0.01f) {
            attributes.screenBrightness = 0.01f;
        }
        mAttachActivity.getWindow().setAttributes(attributes);
    }

    /**
    /**
     * 触摸事件监听
     */
    private OnTouchListener mPlayerTouchListener=new OnTouchListener() {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return mGestureDetector.onTouchEvent(motionEvent);
        }

    };


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_play:
                if (mIvPlay.isSelected()){
                    mIvPlay.setSelected(false);
                    mVideoView.pause();
                }else {
                    mIvPlay.setSelected(true);
                    mVideoView.reload();
                    mVideoView.start();
                }
                break;
            case R.id.iv_replay:
                mIvPlay.setSelected(false);
                mVideoView.setVideoPath(mVideoPath);
                mVideoView.setRender(IjkVideoView.RENDER_TEXTURE_VIEW);
                start();
                break;
        }
    }

    /**=============================================    弹幕     ===============================================================*/
    /**
     * 弹幕初始化
     */


    //弹幕库
    private IDanmakuView mDanmakuView;
    //发射按钮
    private Button mBtnSendDanmaku;
    //输入弹幕
    private TextView mBtnDanmakuText;
    private DanmakuContext mContext;
    //解析器
    private BaseDanmakuParser mParser;
    private void _initDanmaku() {
        mDanmakuView = findViewById(R.id.sv_danmaku);
        // 设置最大显示行数
        HashMap<Integer, Integer> maxLinesPair = new HashMap<Integer, Integer>();
        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 5); // 滚动弹幕最大显示5行
        // 设置是否禁止重叠
        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<Integer, Boolean>();
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);

        mContext = DanmakuContext.create();
        mContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 3)
                .setDuplicateMergingEnabled(false)
                .setScrollSpeedFactor(1.2f)
                .setScaleTextSize(1.2f)
                .setCacheStuffer(new SpannedCacheStuffer(), mCacheStufferAdapter) // 图文混排使用SpannedCacheStuffer
                .setMaximumLines(maxLinesPair)
                .preventOverlapping(overlappingEnablePair)
                .setDanmakuMargin(40);
        if (mDanmakuView != null) {
            mParser = createParser(this.getResources().openRawResource(R.raw.bili));
            mDanmakuView.setCallback(new master.flame.danmaku.controller.DrawHandler.Callback() {
                @Override
                public void updateTimer(DanmakuTimer timer) {
                }

                @Override
                public void drawingFinished() {

                }

                @Override
                public void danmakuShown(BaseDanmaku danmaku) {
                }

                @Override
                public void prepared() {
                    mDanmakuView.start();
                }
            });
            mDanmakuView.prepare(mParser, mContext);
            mDanmakuView.showFPS(false); //是否显示FPS
            mDanmakuView.enableDanmakuDrawingCache(true);
        }
    }
    private BaseDanmakuParser createParser(InputStream stream) {

        if (stream == null) {
            return new BaseDanmakuParser() {

                @Override
                protected Danmakus parse() {
                    return new Danmakus();
                }
            };
        }

        ILoader loader = DanmakuLoaderFactory.create(DanmakuLoaderFactory.TAG_BILI);

        try {
            loader.load(stream);
        } catch (IllegalDataException e) {
            e.printStackTrace();
        }
        BaseDanmakuParser parser = new BiliDanmukuParser();
        IDataSource<?> dataSource = loader.getDataSource();
        parser.load(dataSource);
        return parser;

    }
    /**
     * 添加文本弹幕
     * @param islive
     */
    public void addDanmaku(String text,boolean islive,boolean forself) {
        BaseDanmaku danmaku = mContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        if (danmaku == null || mDanmakuView == null) {
            return;
        }

        danmaku.text = text;
        danmaku.padding = 5;
        danmaku.priority = 1;  //0 表示可能会被各种过滤器过滤并隐藏显示 //1 表示一定会显示, 一般用于本机发送的弹幕
        danmaku.isLive = islive; //是否是直播弹幕
        danmaku.setTime(mDanmakuView.getCurrentTime() + 1200);
        danmaku.textSize = 25f * (mParser.getDisplayer().getDensity() - 0.6f);
        danmaku.textColor = Color.RED;
        danmaku.textShadowColor = Color.WHITE; //阴影/描边颜色
        if(forself){
            danmaku.borderColor = Color.GREEN; //边框颜色，0表示无边框
        }else {
            danmaku.borderColor=0;
        }
        mDanmakuView.addDanmaku(danmaku);

    }

    /**
     * 添加图文混排弹幕
     * @param islive
     */
    private void addDanmaKuShowTextAndImage(boolean islive) {
        BaseDanmaku danmaku = mContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        Drawable drawable = getResources().getDrawable(R.mipmap.ic_launcher);
        drawable.setBounds(0, 0, 100, 100);
        SpannableStringBuilder spannable = createSpannable(drawable);
        danmaku.text = spannable;
        danmaku.padding = 5;
        danmaku.priority = 1;  // 一定会显示, 一般用于本机发送的弹幕
        danmaku.isLive = islive;
        danmaku.setTime(mDanmakuView.getCurrentTime() + 1200);
        danmaku.textSize = 25f * (mParser.getDisplayer().getDensity() - 0.6f);
        danmaku.textColor = Color.RED;
        danmaku.textShadowColor = 0; // 重要：如果有图文混排，最好不要设置描边(设textShadowColor=0)，否则会进行两次复杂的绘制导致运行效率降低
        danmaku.underlineColor = Color.GREEN;
        mDanmakuView.addDanmaku(danmaku);
    }

    /**
     * 创建图文混排模式
     * @param drawable
     * @return
     */
    private SpannableStringBuilder createSpannable(Drawable drawable) {
        String text = "bitmap";
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);
        ImageSpan span = new ImageSpan(drawable);//ImageSpan.ALIGN_BOTTOM);
        spannableStringBuilder.setSpan(span, 0, text.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.append("图文混排");
        spannableStringBuilder.setSpan(new BackgroundColorSpan(Color.parseColor("#8A2233B1")), 0, spannableStringBuilder.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return spannableStringBuilder;
    }
    private BaseCacheStuffer.Proxy mCacheStufferAdapter = new BaseCacheStuffer.Proxy() {

        private Drawable mDrawable;

        /**
         * 在弹幕显示前使用新的text,使用新的text
         * @param danmaku
         * @param fromWorkerThread 是否在工作(非UI)线程,在true的情况下可以做一些耗时操作(例如更新Span的drawable或者其他IO操作)
         * @return 如果不需重置，直接返回danmaku.text
         */

        public void prepareDrawing(final BaseDanmaku danmaku, boolean fromWorkerThread) {
            if (danmaku.text instanceof Spanned) { // 根据你的条件检查是否需要需要更新弹幕
                // FIXME 这里只是简单启个线程来加载远程url图片，请使用你自己的异步线程池，最好加上你的缓存池
                new Thread() {

                    @Override
                    public void run() {
                        String url = "http://www.bilibili.com/favicon.ico";
                        InputStream inputStream = null;
                        Drawable drawable = mDrawable;
                        if (drawable == null) {
                            try {
                                URLConnection urlConnection = new URL(url).openConnection();
                                inputStream = urlConnection.getInputStream();
                                drawable = BitmapDrawable.createFromStream(inputStream, "bitmap");
                                mDrawable = drawable;
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                IOUtils.closeQuietly(inputStream);
                            }
                        }
                        if (drawable != null) {
                            drawable.setBounds(0, 0, 100, 100);
                            SpannableStringBuilder spannable = createSpannable(drawable);
                            danmaku.text = spannable;
                            if (mDanmakuView != null) {
                                mDanmakuView.invalidateDanmaku(danmaku, false);
                            }
                            return;
                        }
                    }
                }.start();
            }
        }

        @Override
        public void releaseResource(BaseDanmaku danmaku) {
            // TODO 重要:清理含有ImageSpan的text中的一些占用内存的资源 例如drawable
        }
    };






    /**=================================================外部调用借口=================================================================*/

    /**
     * Activity.onResume()
     */
    public void onResume(){
        mVideoView.resume();
    }

    /**
     * Activity.onPause()
     */
    public void onPause(){
        mVideoView.pause();
    }

    /**
     * Activity.onDestroy()
     */
    public void onDestroy(){
        mVideoView.destroy();
        IjkMediaPlayer.native_profileEnd();
        if (mDanmakuView != null) {
            // don't forget release!
            mDanmakuView.release();
            mDanmakuView = null;
        }
        // 关闭屏幕常亮
        mAttachActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }


    /**
     *回退按键处理
     * Activity.onBackPressed()
     * @return
     */
    public void onBackPressed(){
        if (mDanmakuView != null) {
            // don't forget release!
            mDanmakuView.release();
            mDanmakuView = null;
        }
        mAttachActivity.finish();
    }
}
