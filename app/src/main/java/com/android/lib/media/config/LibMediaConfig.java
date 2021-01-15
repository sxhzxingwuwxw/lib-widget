package com.android.lib.media.config;

import android.app.Activity;
import android.content.Context;

import com.android.lib.media.player.PayInfoModel;

import java.util.HashMap;

/**
 * Created by weici on 2021/1/14.
 * Describe:
 */
public interface LibMediaConfig {
    String net(String url, HashMap<String, String> params);
    String Decrypt(String data, String key);
    String getErrorMsg(int code);

    void share(Activity activity, PayInfoModel infoModel);
    String buildCourseMedialUrl(String sign, int id);
}
