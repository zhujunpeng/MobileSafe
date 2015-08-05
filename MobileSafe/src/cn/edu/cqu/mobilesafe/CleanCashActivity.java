package cn.edu.cqu.mobilesafe;

import java.lang.reflect.Method;
import java.util.List;
import java.util.zip.Inflater;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CleanCashActivity extends Activity {

	private ProgressBar pb;
	private TextView tv_scan_status;
	private LinearLayout ll_container;
	private PackageManager pm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clean_cash);

		pb = (ProgressBar) findViewById(R.id.pb);
		tv_scan_status = (TextView) findViewById(R.id.tv_scan_status);
		ll_container = (LinearLayout) findViewById(R.id.ll_container);

		cleanCache();
	}

	/**
	 * 扫描手机中所有应用程序的缓存信息
	 */
	private void cleanCache() {
		pm = getPackageManager();
		new Thread(new Runnable() {
			@Override
			public void run() {
				Method getPackageSizeInfoMethod = null;
				Method[] methods = PackageManager.class.getMethods();
				for (Method method : methods) {
					if ("getPackageSizeInfo".equals(method.getName())) {
						getPackageSizeInfoMethod = method;
					}
				}
				List<PackageInfo> installedPackages = pm
						.getInstalledPackages(0);
				pb.setMax(installedPackages.size());
				int progress = 0;
				for (PackageInfo packageInfo : installedPackages) {
					try {
						getPackageSizeInfoMethod.invoke(pm,
								packageInfo.packageName, new MyDataObserver());
					} catch (Exception e) {
						e.printStackTrace();
					}
					progress++;
					pb.setProgress(progress);
				}
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						tv_scan_status.setText("扫描结束");
						System.out.println("v扫描完成");
					}
				});
				
			}
		}).start();
	}

	private class MyDataObserver extends IPackageStatsObserver.Stub {

		@Override
		public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
				throws RemoteException {
			// 获得缓存信息
			final long cacheSize = pStats.cacheSize;
			final String packageName = pStats.packageName;
			runOnUiThread(new Runnable() {
				public void run() {
					tv_scan_status.setText("正在扫描：" + packageName);
					if (cacheSize > 0) {
						try {
							try {
								Thread.sleep(50);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							ApplicationInfo applicationInfo = pm
									.getApplicationInfo(packageName, 0);
							String name = applicationInfo.loadLabel(pm).toString();
							Drawable icon = applicationInfo.loadIcon(pm);
							String formatFileSize = Formatter.formatFileSize(getApplicationContext(), cacheSize);
							
							View view = View.inflate(getApplicationContext(), R.layout.list_cache_info, null);
							ImageView iv_app_icon = (ImageView) view.findViewById(R.id.iv_app_icon);
							TextView tv_app_name = (TextView) view.findViewById(R.id.tv_app_name);
							TextView tv_cache_size = (TextView) view.findViewById(R.id.tv_cache_size);
							// 清除一个缓存的按钮
							ImageView iv_clean_cache = (ImageView) view.findViewById(R.id.iv_clean_cache);
							
							
							iv_app_icon.setImageDrawable(icon);
							tv_app_name.setText(name);
							tv_cache_size.setText("缓存大小：" + formatFileSize);
							ll_container.addView(view, 0);
						} catch (NameNotFoundException e) {
							e.printStackTrace();
						}
					}
				}
			});

		}

	}
}
