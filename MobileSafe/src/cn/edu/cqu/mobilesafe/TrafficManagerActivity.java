package cn.edu.cqu.mobilesafe;

import java.util.List;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.os.Bundle;

public class TrafficManagerActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_traffic_manager);
		
		// 1 获得一个包管理器
		PackageManager pm = getPackageManager();
		// 2 遍历手机操作系统，获取所有应用程序的uid
		List<ApplicationInfo> applications = pm.getInstalledApplications(0);
		for (ApplicationInfo applicationInfo : applications) {
			// 获得应用程序的uid
			int uid = applicationInfo.uid;
			// 获取上传和下载的流量
			long uidRxBytes = TrafficStats.getUidRxBytes(uid);
			long uidTxBytes = TrafficStats.getUidTxBytes(uid);
		}
		// 获取手机3g/2g网络上传下载的总流量
		long mobileRxBytes = TrafficStats.getMobileRxBytes();
		long mobileTxBytes = TrafficStats.getMobileTxBytes();
		// 获取手机全部网络接口的数据
		long totalRxBytes = TrafficStats.getTotalRxBytes();
		long totalTxBytes = TrafficStats.getTotalTxBytes();
	}
}
