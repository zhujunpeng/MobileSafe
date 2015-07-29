package cn.edu.cqu.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug.MemoryInfo;
import cn.edu.cqu.mobilesafe.R;
import cn.edu.cqu.mobilesafe.domain.TaskInfo;

public class TaskInfoProvider {

	public static List<TaskInfo> getTaskInfos(Context context){
		List<TaskInfo> TaskInfos = new ArrayList<TaskInfo>();
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		PackageManager pm = context.getPackageManager();
		List<RunningAppProcessInfo> processes = am.getRunningAppProcesses();
		for (RunningAppProcessInfo processInfo : processes) {
			TaskInfo taskInfo = new TaskInfo();
			// 获取程序的报名
			String packageName = processInfo.processName;
			taskInfo.setPackageName(packageName);
			try {
				// 获得一个应用程序的信息，相当于mainfast文件
				ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0);
				// 获取当前应用占用的内存信息
				MemoryInfo[] memoryInfo = am.getProcessMemoryInfo(new int[]{processInfo.pid});
				long appMem = memoryInfo[0].getTotalPrivateDirty() * 1024;
				taskInfo.setAppMem(appMem);
				// 应用程序的图标
				Drawable icon = applicationInfo.loadIcon(pm);
				taskInfo.setIcon(icon);
				// 应用程序的名称
				String name = applicationInfo.loadLabel(pm).toString();
				taskInfo.setName(name);
				int flags = applicationInfo.flags;
				if ((flags&ApplicationInfo.FLAG_SYSTEM) == 0) {
					//用户程序
					taskInfo.setUserApp(true);
				}else{
					// 系统程序
					taskInfo.setUserApp(false);
				}
			} catch (Exception e) {
				e.printStackTrace();
				taskInfo.setIcon(context.getResources().getDrawable(R.drawable.ic_launcher));
				taskInfo.setName(packageName);
			}
			TaskInfos.add(taskInfo);
		}
		return TaskInfos;
	}
}
