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
	 * 查询号码归属地
	 * @param v
	 */
	public void numberQuery(View v){
		startActivity(new Intent(this, NumberAddressQueryActivity.class));
	}
	/**
	 * 短信备份
	 * @param v
	 */
	public void smsBackup(View v){
		try {
			SmsUtils.backuoSms(AToolsActivity.this);
			Toast.makeText(AToolsActivity.this, "备份成功", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			Toast.makeText(AToolsActivity.this, "备份失败", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}
	/**
	 * 短信恢复
	 * @param v
	 */
	public void smsRestore(View v){
		
	}
}
