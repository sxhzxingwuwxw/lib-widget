package com.android.lib.media;

import android.content.Context;

import com.android.lib.media.config.LibMedia;
import com.android.lib.media.net.ParamUtils;
import com.android.lib.media.net.Request;
import com.android.lib.media.net.Result;
import com.android.lib.media.player.PayInfoApi;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by weici on 2021/1/14.
 * Describe:
 */
public class MediaPresenter {
    private final IMediaDetailView view;
    private final Context context;

    public MediaPresenter(Context context, IMediaDetailView view) {
        this.view = view;
        this.context = context;
    }

    public void loadData(int courseId){
        view.showLoadingState();
        new Request().execute(context, ParamUtils.buildCourseListParam(courseId), new Request.OnCallBackListListener() {
            @Override
            public void callBack(Result result) {
                if (result.code != 200 || result.object == null) {
                    view.showFailureState(LibMedia.config.getErrorMsg(result.code));
                }else{
                    view.showSuccessState();
                    view.loadDataSuccess(result);
                }
            }
        });
    }

    public void getPermit(int pid, int id, long time){
        new Request().execute(context, ParamUtils.buildCoursePermitParam(), new Request.OnCallBackListListener() {
            @Override
            public void callBack(Result result) {
                view.playMedia(result, pid, id, time);
            }
        });
    }

    //点拨课程android微信支付统一下单
    public void pay(int courseId){
        view.showLoadingDialog();
        new Request().execute(context, ParamUtils.buildPayParam(context, courseId), new Request.OnCallBackListListener() {
            @Override
            public void callBack(Result result) {
                if (result.code == 200) {
                    PayInfoApi info = (PayInfoApi) result.object;
                    if (info == null) {
                        view.dismissLoadingDialog(true, "已免费");
                    } else {
                        view.dismissLoadingDialog();
                        view.payLoadSuccess((PayInfoApi) result.object);
                    }
                }else{
                    view.dismissLoadingDialog(false, LibMedia.config.getErrorMsg(result.code));
                }
            }
        });
    }

    public void payFinish(String orderCode){
        view.showLoadingDialog();
        new Request().execute(context, ParamUtils.buildPayFinishParam(orderCode), new Request.OnCallBackListListener() {
            @Override
            public void callBack(Result result) {
                if(result.code == 200){
                    view.paySuccess();
                    view.dismissLoadingDialog(true, "付款成功");
                }else{
                    view.dismissLoadingDialog(false, LibMedia.config.getErrorMsg(result.code));
                    MobclickAgent.reportError(context, new Exception("完成订单异常：" + result.code));
                }
            }
        });
    }
}
