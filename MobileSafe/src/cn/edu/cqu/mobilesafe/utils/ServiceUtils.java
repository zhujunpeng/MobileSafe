package cn.edu.cqu.mobilesafe.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.util.Log;

public class ServiceUtils {
	
	private static final String TAG = "ServiceUtils";

	/**
	 * У��ĳ�������Ƿ񻹴���
	 */
	public static boolean isServiceRunning(Context context,String serviceName){
		// У������Ƿ񻹴���
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> services = am.getRunningServices(100);
		for (RunningServiceInfo info : services) {
			// �õ������������еķ��������
			String name = info.service.getClassName();
//			Log.i(TAG, "���������--"+name);
			if (serviceName.equals(name)) {
				return true;
			}
		}
		return false;
	}

}
