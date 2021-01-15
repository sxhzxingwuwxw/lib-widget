package com.android.lib.weici.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

public class WViewPager extends ViewPager {
	private boolean isScrollable = true;

	public WViewPager(Context context, AttributeSet attrs){
		super(context, attrs);
	}

	public WViewPager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (!isScrollable) {
			return false;
		} else {
			return super.onTouchEvent(ev);
		}

	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (!isScrollable) {
			return false;
		} else {
			return super.onInterceptTouchEvent(ev);
		}
	}

	public void setScrollable(boolean scrollable) {
		isScrollable = scrollable;
	}
}
