package cn.edu.cqu.mobilesafe.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;

public class SystemInfosUtils {

	/**
	 * 获取当前运行的程序个数
	 * @param context
	 * @return
	 */
	public static int getRunningProgressCount(Context context){
//		PackageManager  包管理器，相当于进程管理器，静态的内容，是获取所有的应用
//		ActivityManager 进程管理器，获取所有正在运行的程序
		// 获得一个进程管理器
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> appProcesses = am.getRunningAppProcesses();
		return appProcesses.size();
	}
	/**
	 * 获取可用的内存
	 * @param context
	 * @return
	 */
	public static long getAvailMen(Context context){
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo outInfo = new MemoryInfo();
		am.getMemoryInfo(outInfo);
		return outInfo.availMem;
	}
	/**
	 * 获取全部的内存
	 * @param context
	 * @return
	 */
	public static long getTotalMen(Context context){
		// 在Android4.1.2以上的版本上可以这么用
//		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//		MemoryInfo outInfo = new MemoryInfo();
//		am.getMemoryInfo(outInfo);
//		return outInfo.totalMem;
		
		try {
			File file = new File("proc/meminfo");
			FileInputStream fis = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String line = br.readLine();
			StringBuffer sb = new StringBuffer();
			for (char c : line.toCharArray()) {
				if (c >= '0' && c <= '9') {
					sb.append(c);
				}
			}
			br.close();
			return Long.parseLong(sb.toString())*1024;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
}
