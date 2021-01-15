package com.android.lib.weici.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.android.lib.media.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weici on 2017/11/6.
 */

public class TabViewLayout<T> extends LinearLayout implements ViewPager.OnPageChangeListener {

    private WViewPager mViewPager;
    private TabViewGroup mTabHost;
    private final List<T> mList = new ArrayList<>();
    private final List<String> mTitle = new ArrayList<>();

    public TabViewLayout(Context context) {
        super(context);
        initView();
    }

    public void setScrollable(boolean able) {
        mViewPager.setScrollable(able);
    }

    public TabViewLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public TabViewLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        setOrientation(LinearLayout.VERTICAL);
        inflate(getContext(), R.layout.view_tab_view_layout, this);
        setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));

        mViewPager = findViewById(R.id.viewpaper);
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setOffscreenPageLimit(4);
    }

    public void setDefaultColor(int color){
        if(mTabHost != null) mTabHost.setDefaultColor(color);
    }

    public void setTextSize(int size){
        if(mTabHost != null) mTabHost.setTextSize(size);
    }

    private void initPage() {
        UnderlinePageIndicator indicator = findViewById(R.id.indicator);
        mTabHost = findViewById(R.id.tabhost);

        if (mTitle.size() == 1) {
            indicator.setVisibility(GONE);
            mTabHost.setVisibility(GONE);
            return;
        }
        float w = mTabHost.setData(mTitle);
        mTabHost.select(0);
        indicator.setLineWidth(w);
        mTabHost.setOnTabButtonClickListener(new TabViewGroup.OnTabButtonClickListener() {

            @Override
            public void onTabButtonClickListener(int i) {
                mViewPager.setCurrentItem(i);
            }
        });

        indicator.setViewPager(mViewPager);
        indicator.setSelectedColor(Color.parseColor("#31af00"));
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                mTabHost.select(arg0);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
        indicator.setFades(false);

    }

    public void addData(T t, String title){
        mList.add(t);
        mTitle.add(title);
    }

    public void notifyView(){
        PagerViewLayoutAdapter<T> mPagerAdapter = new PagerViewLayoutAdapter<>(mList);
        mViewPager.setAdapter(mPagerAdapter);
        initPage();
    }

    public void setTabTitle(int pos, String title) {
        mTabHost.setTitle(pos, title);
    }

    public void setCurrentItem(int pos) {
        if (mViewPager.getCurrentItem() == pos) return;
        mViewPager.setCurrentItem(pos);
    }

    public int getCurrentItem() {
        if (mViewPager == null) return 0;
        return mViewPager.getCurrentItem();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (mListener != null) mListener.onPageSelected(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private OnTabViewChangeListener mListener;

    public void setOnTabViewChangeListener(OnTabViewChangeListener listener) {
        mListener = listener;
    }

    public interface OnTabViewChangeListener {
        void onPageSelected(int position);
    }

    public void reSet() {
        if (mTabHost != null)
            mTabHost.removeAllViews();
    }
}
