package cn.edu.cqu.mobilesafe.receiver;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class KillAllReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		System.out.println("接收到来自桌面的广播");
		
		ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
		for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
			am.killBackgroundProcesses(runningAppProcessInfo.processName);
		}
	}

}
