package com.android.lib.weici.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;

import com.android.lib.media.R;


/**
 * 数据操作状态对话框
 * Created by Mouse on 2017/10/28.
 */

public class LoadingDialog extends Dialog implements LoadDialogView.OnFinishListener {

    private LoadDialogView mLoadView;
    private String text;
    private TextView textView;

    public LoadingDialog(@NonNull Context context) {
        this(context, R.style.customDialog);
    }

    public LoadingDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    @Override
    public void show() {
        try {
            super.show();
            mLoadView.showLoading();
        }catch (Exception ignored){}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_vui_loading);
        setCanceledOnTouchOutside(false);
        textView = findViewById(R.id.text);
        mLoadView = findViewById(R.id.loading_img);
        if(!TextUtils.isEmpty(text)){
            textView.setText(text);
        }
        mLoadView.setOnFinishListener(this);
    }

    public LoadingDialog setText(String text){
        this.text = text;
        if(null!=textView){
            textView.setText(text);
        }
        return this;
    }

    public void dismiss(String text, boolean success){
        if(mLoadView != null && isShowing()){
            textView.setText(text);
            if(success) mLoadView.showSuccess();
            else mLoadView.showError();
        }
    }

    public void dismiss(String text, boolean success, OnDialogDismissListener listener){
        mOnDialogDismissListener = listener;
        if(mLoadView != null && isShowing()){
            textView.setText(text);
            if(success) mLoadView.showSuccess();
            else mLoadView.showError();
        }
    }

    @Override
    public void dismiss() {
        mLoadView.dismiss();
        super.dismiss();
    }

    @Override
    public void viewDismiss() {
        mLoadView.postDelayed(new Runnable() {
            @Override
            public void run() {
                textView.setText("正在加载");
                dismiss();
                mLoadView.dismiss();
                if(mOnDialogDismissListener != null) mOnDialogDismissListener.onDialogDismiss();
            }
        }, 800);
    }

    private OnDialogDismissListener mOnDialogDismissListener;
    public interface OnDialogDismissListener{
        void onDialogDismiss();
    }
}
