package cn.edu.cqu.mobilesafe.receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import cn.edu.cqu.mobilesafe.service.UpdateWidgetService;

public class MyWidget extends AppWidgetProvider {

	/**
	 * ÿ�β���widget��ʱ�򶼻����
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
	 * ��һ��widget������ʱ�����
	 */
	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		
		Intent intent = new Intent(context, UpdateWidgetService.class);
		context.startService(intent);
	}
	/**
	 * ���һ��widget�Ƴ���ʱ�����
	 */
	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
		
		Intent intent = new Intent(context, UpdateWidgetService.class);
		context.stopService(intent);
	}
}
