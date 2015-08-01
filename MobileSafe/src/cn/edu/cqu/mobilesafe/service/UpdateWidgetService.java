package cn.edu.cqu.mobilesafe.service;

import java.util.Timer;
import java.util.TimerTask;

import cn.edu.cqu.mobilesafe.R;
import cn.edu.cqu.mobilesafe.receiver.MyWidget;
import cn.edu.cqu.mobilesafe.utils.SystemInfosUtils;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;

public class UpdateWidgetService extends Service {
	
	protected static final String TAG = "UpdateWidgetService";
	private Timer timer;
	private TimerTask task;
	private AppWidgetManager awm;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		awm = AppWidgetManager.getInstance(getApplicationContext());
		timer = new Timer();
		task = new TimerTask() {
			
			@Override
			public void run() {
				ComponentName provider = new ComponentName(getApplicationContext(), MyWidget.class);
				RemoteViews views = new RemoteViews(getPackageName(), R.layout.process_widget);
				views.setTextViewText(R.id.process_count, "正在运行的程序：" +
				SystemInfosUtils.getRunningProgressCount(getApplicationContext()) + "个");
				long availMen = SystemInfosUtils.getAvailMen(getApplicationContext());
				views.setTextViewText(R.id.process_memory, "剩余内存为：" +
				Formatter.formatFileSize(getApplicationContext(), availMen));
				awm.updateAppWidget(provider, views);
			}
		};
		timer.schedule(task, 0, 3000);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		timer.cancel();
		task.cancel();
		timer = null;
		task = null;
	}
}
