package cn.edu.cqu.mobilesafe;

import java.util.List;

import cn.edu.cqu.mobilesafe.db.dao.AntivirusDAO;
import cn.edu.cqu.mobilesafe.utils.MD5Utils;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AntivirusActivity extends Activity {
	
	protected static final int SCAN = 0;
	protected static final int FINISH = 1;
	private ImageView iv_scan_status;
	private ProgressBar progressBar1;
	private TextView tv_scan;
	private PackageManager pm;
	private LinearLayout ll_container;
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SCAN:
				ScanInfos scanInfos= (ScanInfos) msg.obj;
				tv_scan.setText("正在查杀：" + scanInfos.name);
				TextView textView = new TextView(getApplicationContext());
				if (scanInfos.isVirus) {
					textView.setText("扫描安全" + scanInfos.packagename);
					textView.setTextColor(Color.RED);
				}else {
					textView.setText("发现病毒" + scanInfos.packagename);
					textView.setTextColor(Color.GRAY);
				}
				ll_container.addView(textView,0);
				break;
			case FINISH:
				tv_scan.setText("查杀结束");
				iv_scan_status.clearAnimation();
				break;
			default:
				break;
			}
		};
	} ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_antivirus);
		
		iv_scan_status = (ImageView) findViewById(R.id.iv_scan_status);
		progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
		tv_scan = (TextView) findViewById(R.id.tv_scan);
		ll_container = (LinearLayout) findViewById(R.id.ll_container);
		RotateAnimation ra = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		ra.setDuration(3000);
		ra.setRepeatCount(Animation.INFINITE);
		iv_scan_status.startAnimation(ra);
		
		scanVirus();
	}

	/**
	 * 扫描病毒
	 */
	private void scanVirus() {
		pm = getPackageManager();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
				progressBar1.setMax(installedPackages.size());
				int progress = 0;
				for (PackageInfo packageInfo : installedPackages) {
					ScanInfos scanInfo = new ScanInfos();
					scanInfo.packagename = packageInfo.packageName;
					scanInfo.name = packageInfo.applicationInfo.loadLabel(pm).toString();
					
					String dataDir = packageInfo.applicationInfo.dataDir;
					String md5 = MD5Utils.md5Password(dataDir);
//					System.out.println("md5---" + md5);
					if (AntivirusDAO.isVirus(md5)) {
						// 是病毒
						scanInfo.isVirus = true;
					}else {
						// 不是病毒
						scanInfo.isVirus = false;
					}
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					Message msg = handler.obtainMessage();
					msg.what = SCAN;
					msg.obj = scanInfo;
					handler.sendMessage(msg);
					
					progress ++;
					progressBar1.setProgress(progress);
				}
				Message msg = handler.obtainMessage();
				msg.what = FINISH;
				handler.sendMessage(msg);
				
			}
		}).start();
	}
	
	private class ScanInfos{
		String packagename;
		String name;
		boolean isVirus;
	}
}
