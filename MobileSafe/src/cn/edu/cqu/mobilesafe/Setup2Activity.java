package cn.edu.cqu.mobilesafe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Toast;
import cn.edu.cqu.mobilesafe.ui.SettingItemView;

public class Setup2Activity extends BaseSetupActivity {
	
	private SettingItemView siv_setup2_sim;
	private TelephonyManager tm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);
		
		siv_setup2_sim = (SettingItemView) findViewById(R.id.siv_setup2_sim);
		String str = sp.getString("sim", null);
		if (TextUtils.isEmpty(str)) {
			// 没有绑定了
			siv_setup2_sim.setChecked(false);
		}else {
			// 绑定了
			siv_setup2_sim.setChecked(true);
		}
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		siv_setup2_sim.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();
				if (siv_setup2_sim.isChecked()) {
					siv_setup2_sim.setChecked(false);
					editor.putString("sim", null);
				}else {
					siv_setup2_sim.setChecked(true);
					// 保存SIM卡的序列号
					String number = tm.getSimSerialNumber();
					editor.putString("sim", number);
				}
				editor.commit();
			}
		});
	}
	

	@Override
	public void ShowNextActivity() {
		String sim = sp.getString("sim", null);
		if (TextUtils.isEmpty(sim)) {
			//
			Toast.makeText(this, "sim卡没有绑定", Toast.LENGTH_SHORT).show();
			return;
		}
		startActivity(new Intent(this, Setup3Activity.class));
		finish();
		overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
	}

	@Override
	public void ShowPreActivity() {
		startActivity(new Intent(this, Setup1Activity.class));
		finish();
		
		overridePendingTransition(R.anim.trans_pre_in, R.anim.trans_pre_out);
	}
}
