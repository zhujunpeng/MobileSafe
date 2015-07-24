package cn.edu.cqu.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class AToolsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atools);
	}
	
	public void numberQuery(View v){
		startActivity(new Intent(this, NumberAddressQueryActivity.class));
	}
}
