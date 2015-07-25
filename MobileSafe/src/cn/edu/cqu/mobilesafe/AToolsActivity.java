package cn.edu.cqu.mobilesafe;

import cn.edu.cqu.mobilesafe.utils.SmsUtils;
import cn.edu.cqu.mobilesafe.utils.SmsUtils.BackUpCallBack;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class AToolsActivity extends Activity {
	
	private ProgressDialog pd;

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
		pd = new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setTitle("���ڱ��ݶ���");
		pd.show();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
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
							Toast.makeText(AToolsActivity.this, "���ݳɹ�", Toast.LENGTH_SHORT).show();
						}
					});
				} catch (Exception e) {
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							Toast.makeText(AToolsActivity.this, "����ʧ��", Toast.LENGTH_SHORT).show();
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
	 * ���Żָ�
	 * @param v
	 */
	public void smsRestore(View v){
		
	}
}
