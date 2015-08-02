package cn.edu.cqu.mobilesafe;

import cn.edu.cqu.mobilesafe.utils.NumberQueryUtils;
import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NumberAddressQueryActivity extends Activity {
	private EditText et_phone_number;
	private TextView tv_reslut;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_number_address);
		
		et_phone_number = (EditText) findViewById(R.id.et_phone_number);
		tv_reslut = (TextView) findViewById(R.id.tv_reslut);
		
		/**
		 * 监听文本的输入个数
		 */
		et_phone_number.addTextChangedListener(new TextWatcher() {
			
			/**
			 * 当文本发生改变回调
			 */
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s != null && s.length() >= 3) {
					// 查询数据库，并显示结果
					String address = NumberQueryUtils.queryNumber(s.toString());
					tv_reslut.setText("显示结果：" + address);
				}
			}
			
			/**
			 * 当文本发生改变之前回调
			 */
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			/**
			 * 当文本发生改变之后回调
			 */
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	public void numberQuery(View v){
		String phone = et_phone_number.getText().toString().trim();
		if (TextUtils.isEmpty(phone)) {
			Toast.makeText(this, "号码为空。。。", Toast.LENGTH_SHORT).show();
		}else {
			// 取数据库查询
			// 1、网络查询    2、本地查询
			String queryNumber = NumberQueryUtils.queryNumber(phone);
			tv_reslut.setText("显示结果：" + queryNumber);
		}
	}
}
