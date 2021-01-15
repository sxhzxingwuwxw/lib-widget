package com.android.lib.media.config;

/**
 * Created by weici on 2021/1/14.
 * Describe:
 */
public class PayComplete {
    public String orderCode;
    public boolean isSuccess;

    public PayComplete(String orderCode, boolean isSuccess) {
        this.orderCode = orderCode;
        this.isSuccess = isSuccess;
    }
}
