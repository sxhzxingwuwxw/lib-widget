package com.android.lib.weici.widget;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.android.lib.media.R;

public class PageStateView implements View.OnClickListener {
    private Activity mContext;
    private View mView;
    private TextView mErrorText;
    private ImageView mErrorImg;
    private ProgressBar mProgressBar;
    private Button mErrorButton;

    public PageStateView(Activity context, ViewGroup rootView, OnPageStateListener listener) {
        mListener = listener;
        mContext = context;
        mView = View.inflate(mContext, R.layout.widget_view_state_layout, null);
        mView.setVisibility(View.GONE);
        mErrorText = mView.findViewById(R.id.view_state_text);
        mErrorImg = mView.findViewById(R.id.view_state_img);
        mProgressBar = mView.findViewById(R.id.view_state_progress);
        mErrorButton = mView.findViewById(R.id.view_state_fail_text);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        mView.setLayoutParams(lp);
        if (rootView instanceof LinearLayout) rootView.addView(mView, 0);
        else rootView.addView(mView);
        mView.setOnClickListener(this);
    }

    public void showLoading() {
        mView.setVisibility(View.VISIBLE);
        mErrorImg.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        mErrorButton.setVisibility(View.INVISIBLE);
        mErrorText.setText("数据加载中...");
    }

    public void showErrorTranslate(String error, final int code) {
        showErrorView(error, code, true);
    }

    public void showError(String error, final int code) {
        showErrorView(error, code, false);
    }

    private void showErrorView(String error, final int code, boolean translate) {
        mView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        mErrorImg.setVisibility(View.VISIBLE);
        mErrorText.setText(error);

        //全部采用新样式
        //无网提示文案
        if (code == -2) {
            mErrorButton.setVisibility(View.VISIBLE);
            mErrorText.setText(">无法连接网络，请检查网络并刷新重试");
            mErrorImg.setImageResource(R.drawable.icon_no_net);
            mErrorButton.setOnClickListener(v -> {
                if (mListener != null) mListener.stateAction(code);
            });
        } else if (code == 201) {//无数据
            mErrorImg.setImageResource(R.drawable.icon_no_data_empty);
        }
        mErrorText.setTextColor(Color.parseColor(translate?"#ffffff":"#373737"));
        mView.setBackgroundColor(ContextCompat.getColor(mContext, translate? Color.TRANSPARENT:R.color.white));
    }

    public void showSuccess() {
        mView.setVisibility(View.GONE);
    }

    private OnPageStateListener mListener;

    @Override
    public void onClick(View v) {

    }

    public interface OnPageStateListener {
        void stateAction(int n);
    }
}
