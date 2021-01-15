package com.android.lib.weici.widget.utils;

import android.content.res.Resources;

/**
 * Created by weici on 2021/1/14.
 * Describe:
 */
public class Tools {
    public static int dip2px(float dpValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
