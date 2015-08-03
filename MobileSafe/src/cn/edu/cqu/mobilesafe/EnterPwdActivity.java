package cn.edu.cqu.mobilesafe;

import cn.edu.cqu.mobilesafe.utils.MD5Utils;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class EnterPwdActivity extends Activity {
	
	private EditText et_enter_pwd;
	private TextView tv_app_name;
	private ImageView iv_app_icon;
	private String packagename;
	private SharedPreferences sp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enter_pwd);
		
		et_enter_pwd = (EditText) findViewById(R.id.et_enter_pwd);
		tv_app_name = (TextView) findViewById(R.id.tv_app_name);
		iv_app_icon = (ImageView) findViewById(R.id.iv_app_icon);
		
		sp = getSharedPreferences("config", MODE_PRIVATE);
		Intent intent = getIntent();
		packagename = intent.getStringExtra("packagename");
		PackageManager pm = getPackageManager();
		try {
			ApplicationInfo info = pm.getApplicationInfo(packagename, 0);
			iv_app_icon.setImageDrawable(info.loadIcon(pm));
			tv_app_name.setText(info.loadLabel(pm));
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onBackPressed() {
//		 <action android:name="android.intent.action.MAIN" />
//         <category android:name="android.intent.category.HOME" />
//         <category android:name="android.intent.category.DEFAULT" />
//         <category android:name="android.intent.category.MONKEY"/>
		Intent intent = new Intent();
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.HOME");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addCategory("android.intent.category.MONKEY");
		startActivity(intent);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		finish();
	}

	public void click(View v){
		String pwd = et_enter_pwd.getText().toString().trim();
		String watchdogpassword = sp.getString("watchdogpassword", null);
		if (TextUtils.isEmpty(pwd)) {
			Toast.makeText(getApplicationContext(), "密码不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		if (watchdogpassword.equals(MD5Utils.md5Password(pwd))) {
			// 发送一个广播，告诉看门狗临时停止对这个程序的保护
			Intent intent = new Intent();
			intent.setAction("cn.edu.cqu.mobilesafe.tempstop");
			intent.putExtra("temptoppackagename", packagename);
			sendBroadcast(intent);
			finish();
		}else {
			Toast.makeText(getApplicationContext(), "密码错误", Toast.LENGTH_SHORT).show();
		}
	}
}
