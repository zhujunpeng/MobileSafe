package cn.edu.cqu.mobilesafe;

import cn.edu.cqu.mobilesafe.utils.SmsUtils;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class AToolsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atools);
	}
	/**
	 * ��ѯ���������
	 * @param v
	 */
	public void numberQuery(View v){
		startActivity(new Intent(this, NumberAddressQueryActivity.class));
	}
	/**
	 * ���ű���
	 * @param v
	 */
	public void smsBackup(View v){
		try {
			SmsUtils.backuoSms(AToolsActivity.this);
			Toast.makeText(AToolsActivity.this, "���ݳɹ�", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			Toast.makeText(AToolsActivity.this, "����ʧ��", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}
	/**
	 * ���Żָ�
	 * @param v
	 */
	public void smsRestore(View v){
		
	}
}
