package cn.edu.cqu.mobilesafe;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

public class Setup1Activity extends BaseSetupActivity {
	
	protected static final String TAG = "Setup1Activity";
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup1);
		
		
	}
	

	@Override
	public void ShowNextActivity() {
		startActivity(new Intent(this, Setup2Activity.class));
		finish();
		 // 要求在finish()或者startActivity方法之后
		overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
	}

	@Override
	public void ShowPreActivity() {
		
	}
	
	
}
