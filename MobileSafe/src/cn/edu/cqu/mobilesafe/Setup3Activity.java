package cn.edu.cqu.mobilesafe;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

public class Setup3Activity extends BaseSetupActivity {
	
	private EditText et_setup3_phone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);
		
		et_setup3_phone = (EditText) findViewById(R.id.et_setup3_phone);
		
		et_setup3_phone.setText(sp.getString("phone", null));
	}
	@Override
	public void ShowNextActivity() {
		// 保存安全号码
		String phone = et_setup3_phone.getText().toString().trim();
		System.out.println("phone--" + phone);
		if (TextUtils.isEmpty(phone)) {
			Toast.makeText(this, "安全号码还没有设置", Toast.LENGTH_SHORT).show();
			return;
		}else {
			Editor editor = sp.edit();
			editor.putString("phone", phone);
			editor.commit();
		}
		
		startActivity(new Intent(this, Setup4Activity.class));
		finish();
		overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
		
	}
	@Override
	public void ShowPreActivity() {
		startActivity(new Intent(this, Setup2Activity.class));
		finish();
		overridePendingTransition(R.anim.trans_pre_in, R.anim.trans_pre_out);
	}
	
	public void selectContact(View v){
		Intent intent = new Intent(Setup3Activity.this, SelectContactActivity.class);
		startActivityForResult(intent,0);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.i(TAG, "test---test");
		if (data == null) {
			Log.i(TAG, "空了么？？？");
			return;
		}
		else {
			String phone = data.getStringExtra("phone").replace(" ", "");
			Log.i(TAG, phone);
			et_setup3_phone.setText(phone);
		}
	}
}
