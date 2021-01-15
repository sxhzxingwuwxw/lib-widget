package com.android.lib.weici.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

public class TabView extends androidx.appcompat.widget.AppCompatTextView {

	private int position;

	int colorSelected = Color.BLUE;
	int colorDefault = Color.GRAY;

	public TabView(Context context) {
		super(context);
	}
	
	public TabView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	

	public void select(boolean isSelected){
		setTextColor(isSelected?colorSelected:colorDefault);
	}

	public void setPosition(int position){
		this.position = position;
	}
	
	public int getPosition(){
		return position;
	}

	public void setColorSelected(int color){
		this.colorSelected = color;
	}

	public void setColorDefault(int colorDefault) {
		this.colorDefault = colorDefault;
	}
}
