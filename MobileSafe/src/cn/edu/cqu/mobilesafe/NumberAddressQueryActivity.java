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
		 * �����ı����������
		 */
		et_phone_number.addTextChangedListener(new TextWatcher() {
			
			/**
			 * ���ı������ı�ص�
			 */
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s != null && s.length() >= 3) {
					// ��ѯ���ݿ⣬����ʾ���
					String address = NumberQueryUtils.queryNumber(s.toString());
					tv_reslut.setText("��ʾ�����" + address);
				}
			}
			
			/**
			 * ���ı������ı�֮ǰ�ص�
			 */
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			/**
			 * ���ı������ı�֮��ص�
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
			Toast.makeText(this, "����Ϊ�ա�����", Toast.LENGTH_SHORT).show();
		}else {
			// ȡ���ݿ��ѯ
			// 1�������ѯ    2�����ز�ѯ
			String queryNumber = NumberQueryUtils.queryNumber(phone);
			tv_reslut.setText("��ʾ�����" + queryNumber);
		}
	}
}
