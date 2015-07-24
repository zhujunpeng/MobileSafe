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
import cn.edu.cqu.mobilesafe.ui.SettingClickView;
import cn.edu.cqu.mobilesafe.ui.SettingItemView;
import cn.edu.cqu.mobilesafe.utils.ServiceUtils;

public class SettingActivity extends Activity {

	private SettingItemView siv_update, siv_address;
	private SettingClickView scv_changebg;
	private SharedPreferences sp;
	private Intent intent_address;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		sp = getSharedPreferences("config", MODE_PRIVATE);
		siv_update = (SettingItemView) findViewById(R.id.siv_update);
		siv_address = (SettingItemView) findViewById(R.id.siv_address);
		
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
	}
}
