package com.android.lib.weici.widget;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class PagerViewLayoutAdapter<T> extends PagerAdapter {

    private final List<T> mList;

    public PagerViewLayoutAdapter(List<T> list){
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public @NotNull
    Object instantiateItem(@NotNull ViewGroup container, int position) {
        container.addView((View) mList.get(position));
        return mList.get(position);
    }

    @Override
    public void destroyItem(@NotNull ViewGroup container, int position, @NotNull Object object) {
        super.destroyItem(container, position, object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Override
    public int getItemPosition(@NotNull Object object) {
        return PagerViewLayoutAdapter.POSITION_NONE;
    }
}