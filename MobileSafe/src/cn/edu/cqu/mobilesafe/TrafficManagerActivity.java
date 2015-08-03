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
		
		// 1 ���һ����������
		PackageManager pm = getPackageManager();
		// 2 �����ֻ�����ϵͳ����ȡ����Ӧ�ó����uid
		List<ApplicationInfo> applications = pm.getInstalledApplications(0);
		for (ApplicationInfo applicationInfo : applications) {
			// ���Ӧ�ó����uid
			int uid = applicationInfo.uid;
			// ��ȡ�ϴ������ص�����
			long uidRxBytes = TrafficStats.getUidRxBytes(uid);
			long uidTxBytes = TrafficStats.getUidTxBytes(uid);
		}
		// ��ȡ�ֻ�3g/2g�����ϴ����ص�������
		long mobileRxBytes = TrafficStats.getMobileRxBytes();
		long mobileTxBytes = TrafficStats.getMobileTxBytes();
		// ��ȡ�ֻ�ȫ������ӿڵ�����
		long totalRxBytes = TrafficStats.getTotalRxBytes();
		long totalTxBytes = TrafficStats.getTotalTxBytes();
	}
}
