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
	 * ��ȡ��ǰ���еĳ������
	 * @param context
	 * @return
	 */
	public static int getRunningProgressCount(Context context){
//		PackageManager  �����������൱�ڽ��̹���������̬�����ݣ��ǻ�ȡ���е�Ӧ��
//		ActivityManager ���̹���������ȡ�����������еĳ���
		// ���һ�����̹�����
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> appProcesses = am.getRunningAppProcesses();
		return appProcesses.size();
	}
	/**
	 * ��ȡ���õ��ڴ�
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
	 * ��ȡȫ�����ڴ�
	 * @param context
	 * @return
	 */
	public static long getTotalMen(Context context){
		// ��Android4.1.2���ϵİ汾�Ͽ�����ô��
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
