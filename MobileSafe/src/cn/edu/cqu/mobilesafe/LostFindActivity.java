package cn.edu.cqu.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class LostFindActivity extends Activity {
	
	private SharedPreferences sp;
	private TextView tv_lsotfind_phone;
	private ImageView iv_lostfind_lock;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		
		sp = getSharedPreferences("config", MODE_PRIVATE);
		boolean configed = sp.getBoolean("configed", false);
		if (configed) {
			// 停留在手机防盗界面
			setContentView(R.layout.activity_lostfind);
			
			tv_lsotfind_phone = (TextView) findViewById(R.id.tv_lsotfind_phone);
			iv_lostfind_lock = (ImageView) findViewById(R.id.iv_lostfind_lock);
			String phone = sp.getString("phone", null);
			tv_lsotfind_phone.setText(phone);
			iv_lostfind_lock.setImageResource(R.drawable.lock);
		}else {
			// 还没做过设置向导
			startActivity(new Intent(LostFindActivity.this, Setup1Activity.class));
			finish();
		}
	}
	
	public void reEnterSetup(View v){
		startActivity(new Intent(this, Setup1Activity.class));
	}
}
