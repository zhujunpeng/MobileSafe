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
 * 提供手机里面安装程序的所有应用程序的信息
 * @author Administrator
 *
 */
public class AppInfoProvider {

	/**
	 * 获取所有的安装应用的程序的信息
	 * @param context
	 * @return
	 */
	public static List<AppInfo> getAppInfos(Context context){
		// 获得一个包管理器
		PackageManager pm = context.getPackageManager();
		// 获得所有安装的如那件信息
		List<PackageInfo> appInfos = pm.getInstalledPackages(0);
		List<AppInfo> appInfolist = new ArrayList<AppInfo>();
		for (PackageInfo packageInfo : appInfos) {
			// 应用程序包名
			String pakageName = packageInfo.packageName;
			// 应用程序图标
			Drawable icon = packageInfo.applicationInfo.loadIcon(pm);
			// 应用程序名称
			CharSequence name = packageInfo.applicationInfo.loadLabel(pm);
			
			AppInfo appInfo = new AppInfo();
			int flags = packageInfo.applicationInfo.flags;
			if ((flags&ApplicationInfo.FLAG_SYSTEM) == 0) {
				//用户程序
				appInfo.setUserApp(true);
			}else{
				// 系统程序
				appInfo.setUserApp(false);
			}
			if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 0) {
				// 手机内存上
				appInfo.setRomApp(true);
			}else {
				// 手机外部存储上
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
