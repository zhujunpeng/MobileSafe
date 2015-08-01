package cn.edu.cqu.mobilesafe.receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import cn.edu.cqu.mobilesafe.service.UpdateWidgetService;

public class MyWidget extends AppWidgetProvider {

	/**
	 * 每次操作widget的时候都会调用
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
	}
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		
		Intent intent = new Intent(context, UpdateWidgetService.class);
		context.startService(intent);
	}
	/**
	 * 第一个widget创建的时候调用
	 */
	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		
		Intent intent = new Intent(context, UpdateWidgetService.class);
		context.startService(intent);
	}
	/**
	 * 最后一个widget移除的时候调用
	 */
	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
		
		Intent intent = new Intent(context, UpdateWidgetService.class);
		context.stopService(intent);
	}
}
