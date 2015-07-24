package cn.edu.cqu.mobilesafe.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.util.Log;

public class ServiceUtils {
	
	private static final String TAG = "ServiceUtils";

	/**
	 * 校验某个服务是否还存在
	 */
	public static boolean isServiceRunning(Context context,String serviceName){
		// 校验服务是否还存在
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> services = am.getRunningServices(100);
		for (RunningServiceInfo info : services) {
			// 得到所有正在运行的服务的名称
			String name = info.service.getClassName();
//			Log.i(TAG, "服务的名称--"+name);
			if (serviceName.equals(name)) {
				return true;
			}
		}
		return false;
	}

}
