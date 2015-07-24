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
	 * �����ֻ���ʼ�Ĺ㲥
	 * */
	@Override
	public void onReceive(Context context, Intent intent) {
		
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		tm = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
		
		// 1����ȡ֮ǰ�����SIM����Ϣ
		String saveSim = sp.getString("sim", null);
		
		// 2 ����һ�����Ƕ�ȡ��ǰSIM������Ϣ
		String realSim = tm.getSimSerialNumber();
		
		// 3���Ƚ�SIM����Ϣ
		if (saveSim.equals(realSim)) {
			// sim��û�б��
		}else {
			// sim�����
			Toast.makeText(context, "sim�����", Toast.LENGTH_SHORT).show();
		}
	}

}
