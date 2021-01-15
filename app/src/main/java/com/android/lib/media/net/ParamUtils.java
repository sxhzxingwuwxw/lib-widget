package com.android.lib.media.net;

import android.content.Context;

import com.android.lib.media.config.LibMedia;
import com.android.lib.media.player.PayInfoApi;
import com.android.lib.media.utils.LibMediaUtils;
import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by weici on 2021/1/14.
 * Describe:
 */
public class ParamUtils {
    /**
     * 3.5.2   1.2 点播课程详情
     */
    public static String COURSE_DETAIL = "/gaozhong/weici/course/ondemand/course/detail";

    /**
     * 3.5.2   1.3 获取点播课程播放许可
     */
    public static String COURSE_PERMIT = "/gaozhong/weici/course/ondemand/course/permit";
    public static String COURSE_MEDIA_URL = "/gaozhong/weici/course/ondemand/course/content";
    public static String COURSE_MEDIA_PERMIT = "/gaozhong/weici/course/ondemand/course/permit";
    /**
     * 3.5.2 付费
     */
    public static String COURSE_PAY_START = "/androidpay/pay/ondemand/course";
    public static String COURSE_PAY_END = "/androidpay/pay/ondemand/course/finish";

    public static ParamsBody buildCourseListParam(int course_id) {
        ParamsBody body = new ParamsBody() {
            @Override
            public void parse(Result result, JSONObject json) {
                if (result.code == 200 && json != null) {
                    String data = json.optString("data");
                    try {
                        data = LibMedia.config.Decrypt(data, "ac14c13680bdf7a0");
                        result.object = new JSONObject(data);
                        LibMediaUtils.i("data:" + data);
                        return;
                    } catch (Exception ignored) {
                    }
                }
                result.code = 201;
            }
        };
        body.URL = COURSE_DETAIL;
        body.params.put("course_id", String.valueOf(course_id));
        return body;
    }

    public static ParamsBody buildCoursePermitParam() {
        ParamsBody apiBody = new ParamsBody() {
            @Override
            public void parse(Result result, JSONObject json) {
                if (result.code == 200 && json != null) {
                    result.object = json.optString("permit");
                }
            }
        };
        apiBody.URL = COURSE_MEDIA_PERMIT;
        return apiBody;
    }

    public static ParamsBody buildPayParam(Context context, int courseId) {
        ParamsBody apiBody = new ParamsBody() {
            @Override
            public void parse(Result result, JSONObject json) {
                if (result.code == 200) {
                    double money = json.optDouble("money", 0f);
                    if (money != 0) {
                        result.object = new Gson().fromJson(result.text, PayInfoApi.class);
                    }
                }else{
                    MobclickAgent.reportError(context, new Exception("获取订单异常：" + result.code));
                }
            }
        };
        apiBody.URL = COURSE_PAY_START;
        apiBody.params.put("course_id", String.valueOf(courseId));
        return apiBody;
    }

    public static ParamsBody buildPayFinishParam(String orderCode) {
        ParamsBody apiBody = new ParamsBody();
        apiBody.URL = COURSE_PAY_END;
        apiBody.params.put("order_code", orderCode);
        return apiBody;
    }

}
