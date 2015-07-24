package cn.edu.cqu.mobilesafe.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewDebug.ExportedProperty;
import android.widget.TextView;

/*
 * �Զ����TextView���н���
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
	 * ��ǰ��û�н��㣬��ֻ����ƭAndroidϵͳ
	 * */
	@Override
	@ExportedProperty(category = "focus")
	public boolean isFocused() {
		return true;
	}

}
