package com.android.lib.media;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.lib.media.config.LibMedia;
import com.android.lib.media.config.PayComplete;
import com.android.lib.media.net.Result;
import com.android.lib.media.player.MediaController;
import com.android.lib.media.player.PayInfoApi;
import com.android.lib.media.player.PayInfoModel;
import com.android.lib.media.player.ViewMediaStudyList;
import com.android.lib.media.utils.LibMediaUtils;
import com.android.lib.weici.widget.LoadingDialog;
import com.android.lib.weici.widget.PageStateView;
import com.android.lib.weici.widget.TabViewLayout;
import com.android.lib.weici.widget.WWebView;
import com.google.gson.Gson;
import com.xiao.nicevideoplayer.NiceVideoPlayer;
import com.xiao.nicevideoplayer.NiceVideoPlayerManager;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

/**
 * Created by weici on 2021/1/14.
 * Describe:
 */
public class MediaActivity extends Activity implements ViewMediaStudyList.OnItemClickListener,
        MediaController.OnControllerListener, View.OnClickListener, IMediaDetailView, PageStateView.OnPageStateListener {

    private MediaPresenter mPresenter;
    private NiceVideoPlayer mNiceVideoPlayer;
    private long tryTime;

    private MediaController controller;
    private PayInfoModel model;
    private ViewMediaStudyList studyList;

    private LoadingDialog mLoadingDialog;
    private PageStateView mPage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_detail_layout);
        mPage = new PageStateView(this, findViewById(R.id.root_layout), this);
        getData();

    }

    protected MediaPresenter getPresenter() {
        if(mPresenter == null) mPresenter = new MediaPresenter(this, this);
        return mPresenter;
    }

    private void getData() {
        LibMediaUtils.onEvent(this, "media_index");
        int courseId = getIntent().getIntExtra("courseId", 0);
        getPresenter().loadData(courseId);
    }

    private void setView(JSONObject json) {
        model = (PayInfoModel) new Gson().fromJson(json.toString(), PayInfoModel.class);

        TextView tvOldPrice = findViewById(R.id.tv_old_price);
        tvOldPrice.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG| Paint.ANTI_ALIAS_FLAG);
        tvOldPrice.setText(String.valueOf(model.getCourseOldPrice()));
        TextView tvNewPrice = findViewById(R.id.tv_new_price);
        tvNewPrice.setText(String.valueOf(model.getCoursePrice()));
        findViewById(R.id.buy_btn).setOnClickListener(this);

        findViewById(R.id.buy_layout).setVisibility(model.isPay() ? View.GONE : View.VISIBLE);

        mNiceVideoPlayer = findViewById(R.id.nice_player);
        mNiceVideoPlayer.setPlayerType(NiceVideoPlayer.TYPE_IJK);
        mNiceVideoPlayer.continueFromLastPosition(false);
        controller = new MediaController(this);
        controller.setOnControllerListener(this);
        controller.setVideoFinishCoverRestartListener();
        controller.setNiceVideoPlayer(mNiceVideoPlayer);
        mNiceVideoPlayer.setController(controller);

        mNiceVideoPlayer.setEnabled(false);
        setDefaultSize();
        LibMediaUtils.setImageUrl(this, controller.imageView(), model.getCourseTopImage(), R.drawable.bg_image_default);

        TabViewLayout<View> tabLayout = findViewById(R.id.tab_view);
        studyList = new ViewMediaStudyList(this);
        studyList.setData(json.optString("course_info"), model);
        studyList.setOnItemClickListener(this);
        tabLayout.addData(studyList, "课程目录");
        tabLayout.addData(getWebView(json.optString("course_synopsis")), "课程介绍");
        tabLayout.notifyView();
        tabLayout.setDefaultColor(Color.parseColor("#1a1a1a"));
        tabLayout.setTextSize(18);

        // 返回
        findViewById(R.id.back).setOnClickListener(this);

        if (TextUtils.isEmpty(model.getShareTitle())) {
            findViewById(R.id.share_view).setVisibility(View.GONE);
        } else {
            findViewById(R.id.share_view).setOnClickListener(this);
        }
    }

    private WWebView getWebView(String url) {
        WWebView webView = new WWebView(this);
        webView.loadUrl(url);
        return webView;
    }

    private void setDefaultSize() {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mNiceVideoPlayer.getLayoutParams();
        int[] screenSize = LibMediaUtils.getScreenSize(this);
        lp.height = screenSize[0] * 10 / 16;

        ImageView imageView = controller.imageView();
        ViewGroup.LayoutParams ilp = imageView.getLayoutParams();
        ilp.width = lp.width;
        ilp.height = lp.height;
    }

    private void setViewSize() {
        View priceLayout = findViewById(R.id.price_layout);
        View buyBtn = findViewById(R.id.buy_btn);
        View buy_layout = findViewById(R.id.buy_layout);

        LibMediaUtils.setViewSize(this, buy_layout, 195f / 1110f, 5f / 375f);
        LibMediaUtils.setViewSize(this, priceLayout, 45f / 209.75f, 165f / 375f);
        LibMediaUtils.setViewSize(this, buyBtn, 45f / 150f, 225f / 375f);
    }

    @Override
    public void onItemClick(int pid, int id, long time, boolean retry) {
        if(!controller.release(id) && !retry) return;
        this.tryTime = time * 1000;
        if (time < 0 && !model.isPay()) {
            Toast.makeText(this, "购买后即可观看全部内容哦~", Toast.LENGTH_LONG).show();
            return;
        }

        controller.showLoading();
        if (model.isPay()) {
            studyList.updateItemDuration();
        }
        getPresenter().getPermit(pid, id, time);
    }

    @Override
    public void onRetry(int pid, int id, long time) {
        onItemClick(pid, id, time, true);
    }

    @Override
    public void onMediaSizeChange(float width, float height) {
        if(width ==0 || height == 0) return;
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mNiceVideoPlayer.getLayoutParams();
        int[] screenSize = LibMediaUtils.getScreenSize(this);
        lp.width = screenSize[0];
        lp.height = (int) (screenSize[0] * height / width);
        mNiceVideoPlayer.setLayoutParams(lp);

        ImageView imageView = controller.imageView();
        ViewGroup.LayoutParams ilp = imageView.getLayoutParams();
        ilp.width = lp.width;
        ilp.height = lp.height;
    }

    @Override
    public void finish() {
        super.finish();
        if (null != mNiceVideoPlayer && mNiceVideoPlayer.isPlaying()) {
            mNiceVideoPlayer.pause();
        }
        destroy();
    }

    @Override
    protected void onPause() {
        if(mNiceVideoPlayer != null) mNiceVideoPlayer.pause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroy();
    }

    private void destroy(){
        if (null != mNiceVideoPlayer) {
            NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
            NiceVideoPlayerManager.instance().destroy();
            mNiceVideoPlayer.destroy();
            mNiceVideoPlayer = null;
        }
    }

    @Override
    public void onProgressListener(long duration, long total) {
        if (model.isPay()) {
            return;
        }
        if (tryTime > 0 && duration >= tryTime && !model.isPay()) {
            controller.showVideoFinishCover(true);
        }
    }

    @Override
    public void onComplete(int pid, int id) {
        if (!model.isPay()) {
            return;
        }
        LibMediaUtils.setPlayRecord(this, model.getCourseId(), pid, id, -1);
        studyList.onPlayCompleteListener(pid, id, -1000);
        LibMediaUtils.i("播放完成");
    }

    @Override
    public void onPauseListener(int pid, int id, long duration) {
        if (!model.isPay()) {
            return;
        }
        LibMediaUtils.setPlayRecord(this, model.getCourseId(), pid, id, duration);
        LibMediaUtils.i("播放暂停");
    }

    /**
     * 支付完成
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(PayComplete msg) {
        if (msg.isSuccess) {
            getPresenter().payFinish(msg.orderCode);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Tencent.onActivityResultData(requestCode, resultCode, data, new ShareLogicImpl(this).mQQlistener);
    }

    @Override
    public void onBackPressed() {
        if(controller == null || controller.exit())
            super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.back){
            if(controller == null || controller.exit()) finish();
        }else if(v.getId() == R.id.share_view){
            //DialogFactory.showShareDialog(getActivity(), ShareBody.getShareData(model), null, false, WorkType.OTHER);
            LibMedia.config.share(this, model);
        } else if(v.getId() == R.id.buy_btn){
            LibMediaUtils.onEvent(this, "media_buy_count");
            getPresenter().pay(model.getCourseId());
        }
    }

    @Override
    public void loadDataSuccess(Result result) {
        setView((JSONObject) result.object);
        showSuccessState();
        findViewById(R.id.title_layout).setVisibility(View.GONE);
        setViewSize();
    }

    @Override
    public void playMedia(Result result, int pid, int id, long time) {
        if (result.object != null) {
            String url = LibMedia.config.buildCourseMedialUrl((String) result.object, id);
            LibMediaUtils.i("url:" + url);

            View.OnClickListener listener = v -> start(url, pid, id, time);
            if(!controller.setNetView(listener)){
                start(url, pid, id, time);
            }
        }else{
            String text = LibMedia.config.getErrorMsg(result.code);
            if(result.code == -2) text = "网络未连接，请检查网络设置";
            controller.showErrorText(text, pid, id, time);
        }
    }

    private void start(String url, int pid, int id, long time){
        controller.setTryTime(model.isPay()?-1:time);
        controller.showErrorText(null, pid, id, 0);
        mNiceVideoPlayer.setUp(url, null);
        long lastPlayPosition = LibMediaUtils.getPlayRecord(this, model.getCourseId(), pid, id);
        LibMediaUtils.i("播放位置：" + lastPlayPosition);
        mNiceVideoPlayer.start(lastPlayPosition);
    }

    @Override
    public void payLoadSuccess(PayInfoApi info) {
        info.pay(this);
    }

    @Override
    public void paySuccess() {
        controller.payFinish();
        controller.setCurrentMediaId(-1);
        findViewById(R.id.buy_layout).setVisibility(View.GONE);
        model.setPayFlag(1);
        studyList.onPayChangeListener();
    }

    private LoadingDialog getLoadingDialog(){
        if(mLoadingDialog == null) mLoadingDialog = new LoadingDialog(this);
        return mLoadingDialog;
    }

    @Override
    public void showLoadingState() {
        mPage.showLoading();
    }

    @Override
    public void showSuccessState() {
        mPage.showSuccess();
    }

    @Override
    public void showFailureState(String text) {
        mPage.showError(text, -1);
    }

    @Override
    public void showLoadingDialog() {
        getLoadingDialog().show();
    }

    @Override
    public void dismissLoadingDialog() {
        getLoadingDialog().dismiss();
    }

    @Override
    public void dismissLoadingDialog(boolean success, String msg) {
        getLoadingDialog().dismiss(msg, success);
    }

    @Override
    public void stateAction(int n) {
        getData();
    }
}
