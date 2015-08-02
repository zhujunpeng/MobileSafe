package cn.edu.cqu.mobilesafe.service;

import java.util.Timer;
import java.util.TimerTask;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;
import cn.edu.cqu.mobilesafe.R;
import cn.edu.cqu.mobilesafe.receiver.MyWidget;
import cn.edu.cqu.mobilesafe.utils.SystemInfosUtils;

public class UpdateWidgetService extends Service {

	protected static final String TAG = "UpdateWidgetService";
	private Timer timer;
	private TimerTask task;
	private AppWidgetManager awm;
	private ScreenOffReceivce offReceivce;
	private ScreenOnReceivce onReceivce;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		onReceivce = new ScreenOnReceivce();
		offReceivce = new ScreenOffReceivce();
		registerReceiver(offReceivce, new IntentFilter(Intent.ACTION_SCREEN_OFF));
		registerReceiver(onReceivce, new IntentFilter(Intent.ACTION_SCREEN_ON));
		if (timer ==null && task == null) {
			startTimer();
		}
	}

	

	public class ScreenOffReceivce extends BroadcastReceiver{

		private static final String TAG = "ScreenOffReceivce";

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "屏幕锁屏。。");
			stopTimer();
		}
		
	}
	
	public class ScreenOnReceivce extends BroadcastReceiver{

		private static final String TAG = "ScreenOffReceivce";

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "屏幕解锁。。");
			startTimer();
		}
		
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (timer != null && task != null) {
			stopTimer();
		}
		if (offReceivce != null && onReceivce != null) {
			unregisterReceiver(offReceivce);
			unregisterReceiver(onReceivce);
			offReceivce=null;
			onReceivce=null;
		}
	}

	private void startTimer() {
		awm = AppWidgetManager.getInstance(getApplicationContext());
		timer = new Timer();
		task = new TimerTask() {

			@Override
			public void run() {
				Log.i(TAG, "更新widget。。。");
				ComponentName provider = new ComponentName(
						getApplicationContext(), MyWidget.class);
				RemoteViews views = new RemoteViews(getPackageName(),
						R.layout.process_widget);
				views.setTextViewText(
						R.id.process_count,
						"正在运行的程序："
								+ SystemInfosUtils
										.getRunningProgressCount(getApplicationContext())
								+ "个");
				long availMen = SystemInfosUtils
						.getAvailMen(getApplicationContext());
				views.setTextViewText(
						R.id.process_memory,
						"剩余内存为："
								+ Formatter.formatFileSize(
										getApplicationContext(), availMen));

				// 描述一个动作，这个动作是有另一个程序执行
				// 自定义一个广播事件，杀死后台程序
				Intent intent = new Intent();
				intent.setAction("cn.edu.cqu.mobilesafe.killall");
				PendingIntent pendingIntent = PendingIntent.getBroadcast(
						getApplicationContext(), 0, intent,
						PendingIntent.FLAG_UPDATE_CURRENT);
				views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);
				awm.updateAppWidget(provider, views);
			}
		};
		timer.schedule(task, 0, 3000);
	}
	private void stopTimer() {
		timer.cancel();
		task.cancel();
		timer = null;
		task = null;
	}
}
