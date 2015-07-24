package cn.edu.cqu.mobilesafe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;

public class Setup4Activity extends BaseSetupActivity {
	
	private SharedPreferences sp;
	private CheckBox cb_setup4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup4);
		
		cb_setup4 = (CheckBox) findViewById(R.id.cb_setup4);
		sp = getSharedPreferences("config", MODE_PRIVATE);
//		if (cb_setup4.isChecked()) {
//			cb_setup4.setText("您开启了防盗保护");
//		}else {
//			cb_setup4.setText("您还没有开启防盗保护");
//		}
		cb_setup4.setChecked(true);
		cb_setup4.setText("您开启了防盗保护");
	}
	
	
	public void finish(View v){
		Editor editor = sp.edit();
		editor.putBoolean("configed", true);
		editor.commit();
//		cb_setup4.setChecked(true);
//		cb_setup4.setText("您开启了防盗保护");
		startActivity(new Intent(this, LostFindActivity.class));
		finish();
		overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
	}

	@Override
	public void ShowNextActivity() {
		
	}

	@Override
	public void ShowPreActivity() {
		startActivity(new Intent(this, Setup3Activity.class));
		finish();
		overridePendingTransition(R.anim.trans_pre_in, R.anim.trans_pre_out);
	}
}
