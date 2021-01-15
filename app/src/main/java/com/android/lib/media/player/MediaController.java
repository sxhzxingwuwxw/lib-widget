package com.android.lib.media.player;

import android.content.Context;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.DrawableRes;

import com.android.lib.media.R;
import com.android.lib.media.utils.LibMediaUtils;
import com.xiao.nicevideoplayer.INiceVideoPlayer;
import com.xiao.nicevideoplayer.NiceUtil;
import com.xiao.nicevideoplayer.NiceVideoPlayer;
import com.xiao.nicevideoplayer.NiceVideoPlayerController;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by Mouse on 2017/10/31.
 */

public class MediaController extends NiceVideoPlayerController
        implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private final Context mContext;

    private ImageView mImage;
    private TextView mTitle;

    private RelativeLayout mControllerLayout;
    private ImageView mRestartPause;
    private TextView mCurTimeTextView;
    private TextView mAllTimeTextView;
    private SeekBar mSeek;
    private ImageView mFullScreen;

    private LinearLayout mLoading;
    private ImageView mLoadingProgress;
    private TextView mLoadText;

    private LinearLayout mCenterLayout;
    private TextView mCenterText;
    private ProgressBar mCenterProgress;
    private ImageView mCenterImg;

    private LinearLayout mError;
    private TextView mRetry;

    private boolean topBottomVisible;
    private CountDownTimer mDismissTopBottomCountDownTimer;

    private ImageView mMediaBack;

    private LinearLayout mVideoFinishCover;
    private TextView errorText;

    private int mCurrentMediaId;
    private long tryTime;

    public MediaController(Context context) {
        super(context);
        mContext = context;
        init();
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.view_media_controller, this, true);

        mVideoFinishCover = findViewById(R.id.video_finish_cover);
        mMediaBack = findViewById(R.id.media_back);
        mImage = findViewById(R.id.image);
        mTitle = findViewById(R.id.title);

        mControllerLayout = findViewById(R.id.controller_layout);
        mRestartPause = findViewById(R.id.media_start_btn);
        mCurTimeTextView = findViewById(R.id.media_cur_time);
        mAllTimeTextView = findViewById(R.id.media_all_time);
        mSeek = findViewById(R.id.seek);
        mFullScreen = findViewById(R.id.full_screen);

        mLoading = findViewById(R.id.loading);
        mLoadingProgress = findViewById(R.id.loading_progress);
        mLoadText = findViewById(R.id.load_text);

        mCenterLayout = findViewById(R.id.center_layout);
        mCenterText = findViewById(R.id.center_text);
        mCenterProgress = findViewById(R.id.center_progress);
        mCenterImg = findViewById(R.id.center_img);

        mError = findViewById(R.id.error);
        errorText = findViewById(R.id.error_text);
        mRetry = findViewById(R.id.retry);

        mMediaBack.setOnClickListener(this);
        mRestartPause.setOnClickListener(this);
        mFullScreen.setOnClickListener(this);
        mRetry.setOnClickListener(this);

        mSeek.setOnSeekBarChangeListener(this);
        this.setOnClickListener(this);
    }

    public void setTryTime(long tryTime){
        this.tryTime = tryTime * 1000;
    }

    @Override
    public void onClick(View v) {
        if (v == mMediaBack) {
            if (mNiceVideoPlayer.isFullScreen()) {
                mNiceVideoPlayer.exitFullScreen();
            } else if (mNiceVideoPlayer.isTinyWindow()) {
                mNiceVideoPlayer.exitTinyWindow();
            }
        } else if (v == mRestartPause) {
            if (mNiceVideoPlayer.isPlaying() || mNiceVideoPlayer.isBufferingPlaying()) {
                mNiceVideoPlayer.pause();
            } else if (mNiceVideoPlayer.isPaused() || mNiceVideoPlayer.isBufferingPaused()) {
                mNiceVideoPlayer.restart();
            }
        } else if (v == mFullScreen) {
            if (mNiceVideoPlayer.isNormal() || mNiceVideoPlayer.isTinyWindow()) {
                mNiceVideoPlayer.enterFullScreen();
            } else if (mNiceVideoPlayer.isFullScreen()) {
                mNiceVideoPlayer.exitFullScreen();
            }
        } else if (v == mRetry) {
            if (!TextUtils.isEmpty(this.text) && OnControllerListener != null) {
                OnControllerListener.onRetry(this.pid, this.id, this.time);
            } else {
                if (isNetWorkError) {
                    if (LibMediaUtils.isNetworkAvailable(mContext)) {
                        isNetWorkError = false;
                        mNiceVideoPlayer.restart();
                    }
                }else{
                    mNiceVideoPlayer.restart();
                }
            }
        } else if (v == this) {
            if (mNiceVideoPlayer.isPlaying()
                    || mNiceVideoPlayer.isPaused()
                    || mNiceVideoPlayer.isBufferingPlaying()
                    || mNiceVideoPlayer.isBufferingPaused()) {
                setTopBottomVisible(!topBottomVisible);
            }
        }
    }

    public boolean exit() {
        if (mNiceVideoPlayer != null && mNiceVideoPlayer.isFullScreen()) {
            mNiceVideoPlayer.exitFullScreen();
            return false;
        }
        return true;
    }

    @Override
    public void setTitle(String title) {
        mTitle.setText(title);
    }

    @Override
    public void setImage(@DrawableRes int res) {
        mImage.setImageResource(res);
    }

    @Override
    public ImageView imageView() {
        return mImage;
    }

    @Override
    public void setLenght(long length) {
        mAllTimeTextView.setText(NiceUtil.formatTime(length));
    }

    @Override
    public void setLenght(String length) {
        if (TextUtils.isEmpty(length)) return;
        mAllTimeTextView.setText(length);
    }

    @Override
    public void setNiceVideoPlayer(INiceVideoPlayer niceVideoPlayer) {
        super.setNiceVideoPlayer(niceVideoPlayer);
        if(niceVideoPlayer instanceof NiceVideoPlayer)
            ((NiceVideoPlayer)mNiceVideoPlayer).setOnCompletionListener(mOnCompletionListener);
    }

    public void showLoading() {
        showLoadLayout(true);
        mLoadText.setText("正在准备...");
        mVideoFinishCover.setVisibility(GONE);
    }

    private Animation rotateAnimation;

    private void showLoadLayout(boolean show) {
        if (show) {
            mLoading.setVisibility(VISIBLE);
            if (rotateAnimation == null) {
                rotateAnimation = new RotateAnimation(0, 359, Animation.RELATIVE_TO_SELF,
                        0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotateAnimation.setFillAfter(true);
                rotateAnimation.setRepeatCount(-1);
                rotateAnimation.setInterpolator(new LinearInterpolator());
                rotateAnimation.setDuration(5000);
            }
            mLoadingProgress.startAnimation(rotateAnimation);
        } else {
            mLoading.setVisibility(GONE);
            mLoadingProgress.clearAnimation();
        }
    }


    private int pid, id;
    private long time;
    private String text;

    public void showErrorText(String text, int pid, int id, long time) {
        this.pid = pid;
        this.id = id;
        this.time = time;
        this.text = text;
        if (!TextUtils.isEmpty(text)) {
            showErrorView();
            errorText.setText(text);
        }
    }

    private void showErrorView(){
        mError.setVisibility(VISIBLE);
        if (mNiceVideoPlayer.isFullScreen()) {
            mNiceVideoPlayer.exitFullScreen();
        }
    }

    @Override
    protected void onPlayStateChanged(int playState) {

        LibMediaUtils.i("playState:" + playState);
        switch (playState) {
            case NiceVideoPlayer.STATE_IDLE:
                break;
            case NiceVideoPlayer.STATE_PREPARING:
                mVideoFinishCover.setVisibility(GONE);
                showLoadLayout(true);
                mLoadText.setText("正在准备...");
                mError.setVisibility(View.GONE);
                break;
            case NiceVideoPlayer.STATE_PREPARED:
                mImage.setVisibility(View.GONE);
                NiceVideoPlayer niceVideoPlayer = ((NiceVideoPlayer)mNiceVideoPlayer);
                OnControllerListener.onMediaSizeChange(1f * niceVideoPlayer.getMediaPlayer().getVideoWidth(),
                        1f * niceVideoPlayer.getMediaPlayer().getVideoHeight());
                break;
            case NiceVideoPlayer.STATE_PLAYING:
                startUpdateProgressTimer();
                showLoadLayout(false);
                mRestartPause.setImageResource(R.drawable.ic_media_pause);
                startDismissTopBottomTimer();
                mError.setVisibility(View.GONE);
                break;
            case NiceVideoPlayer.STATE_PAUSED:
                showLoadLayout(false);
                mRestartPause.setImageResource(R.drawable.ic_media_play);
                cancelDismissTopBottomTimer();
                if (null != OnControllerListener) {
                    long position = mNiceVideoPlayer.getCurrentPosition();
                    if(mNiceVideoPlayer.getDuration() - mNiceVideoPlayer.getCurrentPosition()< 100)
                        position = mNiceVideoPlayer.getDuration() - 100;
                    OnControllerListener.onPauseListener(this.pid, this.id, position);
                }
                break;
            case NiceVideoPlayer.STATE_BUFFERING_PLAYING:
                showLoadLayout(true);
                mRestartPause.setImageResource(R.drawable.ic_media_pause);
                mLoadText.setText("正在缓冲...");
                startDismissTopBottomTimer();
                cancelUpdateProgressTimer();
                break;
            case NiceVideoPlayer.STATE_BUFFERING_PAUSED:
                showLoadLayout(true);
                mRestartPause.setImageResource(R.drawable.ic_media_play);
                mLoadText.setText("正在缓冲...");
                cancelDismissTopBottomTimer();
                cancelUpdateProgressTimer();
                break;
            case NiceVideoPlayer.STATE_ERROR:
                if (isNetWorkError) return;
                cancelUpdateProgressTimer();
                setTopBottomVisible(false);
                if (!LibMediaUtils.isNetworkAvailable(mContext)) {
                    errorText.setText("网络未连接，请检查网络设置");
                    isNetWorkError = true;
                    mNiceVideoPlayer.pause();
                } else {
                    isNetWorkError = false;
                    errorText.setText("播放失败，请重试。");
                }
                showErrorView();
                break;
            case NiceVideoPlayer.STATE_COMPLETED:
                cancelUpdateProgressTimer();
                setTopBottomVisible(false);
                mImage.setVisibility(View.VISIBLE);
                if (null != OnControllerListener) {
                    mNiceVideoPlayer.release();
                    OnControllerListener.onComplete(this.pid, this.id);
                }
                break;
        }
    }

    public void payFinish(){
        mError.setVisibility(GONE);
        mVideoFinishCover.setVisibility(View.GONE);
        tryTime = -1;
    }

    private boolean isNetWorkError = false;

    @Override
    protected void onPlayModeChanged(int playMode) {
        if (playMode == NiceVideoPlayer.MODE_NORMAL) {
            mMediaBack.setVisibility(View.GONE);
            mFullScreen.setImageResource(R.drawable.ic_media_screen);
        } else if (playMode == NiceVideoPlayer.MODE_FULL_SCREEN) {
            mMediaBack.setVisibility(View.VISIBLE);
            mFullScreen.setImageResource(R.drawable.ic_media_un_screen);
        } else if (playMode == NiceVideoPlayer.MODE_TINY_WINDOW) {
            mMediaBack.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void reset() {
        topBottomVisible = false;
        cancelUpdateProgressTimer();
        cancelDismissTopBottomTimer();
        mSeek.setProgress(0);
        mSeek.setSecondaryProgress(0);
        mImage.setVisibility(View.VISIBLE);
        mControllerLayout.setVisibility(View.GONE);
        mFullScreen.setImageResource(R.drawable.ic_player_enlarge);
        mMediaBack.setVisibility(View.GONE);
        showLoadLayout(false);
        mError.setVisibility(View.GONE);
        isNetWorkError = false;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        LibMediaUtils.i("onProgressChanged:" + i);
        mCurTimeTextView.setText(NiceUtil.formatTime(i));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        cancelDismissTopBottomTimer();
        isSeekBarUp = false;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        long position = seekBar.getProgress();
        if(tryTime >0 && position > tryTime){
            cancelUpdateProgressTimer();
            seekBar.setProgress((int) tryTime);
            showVideoFinishCover(true);
        }else {
            if (position > 1000 && position == mNiceVideoPlayer.getDuration())
                position = position - 1000;
            mNiceVideoPlayer.seekTo(position);
        }
        isSeekBarUp = true;
    }

    private boolean isSeekBarUp = true;

    @Override
    protected void updateProgress() {
        long position = mNiceVideoPlayer.getCurrentPosition();
        long duration = mNiceVideoPlayer.getDuration();
        int bufferPercentage = (int) (mNiceVideoPlayer.getBufferPercentage() * duration/100f);
        mSeek.setSecondaryProgress(bufferPercentage);
        if (isSeekBarUp)
            mSeek.setProgress((int) position);
        if (mSeek.getMax() == 0 || duration != mSeek.getMax()) {
            mAllTimeTextView.setText(NiceUtil.formatTime(duration));
            mSeek.setMax((int) duration);
        }
        if (null != OnControllerListener) {
            OnControllerListener.onProgressListener(position, duration);
        }
    }

    @Override
    protected void showChangePosition(long duration, int newPositionProgress) {
        /*long newPosition = (long) (duration * newPositionProgress / 100f);
        String text = NiceUtil.formatTime(newPosition);
        showCenterLayout(newPositionProgress, 0, text);
        Logger.i("show change position:" + newPosition + "   " + duration + "   " + newPositionProgress + "   " + text);
        mSeek.setProgress(newPositionProgress);
        mCurTimeTextView.setText(text);*/
    }

    @Override
    protected void hideChangePosition() {
        hideCenterLayout();
    }

    @Override
    protected void showChangeVolume(int newVolumeProgress) {
        showCenterLayout(newVolumeProgress, R.drawable.ic_palyer_volume, null);
    }

    @Override
    protected void hideChangeVolume() {
        hideCenterLayout();
    }

    @Override
    protected void showChangeBrightness(int newBrightness) {
        showCenterLayout(newBrightness, R.drawable.ic_palyer_brightness, null);
    }

    @Override
    protected void hideChangeBrightness() {
        hideCenterLayout();
    }

    private void hideCenterLayout() {
        mCenterLayout.setVisibility(View.GONE);
    }

    private void showCenterLayout(int progress, int imgRes, String text) {
        mCenterLayout.setVisibility(View.VISIBLE);
        if (imgRes != 0) {
            mCenterImg.setVisibility(VISIBLE);
            mCenterImg.setImageResource(imgRes);
        } else {
            mCenterImg.setVisibility(GONE);
        }

        if (!TextUtils.isEmpty(text)) {
            mCenterText.setVisibility(VISIBLE);
            mCenterText.setText(text);
        } else {
            mCenterText.setVisibility(GONE);
        }
        mCenterProgress.setProgress(progress);
    }

    /**
     * 开启top、bottom自动消失的timer
     */
    private void startDismissTopBottomTimer() {
        cancelDismissTopBottomTimer();
        if (mDismissTopBottomCountDownTimer == null) {
            mDismissTopBottomCountDownTimer = new CountDownTimer(8000, 8000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    setTopBottomVisible(false);
                }
            };
        }
        mDismissTopBottomCountDownTimer.start();
    }

    /**
     * 取消top、bottom自动消失的timer
     */
    private void cancelDismissTopBottomTimer() {
        if (mDismissTopBottomCountDownTimer != null) {
            mDismissTopBottomCountDownTimer.cancel();
        }
    }

    /**
     * 设置top、bottom的显示和隐藏
     *
     * @param visible true显示，false隐藏.
     */
    private void setTopBottomVisible(boolean visible) {
        mMediaBack.setVisibility(visible ? VISIBLE : GONE);
        mControllerLayout.setVisibility(visible ? VISIBLE : GONE);
        topBottomVisible = visible;
        if (visible) {
            if (!mNiceVideoPlayer.isPaused() && !mNiceVideoPlayer.isBufferingPaused()) {
                startDismissTopBottomTimer();
            }
        } else {
            cancelDismissTopBottomTimer();
        }
    }

    private OnControllerListener OnControllerListener;

    public interface OnControllerListener {
        void onProgressListener(long duration, long total);

        void onComplete(int pid, int id);

        void onPauseListener(int pid, int id, long duration);

        void onRetry(int pid, int id, long time);

        void onMediaSizeChange(float width, float height);
    }

    public void setOnControllerListener(OnControllerListener listener) {
        this.OnControllerListener = listener;
    }

    public void setCurrentMediaId(int id){
        mCurrentMediaId = id;
    }

    public boolean release(int id) {
        mVideoFinishCover.setVisibility(View.GONE);
        if(mCurrentMediaId != id) {
            mCurrentMediaId = id;
            mNiceVideoPlayer.pause();
            mNiceVideoPlayer.release();
            return true;
        }
        return false;
    }

    public void showVideoFinishCover(boolean isShow) {
        mVideoFinishCover.setVisibility(isShow ? View.VISIBLE : View.GONE);
        if (isShow) {
            mCurrentMediaId = -2;
            mNiceVideoPlayer.pause();
            //cancelUpdateProgressTimer();
            if (mNiceVideoPlayer.isFullScreen()) {
                mNiceVideoPlayer.exitFullScreen();
            }
        } else {
            mNiceVideoPlayer.release();
            mNiceVideoPlayer.start();
            //startUpdateProgressTimer();
        }
    }

    public void setVideoFinishCoverRestartListener() {
        findViewById(R.id.video_restart).setOnClickListener(view -> showVideoFinishCover(false));
    }

    private final IMediaPlayer.OnCompletionListener mOnCompletionListener = mp -> {
        LibMediaUtils.i("播放结束：" + (mNiceVideoPlayer.getDuration() - mNiceVideoPlayer.getCurrentPosition()));
        if (mNiceVideoPlayer.getDuration() - mNiceVideoPlayer.getCurrentPosition() > 2000) {
            setState(NiceVideoPlayer.STATE_ERROR);
        } else {
            setState(NiceVideoPlayer.STATE_COMPLETED);
            LibMediaUtils.i("onCompletion ——> STATE_COMPLETED");
            // 清除屏幕常亮
            setKeepScreenOn(false);
            release(0);
            resetList();
        }
    };

    private void setState(int state) {
        if (mNiceVideoPlayer instanceof NiceVideoPlayer)
            ((NiceVideoPlayer) mNiceVideoPlayer).setCurrentState(state);
    }

    public boolean setNetView(View.OnClickListener listener){
        View netView = findViewById(R.id.net_layout);
        netView.setVisibility(GONE);
        mError.setVisibility(View.GONE);
        if(!LibMediaUtils.isWifi(getContext())){
            netView.setVisibility(VISIBLE);
            findViewById(R.id.net_btn).setOnClickListener(v -> {
                netView.setVisibility(GONE);
                listener.onClick(v);
            });
            return true;
        }

        return false;
    }

    @Override
    protected void changePositionOnActionUp() {
    }
}
