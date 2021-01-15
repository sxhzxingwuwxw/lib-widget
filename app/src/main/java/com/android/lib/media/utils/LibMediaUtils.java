package com.android.lib.media.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

/**
 * Created by weici on 2021/1/14.
 * Describe:
 */
public class LibMediaUtils {

    @SuppressLint("MissingPermission")
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (NetworkInfo networkInfo : info) {
                    if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 判断当前网络是否是wifi
     */
    @SuppressLint("MissingPermission")
    public static boolean isWifi(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    public static void i(String msg) {
        Log.i("weiciLog", msg);
    }

    public static int dip2px(float dpValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * @param mss 单位是秒
     * @return 时分秒
     */
    public static String formatDateTime(long mss) {
        if (mss == 0) return "";
        String dateTimes = "";
        long days = mss / (60 * 60 * 24);
        long hours = (mss % (60 * 60 * 24)) / (60 * 60);
        long minutes = (mss % (60 * 60)) / 60;
        long seconds = mss % 60;
        if (days > 0) {
            dateTimes = days + "天";
        }
        if (hours > 0) {
            dateTimes += hours + "小时";
        }
        if (minutes > 0) {
            dateTimes += minutes + "分钟";
        }
        if (seconds > 0) {
            dateTimes += seconds + "秒";
        }

        return dateTimes;
    }

    public static void setTextView(View view, int id, String text) {
        TextView textView = view.findViewById(id);
        setTextView(textView, text);
    }

    public static void setTextView(TextView textView, String text) {
        if (TextUtils.isEmpty(text)) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.VISIBLE);
            textView.setText(text);
        }
    }

    public static void save(Context context, String key, long value){
        SharedPreferences sp = context.getSharedPreferences( "lib_media", Context.MODE_PRIVATE );
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong( key, value);
        editor.apply();
    }
    public static long get(Context context, String key){
        SharedPreferences sp = context.getSharedPreferences( "lib_media", Context.MODE_PRIVATE );
        return sp.getLong( key, 0);
    }

    public static boolean setPlayRecord(Context context, int courseId, int pid, int id, long time) {
        long t = getPlayRecord(context, courseId, pid, id);
        if(t == -1) return true;//学习完就不更新
        save(context, "pre_key_play_record_" + courseId + "_pid" + pid + "_id" + id, time);
        return false;
    }

    public static long getPlayRecord(Context context, int courseId, int pid, int id) {
        return get(context, "pre_key_play_record_" + courseId + "_pid" + pid + "_id" + id);
    }

    private static void onEvent(Context context, String id, HashMap<String, String> map){
        MobclickAgent.onEvent(context, id, map);
    }

    public static void onEvent(Context context, String id){
        MobclickAgent.onEvent(context, id);
    }

    //付费视频点击
    public static void reportMediaItem(Context context, String medialTitle, int id) {
        HashMap<String, String> map = new HashMap<>();
        map.put("title", medialTitle);
        map.put("mediaID", String.valueOf(id));
        onEvent(context, "media_item", map);
    }

    public static int[] getScreenSize(Context context) {
        DisplayMetrics metrics = getDisplayMetrics(context);
        return new int[]{metrics.widthPixels, metrics.heightPixels};
    }

    public static DisplayMetrics getDisplayMetrics(Context context) {
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metric);
        return metric;
    }

    /**
     * @param size     高宽比
     * @param cutWidth 需要裁剪的宽高比
     */
    public static void setViewSize(Context context, View view, float size, float cutWidth) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int width = (int) (dm.widthPixels - dm.widthPixels * cutWidth);
        int height = (int) (size * width);
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (lp == null) lp = new ViewGroup.LayoutParams(width, height);
        lp.width = width;
        lp.height = height;
        view.setLayoutParams(lp);
    }

    public static void setImageUrl(Context activity, ImageView imgView, String url, int defRes) {
        try {
            if (TextUtils.isEmpty(url)) {
                imgView.setImageResource(defRes);
                return;
            }
            Glide.with(activity).load(url).dontAnimate().placeholder(defRes).into(imgView);
        } catch (Exception e) {// 解决：Glide You cannot start a load for a destroyed activity

        }

    }
}
