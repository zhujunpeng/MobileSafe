package cn.edu.cqu.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import cn.edu.cqu.mobilesafe.AppManagerActivity;
import cn.edu.cqu.mobilesafe.domain.AppInfo;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
/**
 * �ṩ�ֻ����氲װ���������Ӧ�ó������Ϣ
 * @author Administrator
 *
 */
public class AppInfoProvider {

	/**
	 * ��ȡ���еİ�װӦ�õĳ������Ϣ
	 * @param context
	 * @return
	 */
	public static List<AppInfo> getAppInfos(Context context){
		// ���һ����������
		PackageManager pm = context.getPackageManager();
		// ������а�װ�����Ǽ���Ϣ
		List<PackageInfo> appInfos = pm.getInstalledPackages(0);
		List<AppInfo> appInfolist = new ArrayList<AppInfo>();
		for (PackageInfo packageInfo : appInfos) {
			// Ӧ�ó������
			String pakageName = packageInfo.packageName;
			// Ӧ�ó���ͼ��
			Drawable icon = packageInfo.applicationInfo.loadIcon(pm);
			// Ӧ�ó�������
			CharSequence name = packageInfo.applicationInfo.loadLabel(pm);
			
			AppInfo appInfo = new AppInfo();
			int flags = packageInfo.applicationInfo.flags;
			if ((flags&ApplicationInfo.FLAG_SYSTEM) == 0) {
				//�û�����
				appInfo.setUserApp(true);
			}else{
				// ϵͳ����
				appInfo.setUserApp(false);
			}
			if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 0) {
				// �ֻ��ڴ���
				appInfo.setRomApp(true);
			}else {
				// �ֻ��ⲿ�洢��
				appInfo.setRomApp(false);
			}
			appInfo.setIcon(icon);
			appInfo.setName(name.toString());
			appInfo.setPakageName(pakageName);
			appInfolist.add(appInfo);
		}
		return appInfolist;
	}
}
