package cn.edu.cqu.mobilesafe.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewDebug.ExportedProperty;
import android.widget.TextView;

/*
 * 自定义的TextView，有焦点
 * */
public class FocuseTextView extends TextView {

	public FocuseTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		
	}

	public FocuseTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
	}
	public FocuseTextView(Context context) {
		super(context);
		
	}
	
	
	/**
	 * 当前并没有焦点，我只是欺骗Android系统
	 * */
	@Override
	@ExportedProperty(category = "focus")
	public boolean isFocused() {
		return true;
	}

}
