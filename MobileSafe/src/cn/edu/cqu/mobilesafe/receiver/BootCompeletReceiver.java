package cn.edu.cqu.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class BootCompeletReceiver extends BroadcastReceiver {
	
	private  SharedPreferences sp ;
	private TelephonyManager tm;

	/*
	 * 监听手机开始的广播
	 * */
	@Override
	public void onReceive(Context context, Intent intent) {
		
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		tm = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
		
		// 1、读取之前保存的SIM卡信息
		String saveSim = sp.getString("sim", null);
		
		// 2 、在一个就是读取当前SIM卡的信息
		String realSim = tm.getSimSerialNumber();
		
		// 3、比较SIM卡信息
		if (saveSim.equals(realSim)) {
			// sim卡没有变更
		}else {
			// sim卡变更
			Toast.makeText(context, "sim卡变更", Toast.LENGTH_SHORT).show();
		}
	}

}
