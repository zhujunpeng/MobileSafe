package cn.edu.cqu.mobilesafe;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.edu.cqu.mobilesafe.domain.AppInfo;
import cn.edu.cqu.mobilesafe.engine.AppInfoProvider;

public class AppManagerActivity extends Activity {
	
	private TextView tv_avail_rom;
	private TextView tv_avail_sd;
	private ListView lv_app_manage;
	private LinearLayout ll_loading;
	/**
	 * ȫ����Ӧ�ó�����Ϣ
	 */
	private List<AppInfo> appInfos;
	/**
	 * �û���Ӧ�ó�����Ϣ
	 */
	private List<AppInfo> userAppInfos;
	/**
	 * ϵͳ��Ӧ�ó�����Ϣ
	 */
	private List<AppInfo> systemAppInfos;
	private AppInfoManageAdapter adapter;
	private TextView tv_number;
	
	private PopupWindow popupWindow;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_manager);
		
		tv_avail_rom = (TextView) findViewById(R.id.tv_avail_rom);
		tv_avail_sd = (TextView) findViewById(R.id.tv_avail_sd);
		lv_app_manage = (ListView) findViewById(R.id.lv_app_manage);
		ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
		tv_number = (TextView) findViewById(R.id.tv_number);
		
		long sdSize = getAvailSpace(Environment.getExternalStorageDirectory().getPath());
		long romSize = getAvailSpace(Environment.getDataDirectory().getPath());
		
		tv_avail_sd.setText("SD�����ÿռ䣺" + Formatter.formatFileSize(this, sdSize));
		tv_avail_rom.setText("�ڴ���ÿռ䣺" + Formatter.formatFileSize(this, romSize));
		
		// ���ذ�װ��Ӧ�ó�����Ϣ
		ll_loading.setVisibility(View.VISIBLE);
		// ��ʹ�Ĳ���Ҫʹ�����߳�
		new Thread(new Runnable() {

			@Override
			public void run() {
				appInfos = AppInfoProvider.getAppInfos(AppManagerActivity.this);
				userAppInfos = new ArrayList<AppInfo>();
				systemAppInfos = new ArrayList<AppInfo>();
				for (AppInfo appInfo : appInfos) {
					if (appInfo.isUserApp()) {
						userAppInfos.add(appInfo);
					}else {
						systemAppInfos.add(appInfo);
					}
				}
				
				runOnUiThread(new Runnable() {
					public void run() {
						adapter = new AppInfoManageAdapter();
						lv_app_manage.setAdapter(adapter);
						ll_loading.setVisibility(View.INVISIBLE);
					}
				});
			}
		}).start();
		
		lv_app_manage.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
			}
			
			// ��listView�϶�ʱ����
			// firstVisibleItem ��һ���ɼ�����Ŀ��λ��
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// ������ʱ����������
				dismissPopupWindows();
				if (userAppInfos != null && systemAppInfos != null) {
					tv_number.setVisibility(View.VISIBLE);
					if (firstVisibleItem > userAppInfos.size()) {
						tv_number.setText("ϵͳ�������:" + systemAppInfos.size() + "��");
					}else {
						tv_number.setText("�û��������:" + userAppInfos.size() + "��");
					}
				}
			}
		});
		
		lv_app_manage.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				AppInfo appInfo;
				if (position == 0 || position == userAppInfos.size() + 1) {
					return;
				}else if (position <= userAppInfos.size()) {
					int newposition = position - 1;
					appInfo = userAppInfos.get(newposition);
				}else {
					int newposition = position - 1 - userAppInfos.size() -1;
					appInfo = systemAppInfos.get(newposition);
				}
				// �������һ����ʱ��ɾ������
				dismissPopupWindows();
				TextView contentView = new TextView(AppManagerActivity.this);
				contentView.setTextColor(Color.BLACK);
				contentView.setText(appInfo.getPakageName());
				popupWindow = new PopupWindow(contentView, -2, -2);
				popupWindow.setBackgroundDrawable(new ColorDrawable(Color.RED));
				int [] location = new int[2];
				view.getLocationInWindow(location);
				popupWindow.showAtLocation(parent, Gravity.LEFT | Gravity.TOP, location[0], location[1]);
				
				
			}
		});
	}
	
	private class AppInfoManageAdapter extends BaseAdapter{

		@Override
		public int getCount() {
//			return appInfos.size();
			return userAppInfos.size() + systemAppInfos.size() + 2;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			AppInfo appInfo = null;
			if (position == 0) {
				TextView tv = new TextView(getApplicationContext());
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				tv.setText("�û����������" + userAppInfos.size() + "��");
				return tv;
			}else if (position == (userAppInfos.size() + 1)) {
				TextView tv = new TextView(getApplicationContext());
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				tv.setText("ϵͳ���������" + systemAppInfos.size() + "��");
				return tv;
			}else if (position <= userAppInfos.size()) {
				int newposition = position - 1;
				appInfo = userAppInfos.get(newposition);
			}else {
				int newposition = position - 1 - userAppInfos.size() -1;
				appInfo = systemAppInfos.get(newposition);
			}
			View view;
			ViewHolder holder;
			// �жϸ���View�Ƿ�Ϊ�գ����ҷ��Ǻ��ʵ�����ȥ����
			if (convertView != null && convertView instanceof RelativeLayout) {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}else {
				view = View.inflate(AppManagerActivity.this, R.layout.list_item_appinfo, null);
				holder = new ViewHolder();
				holder.app_name = (TextView) view.findViewById(R.id.tv_app_name);
				holder.app_location = (TextView) view.findViewById(R.id.tv_app_location);
				holder.app_icon = (ImageView) view.findViewById(R.id.iv_app_icon);
				view.setTag(holder);
			}
//			AppInfo appInfo = appInfos.get(position);
//			AppInfo appInfo = null;
//			if (position <= userAppInfos.size()) {
//				appInfo = userAppInfos.get(position);
//			}else {
//				int newposition = position - userAppInfos.size();
//				appInfo = systemAppInfos.get(newposition);
//			}
			holder.app_icon.setImageDrawable(appInfo.getIcon());
			holder.app_name.setText(appInfo.getName());
			if (appInfo.isRomApp()) {
				holder.app_location.setText("�ֻ��ڴ�");
			}else {
				holder.app_location.setText("�ⲿ�洢");
			}
			return view;
		}
		
		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

	}
	
	private static class ViewHolder{
		private TextView app_name;
		private TextView app_location;
		private ImageView app_icon;
	}
	/**
	 * ��ȡָ��Ŀ¼�Ŀ��ÿռ�
	 * @param path
	 * @return
	 */
	private long getAvailSpace(String path){
		StatFs statFs = new StatFs(path);
		// ��ȡÿ�������Ĵ�С
		long size = statFs.getBlockSize();
		// ��ȡ������������
		long count = statFs.getAvailableBlocks();
		return count * size;
	}
	private void dismissPopupWindows() {
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
			popupWindow = null;
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		dismissPopupWindows();
	}
}
