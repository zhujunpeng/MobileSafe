package cn.edu.cqu.mobilesafe.service;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import cn.edu.cqu.mobilesafe.EnterPwdActivity;
import cn.edu.cqu.mobilesafe.db.dao.AppLockDAO;

public class WatchDogService extends Service {
	
	private static final String TAG = "WatchDogService";
	private ActivityManager am;
	private boolean flag;
	private AppLockDAO appLockDAO;
	private WatchDogReceiver dogReceiver;
	private ScreenOffReceivce offReceivce;
	private ScreenOnReceivce onReceivce;
	private DataChangeReceivce changeReceivce;
	private String temptoppackagename;
	private List<String> packagenames;
	private Intent intent;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private class WatchDogReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("�յ���ͣ�����Ĺ㲥�¼��ˡ�����");
			temptoppackagename = intent.getStringExtra("temptoppackagename");
		}
		
	}
	
	public class ScreenOffReceivce extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "��Ļ�����ˣ�����");
			temptoppackagename = null;
			flag = false;
		}
		
	}
	
	public class DataChangeReceivce extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "���ݷ����ı���");
			packagenames = appLockDAO.findAll();
			System.out.println("packagenames---" + packagenames);
		}
		
	}
	
	public class ScreenOnReceivce extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "��Ļ������");
			// ����Ҫ��������
			flag = true;
			Log.i(TAG, "flag---" + flag);
			watchDog();
		}
		
	}
	@Override
	public void onCreate() {
		super.onCreate();
		
		dogReceiver = new WatchDogReceiver();
		offReceivce = new ScreenOffReceivce();
		onReceivce = new ScreenOnReceivce();
		changeReceivce = new DataChangeReceivce();
		registerReceiver(dogReceiver, new IntentFilter("cn.edu.cqu.mobilesafe.tempstop"));
		registerReceiver(offReceivce, new IntentFilter(Intent.ACTION_SCREEN_OFF));
		registerReceiver(onReceivce, new IntentFilter(Intent.ACTION_SCREEN_ON));
		registerReceiver(changeReceivce, new IntentFilter("cn.edu.cqu.mobilesafe.packagechange"));
		appLockDAO = new AppLockDAO(getApplicationContext());
		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		
		packagenames = appLockDAO.findAll();
		intent = new Intent(getApplicationContext(), EnterPwdActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		flag = true;
		// ���Ź�����
		watchDog();
		
	}



	private void watchDog() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(flag){
					// һֱ��ȡ�������е�����ջ
					List<RunningTaskInfo> runningTasks = am.getRunningTasks(1);
					String packageName = runningTasks.get(0).topActivity.getPackageName();
//					Log.i(TAG, "packageName---" + packageName);
					if (appLockDAO.find(packageName)) {// ��ѯ���ݿ�̫����������Դ���ĳɲ�ѯ�ڴ�
//					if(packagenames.contains(packageName)){// ��ѯ�ڴ棬�ӿ��ٶ�
						if (packageName.equals(temptoppackagename)) {
							
						}else {
							System.out.println("Ҫ�󱣻�");
							intent.putExtra("packagename", packageName);
							startActivity(intent);
						}
					}
					try {
						// ���Ź�����50ms
						Thread.sleep(20);
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
		unregisterReceiver(dogReceiver);
		dogReceiver = null;
		unregisterReceiver(offReceivce);
		offReceivce = null;
		unregisterReceiver(onReceivce);
		onReceivce = null;
		unregisterReceiver(changeReceivce);
		changeReceivce = null;
		flag = false;
	}
}
