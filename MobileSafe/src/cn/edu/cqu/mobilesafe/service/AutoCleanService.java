package cn.edu.cqu.mobilesafe.service;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class AutoCleanService extends Service {
	
	private ScreenOffReceivce receiver;
	private ActivityManager am;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		receiver = new ScreenOffReceivce();
		registerReceiver(receiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
		receiver = null;
	}
	
	public class ScreenOffReceivce extends BroadcastReceiver{

		private static final String TAG = "ScreenOffReceivce";

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "屏幕锁屏了；；；");
			List<RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
			for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
				// 杀死后台运行的程序
				am.killBackgroundProcesses(runningAppProcessInfo.processName);
			}
			Log.i(TAG, "干掉了一些程序");
		}
		
	}
}
