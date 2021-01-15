package com.android.lib.media;

import com.android.lib.media.net.Result;
import com.android.lib.media.player.PayInfoApi;

/**
 * Created by weici on 2020/12/24.
 * Describe:
 */
public interface IMediaDetailView {
    void showLoadingState();
    void showSuccessState();
    void showFailureState(String text);
    void showLoadingDialog();
    void dismissLoadingDialog();
    void dismissLoadingDialog(boolean success, String msg);



    void loadDataSuccess(Result result);
    void playMedia(Result result, int pid, int id, long time);
    void payLoadSuccess(PayInfoApi info);
    void paySuccess();
}
