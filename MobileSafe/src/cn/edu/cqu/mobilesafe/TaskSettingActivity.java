package cn.edu.cqu.mobilesafe;

import cn.edu.cqu.mobilesafe.service.AutoCleanService;
import cn.edu.cqu.mobilesafe.utils.ServiceUtils;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class TaskSettingActivity extends Activity {
	
	private CheckBox cb_show_system;
	private CheckBox cb_auto_clean;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_setting);
		
		cb_show_system = (CheckBox) findViewById(R.id.cb_show_system);
		cb_auto_clean = (CheckBox) findViewById(R.id.cb_auto_clean);
		
		sp = getSharedPreferences("config", MODE_PRIVATE);
		cb_show_system.setChecked(sp.getBoolean("showsystem", false));
		cb_show_system.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Editor editor = sp.edit();
				editor.putBoolean("showsystem", isChecked);
				editor.commit();
			}
		});
		
		cb_auto_clean.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Intent intent = new Intent(TaskSettingActivity.this, AutoCleanService.class);
				// ��������
				if (isChecked) {
					startService(intent);
				}else {
					stopService(intent);
				}
			}
		});
		
		boolean serviceRunning = ServiceUtils.isServiceRunning(this, "cn.edu.cqu.mobilesafe.service.AutoCleanService");
		Editor editor = sp.edit();
		if (serviceRunning) {
			cb_auto_clean.setChecked(true);
			editor.putBoolean("autoclean", true);
		}else {
			cb_auto_clean.setChecked(false);
			editor.putBoolean("autoclean", false);
		}
		editor.commit();
	}
}
