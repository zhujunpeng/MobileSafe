package cn.edu.cqu.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import cn.edu.cqu.mobilesafe.service.AddressService;
import cn.edu.cqu.mobilesafe.service.CallSmsSafeService;
import cn.edu.cqu.mobilesafe.service.WatchDogService;
import cn.edu.cqu.mobilesafe.ui.SettingClickView;
import cn.edu.cqu.mobilesafe.ui.SettingItemView;
import cn.edu.cqu.mobilesafe.utils.ServiceUtils;

public class SettingActivity extends Activity {

	private SettingItemView siv_update, siv_address,siv_callsmsm_safe,scv_wathdog;
	private SettingClickView scv_changebg;
	private SharedPreferences sp;
	private Intent intent_address,callsmsIntent,watchdogIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		sp = getSharedPreferences("config", MODE_PRIVATE);
		siv_update = (SettingItemView) findViewById(R.id.siv_update);
		siv_address = (SettingItemView) findViewById(R.id.siv_address);
		siv_callsmsm_safe = (SettingItemView) findViewById(R.id.siv_callsmsm_safe);
		scv_wathdog = (SettingItemView) findViewById(R.id.scv_wathdog);
		
		// ���øı䱳��
		scv_changebg = (SettingClickView) findViewById(R.id.scv_changebg);
		scv_changebg.setTitle("��������ʾ����");
		final String items[] = {"��͸��","������","��ʿ��","������","ƻ����"};
		int which = sp.getInt("which", 0);
		scv_changebg.setDesc(items[which]);
		scv_changebg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				int dd = sp.getInt("which", 0);
				// ����һ����ѡ��
				AlertDialog.Builder builder = new Builder(SettingActivity.this);
				builder.setTitle("�����ط��");
				builder.setSingleChoiceItems(items, dd, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// ����ѡ�еĲ���
						Editor editor = sp.edit();
						editor.putInt("which", which);
						scv_changebg.setDesc(items[which]);
						editor.commit();
					}
				});
				builder.setNegativeButton("ȡ��", null);
				builder.show();
			}
		});

		boolean update = sp.getBoolean("update", false);
		if (update) {
			// �Զ������Ѿ�����
			siv_update.setChecked(true);
		} else {
			// �Զ������Ѿ�����
			siv_update.setChecked(false);
		}
		siv_update.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Editor editor = sp.edit();
				// �ж��Ƿ�ѡ��
				if (siv_update.isChecked()) {
					// �Ѿ�������
					siv_update.setChecked(false);
					editor.putBoolean("update", false);
				} else {
					// �ر�����
					siv_update.setChecked(true);
					editor.putBoolean("update", true);
				}
				editor.commit();
			}
		});
		intent_address = new Intent(this, AddressService.class);
		boolean serviceRunning = ServiceUtils.isServiceRunning(SettingActivity.this,
				"cn.edu.cqu.mobilesafe.service.AddressService");
		if (serviceRunning) {
			// �����Ѿ�����
			siv_address.setChecked(true);
		}else {
			siv_address.setChecked(false);
		}
		siv_address.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// �ж��Ƿ�ѡ��
				if (siv_address.isChecked()) {
					// ��Ϊ��ѡ��״̬���򿪹�����
					siv_address.setChecked(false);
					// �رշ���
					stopService(intent_address);
				} else {
					// �ر���ʾ������
					siv_address.setChecked(true);
					// �򿪷���
					startService(intent_address);
				}
			}
		});
		
		// ����������״̬
		callsmsIntent = new Intent(SettingActivity.this, CallSmsSafeService.class);
		
		siv_callsmsm_safe.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// �ж��Ƿ�ѡ��
				if (siv_callsmsm_safe.isChecked()) {
					// ��Ϊ��ѡ��״̬
					siv_callsmsm_safe.setChecked(false);
					// �رշ���
					stopService(callsmsIntent);
				} else {
					// ѡ��״̬
					siv_callsmsm_safe.setChecked(true);
					// �򿪷���
					startService(callsmsIntent);
				}
			}
		});
		
		// ����������״̬
		watchdogIntent = new Intent(SettingActivity.this, WatchDogService.class);
		boolean watchdogRunning = ServiceUtils.isServiceRunning(SettingActivity.this,
				"cn.edu.cqu.mobilesafe.service.WatchDogService");
		if (watchdogRunning) {
			// �����Ѿ�����
			scv_wathdog.setChecked(true);
		}else {
			scv_wathdog.setChecked(false);
		}
		scv_wathdog.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// �ж��Ƿ�ѡ��
				if (scv_wathdog.isChecked()) {
					// ��Ϊ��ѡ��״̬
					scv_wathdog.setChecked(false);
					// �رշ���
					stopService(watchdogIntent);
				} else {
					// ѡ��״̬
					scv_wathdog.setChecked(true);
					// �򿪷���
					startService(watchdogIntent);
				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		boolean serviceRunning = ServiceUtils.isServiceRunning(SettingActivity.this,
				"cn.edu.cqu.mobilesafe.service.AddressService");
		if (serviceRunning) {
			// �����Ѿ�����
			siv_address.setChecked(true);
		}else {
			siv_address.setChecked(false);
		}
		boolean isCallSmsmServiceRunning = ServiceUtils.isServiceRunning(SettingActivity.this,
				"cn.edu.cqu.mobilesafe.service.CallSmsSafeService");
		if (isCallSmsmServiceRunning) {
			// �����Ѿ�����
			siv_callsmsm_safe.setChecked(true);
		}else {
			siv_callsmsm_safe.setChecked(false);
		}
	}
}
