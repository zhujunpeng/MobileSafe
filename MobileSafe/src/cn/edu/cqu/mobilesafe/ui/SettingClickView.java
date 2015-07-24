package cn.edu.cqu.mobilesafe.ui;

import cn.edu.cqu.mobilesafe.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;


/*
 * 自动以的组合控件
 * */
public class SettingClickView extends RelativeLayout {
	
	private TextView tv_desc,tv_title;
	private String desc_on;
	private String desc_off;
	
	// 把一个布局文件----》一个View，并且加载在SettingItemView中
	private void initView(Context context) {
		View view = View.inflate(getContext(), R.layout.setting_click_view, SettingClickView.this);
		tv_desc = (TextView) view.findViewById(R.id.tv_desc);
		tv_title = (TextView) view.findViewById(R.id.tv_title);
	}

	public SettingClickView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	public SettingClickView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
		
		String title = attrs.getAttributeValue("http://schemas.android.com/apk/res/cn.edu.cqu.mobilesafe", "title");
		desc_on = attrs.getAttributeValue("http://schemas.android.com/apk/res/cn.edu.cqu.mobilesafe", "desc_on");
		desc_off = attrs.getAttributeValue("http://schemas.android.com/apk/res/cn.edu.cqu.mobilesafe", "desc_off");
		tv_title.setText(title);
		setDesc(desc_off);
	}

	public SettingClickView(Context context) {
		super(context);
		initView(context);
	}

	
	
	/*
	 * 设置组合控件的状态
	 * */
	public void setChecked(boolean checked){
		if (checked) {
			setDesc(desc_off);
		}else {
			setDesc(desc_on);
		}
	}
	
	/*
	 * 组合控件更改文字
	 * */
	public void setDesc(String text){
		tv_desc.setText(text);
	}
	
	/*
	 * 组合控件更改标题
	 * */
	public void setTitle(String text){
		tv_title.setText(text);
	}
}
