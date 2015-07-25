package cn.edu.cqu.mobilesafe;

import cn.edu.cqu.mobilesafe.utils.SmsUtils;
import cn.edu.cqu.mobilesafe.utils.SmsUtils.BackUpCallBack;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

public class AToolsActivity extends Activity {
	
	private ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
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
		pd = new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setTitle("正在备份短信");
		pd.show();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					// SmsUtils里面实现的一个接口，里面有两个方法
					SmsUtils.backuoSms(AToolsActivity.this,new BackUpCallBack() {
						
						@Override
						public void onSmsBackup(int progress) {
							pd.setProgress(progress);
						}
						
						@Override
						public void SmsMaxBackup(int max) {
							pd.setMax(max);
						}
					});
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(AToolsActivity.this, "备份成功", Toast.LENGTH_SHORT).show();
						}
					});
				} catch (Exception e) {
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							Toast.makeText(AToolsActivity.this, "备份失败", Toast.LENGTH_SHORT).show();
						}
					});
					e.printStackTrace();
				}finally{
					pd.dismiss();
				}
				
			}
		}).start();
	}
	/**
	 * 短信恢复
	 * @param v
	 */
	public void smsRestore(View v){
		
		pd = new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setTitle("正在恢复短信");
		pd.show();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					SmsUtils.restoreSms(AToolsActivity.this, true, new BackUpCallBack() {
						
						@Override
						public void onSmsBackup(int progress) {
							pd.setProgress(progress);
						}
						
						@Override
						public void SmsMaxBackup(int max) {
							pd.setMax(max);
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					pd.dismiss();
				}
			}
		}).start();
	}
}
