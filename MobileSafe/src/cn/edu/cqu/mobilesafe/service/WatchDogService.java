package cn.edu.cqu.mobilesafe.service;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class WatchDogService extends Service {
	
	private static final String TAG = "WatchDogService";
	private ActivityManager am;
	private boolean flag;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		flag = true;
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(flag){
					// 一直获取正在运行的任务栈
					List<RunningTaskInfo> runningTasks = am.getRunningTasks(100);
					String packageName = runningTasks.get(0).topActivity.getPackageName();
					Log.i(TAG, "packageName---" + packageName);
					try {
						// 看门狗休眠50ms
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		flag = false;
	}
}
