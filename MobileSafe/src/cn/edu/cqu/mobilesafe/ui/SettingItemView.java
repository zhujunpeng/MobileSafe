package cn.edu.cqu.mobilesafe.ui;

import cn.edu.cqu.mobilesafe.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;


/*
 * �Զ��Ե���Ͽؼ�
 * */
public class SettingItemView extends RelativeLayout {
	
	private CheckBox cb_status;
	private TextView tv_desc,tv_title;
	private String desc_on;
	private String desc_off;
	
	// ��һ�������ļ�----��һ��View�����Ҽ�����SettingItemView��
	private void initView(Context context) {
		View view = View.inflate(getContext(), R.layout.setting_item_view, SettingItemView.this);
		cb_status = (CheckBox) view.findViewById(R.id.cb_status);
		tv_desc = (TextView) view.findViewById(R.id.tv_desc);
		tv_title = (TextView) view.findViewById(R.id.tv_title);
	}

	public SettingItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	public SettingItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
		
		String title = attrs.getAttributeValue("http://schemas.android.com/apk/res/cn.edu.cqu.mobilesafe", "title");
		desc_on = attrs.getAttributeValue("http://schemas.android.com/apk/res/cn.edu.cqu.mobilesafe", "desc_on");
		desc_off = attrs.getAttributeValue("http://schemas.android.com/apk/res/cn.edu.cqu.mobilesafe", "desc_off");
		tv_title.setText(title);
		setDesc(desc_off);
	}

	public SettingItemView(Context context) {
		super(context);
		initView(context);
	}

	/*
	 * ��Ͽؼ��Ƿ��н���
	 * */
	public boolean isChecked(){
		return cb_status.isChecked();
	}
	
	/*
	 * ������Ͽؼ���״̬
	 * */
	public void setChecked(boolean checked){
		if (checked) {
			setDesc(desc_off);
		}else {
			setDesc(desc_on);
		}
		cb_status.setChecked(checked);
	}
	
	/*
	 * ��Ͽؼ���������
	 * */
	public void setDesc(String text){
		tv_desc.setText(text);
	}
	
	/*
	 * ��Ͽؼ����ı���
	 * */
	public void setTitle(String text){
		tv_title.setText(text);
	}
}
