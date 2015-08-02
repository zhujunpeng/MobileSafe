package cn.edu.cqu.mobilesafe;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.edu.cqu.mobilesafe.db.dao.AppLockDAO;
import cn.edu.cqu.mobilesafe.domain.AppInfo;
import cn.edu.cqu.mobilesafe.engine.AppInfoProvider;
import cn.edu.cqu.mobilesafe.utils.DensityUtil;

public class AppManagerActivity extends Activity implements OnClickListener {

	private static final String TAG = "AppManagerActivity";
	private TextView tv_avail_rom;
	private TextView tv_avail_sd;
	private ListView lv_app_manage;
	private LinearLayout ll_loading;
	/**
	 * 全部的应用程序信息
	 */
	private List<AppInfo> appInfos;
	/**
	 * 用户的应用程序信息
	 */
	private List<AppInfo> userAppInfos;
	/**
	 * 系统的应用程序信息
	 */
	private List<AppInfo> systemAppInfos;
	private AppInfoManageAdapter adapter;
	private TextView tv_number;
	// 弹出的窗体
	private PopupWindow popupWindow;
	/**
	 * 启动应用
	 */
	private LinearLayout ll_start;
	/**
	 * 分享应用
	 */
	private LinearLayout ll_share;
	/**
	 * 卸载应用
	 */
	private LinearLayout ll_uninstall;
	private AppInfo appInfo = null;
	private AppLockDAO appLockDAO;

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
		
		appLockDAO = new AppLockDAO(this);

		long sdSize = getAvailSpace(Environment.getExternalStorageDirectory()
				.getPath());
		long romSize = getAvailSpace(Environment.getDataDirectory().getPath());

		tv_avail_sd
				.setText("SD卡可用空间：" + Formatter.formatFileSize(this, sdSize));
		tv_avail_rom.setText("内存可用空间："
				+ Formatter.formatFileSize(this, romSize));

		// 获取应用程序数据
		fillData();

		lv_app_manage.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			// 当listView拖动时调用
			// firstVisibleItem 第一个可见的条目的位置
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// 滑动的时候消除窗体
				dismissPopupWindows();
				if (userAppInfos != null && systemAppInfos != null) {
					tv_number.setVisibility(View.VISIBLE);
					if (firstVisibleItem > userAppInfos.size()) {
						tv_number.setText("系统程序个数:" + systemAppInfos.size()
								+ "个");
					} else {
						tv_number.setText("用户程序个数:" + userAppInfos.size() + "个");
					}
				}
			}
		});

		lv_app_manage.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
//				System.out.println("位置---" + position);
				if (position == 0 || position == userAppInfos.size() + 1) {
					return;
				} else if (position <= userAppInfos.size()) {
					int newposition = position - 1;
					appInfo = userAppInfos.get(newposition);
					System.out.println("用户appInfo---" + appInfo.toString());
				} else {
					int newposition = position - 1 - userAppInfos.size() - 1;
					appInfo = systemAppInfos.get(newposition);
					System.out.println("系统appInfo---" + appInfo.toString());
				}
				// 窗体存在一个的时候，删除窗体
				dismissPopupWindows();
				View contentView = View.inflate(getApplicationContext(),
						R.layout.popup_item, null);
				// 初始化控件
				ll_start = (LinearLayout) contentView
						.findViewById(R.id.ll_start);
				ll_share = (LinearLayout) contentView
						.findViewById(R.id.ll_share);
				ll_uninstall = (LinearLayout) contentView
						.findViewById(R.id.ll_uninstall);
				// 绑定监听事件
				ll_start.setOnClickListener(AppManagerActivity.this);
				ll_share.setOnClickListener(AppManagerActivity.this);
				ll_uninstall.setOnClickListener(AppManagerActivity.this);

				popupWindow = new PopupWindow(contentView, -2, -2);
				// popupWindow 动画效果必须要有背景才能实现
				popupWindow.setBackgroundDrawable(new ColorDrawable(
						Color.TRANSPARENT));
				int[] location = new int[2];
				view.getLocationInWindow(location);
				// 将像素转为dp
				int dip = DensityUtil.px2dip(getApplicationContext(), 100);
				popupWindow.showAtLocation(parent, Gravity.LEFT | Gravity.TOP,
						dip, location[1]);
				// 缩放的动画效果
				ScaleAnimation sa = new ScaleAnimation(0.3f, 1.0f, 0.3f, 1.0f,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0);
				sa.setDuration(300);
				// 渐变的动画
				AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
				aa.setDuration(300);
				AnimationSet set = new AnimationSet(false);
				set.addAnimation(aa);
				set.addAnimation(sa);
				contentView.setAnimation(sa);
			}
		});
		
		lv_app_manage.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0 || position == userAppInfos.size() + 1) {
					return true;
				} else if (position <= userAppInfos.size()) {
					int newposition = position - 1;
					appInfo = userAppInfos.get(newposition);
					System.out.println("用户appInfo---" + appInfo.toString());
				} else {
					int newposition = position - 1 - userAppInfos.size() - 1;
					appInfo = systemAppInfos.get(newposition);
					System.out.println("系统appInfo---" + appInfo.toString());
				}
				ViewHolder holder = (ViewHolder) view.getTag();
				if(appLockDAO.find(appInfo.getPakageName())){
					// 数据库中存在，删除记录，修改ui界面为未锁定
					appLockDAO.delete(appInfo.getPakageName());
					holder.iv_lock_status.setImageResource(R.drawable.unlock);
					Toast.makeText(getApplicationContext(), appInfo.getName() + "解除锁定", Toast.LENGTH_SHORT).show();
				}else {
					// 数据库中不存在，添加记录，修改ui界面为锁定
					appLockDAO.add(appInfo.getPakageName());
					holder.iv_lock_status.setImageResource(R.drawable.lock);
					Toast.makeText(getApplicationContext(), appInfo.getName() + "锁定", Toast.LENGTH_SHORT).show();
				}
				return true;
			}
		});
	}

	/**
	 * 获取应用程序数据
	 */
	private void fillData() {
		// 加载安装的应用程序信息
		ll_loading.setVisibility(View.VISIBLE);
		// 好使的操作要使用子线程
		new Thread(new Runnable() {

			@Override
			public void run() {
				appInfos = AppInfoProvider.getAppInfos(AppManagerActivity.this);
				userAppInfos = new ArrayList<AppInfo>();
				systemAppInfos = new ArrayList<AppInfo>();
				for (AppInfo appInfo : appInfos) {
					if (appInfo.isUserApp()) {
						userAppInfos.add(appInfo);
					} else {
						systemAppInfos.add(appInfo);
					}
				}

				runOnUiThread(new Runnable() {
					public void run() {
						if (adapter == null) {
							adapter = new AppInfoManageAdapter();
							lv_app_manage.setAdapter(adapter);
						}else {
							// 通知数据发生改变
							adapter.notifyDataSetChanged();
						}
						ll_loading.setVisibility(View.INVISIBLE);
					}
				});
			}
		}).start();
	}

	private class AppInfoManageAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// return appInfos.size();
			return userAppInfos.size() +1 + systemAppInfos.size() + 1;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			if (position == 0) {
				TextView tv = new TextView(getApplicationContext());
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				tv.setText("用户程序个数：" + userAppInfos.size() + "个");
				return tv;
			} else if (position == (userAppInfos.size() + 1)) {
				TextView tv = new TextView(getApplicationContext());
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				tv.setText("系统程序个数：" + systemAppInfos.size() + "个");
				return tv;
			} else if (position <= userAppInfos.size()) {
				int newposition = position - 1;
				appInfo = userAppInfos.get(newposition);
			} else {
				int newposition = position - 1 - userAppInfos.size() - 1;
				appInfo = systemAppInfos.get(newposition);
			}
			View view;
			ViewHolder holder;
			// 判断复用View是否为空，并且否是合适的类型去复用
			if (convertView != null && convertView instanceof RelativeLayout) {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(AppManagerActivity.this,
						R.layout.list_item_appinfo, null);
				holder = new ViewHolder();
				holder.app_name = (TextView) view
						.findViewById(R.id.tv_app_name);
				holder.app_location = (TextView) view
						.findViewById(R.id.tv_app_location);
				holder.app_icon = (ImageView) view
						.findViewById(R.id.iv_app_icon);
				holder.iv_lock_status = (ImageView) view.findViewById(R.id.iv_lock_status);
				view.setTag(holder);
			}
			holder.app_icon.setImageDrawable(appInfo.getIcon());
			holder.app_name.setText(appInfo.getName());
			if (appInfo.isRomApp()) {
				holder.app_location.setText("手机内存");
			} else {
				holder.app_location.setText("外部存储");
			}
			// 程序锁的界面
			if(appLockDAO.find(appInfo.getPakageName())){
				holder.iv_lock_status.setImageResource(R.drawable.lock);
			}else {
				holder.iv_lock_status.setImageResource(R.drawable.unlock);
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

	private static class ViewHolder {
		private TextView app_name;
		private TextView app_location;
		private ImageView app_icon;
		private ImageView iv_lock_status;
	}

	/**
	 * 获取指定目录的可用空间
	 * 
	 * @param path
	 * @return
	 */
	private long getAvailSpace(String path) {
		StatFs statFs = new StatFs(path);
		// 获取每个扇区的大小
		long size = statFs.getBlockSize();
		// 获取可用扇区数量
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

	@Override
	public void onClick(View v) {
		dismissPopupWindows();
		switch (v.getId()) {
		case R.id.ll_start:
			startApplication();
//			Log.i(TAG, "打开---" + appInfo.getPakageName());
			break;
		case R.id.ll_share:
//			Log.i(TAG, "分享---" + appInfo.getPakageName());
			shareApplication();
			break;
		case R.id.ll_uninstall:
//			Log.i(TAG, "卸载---" + appInfo.getPakageName());
			if (appInfo.isUserApp()) {
				uninstallApplication();
			}else {
				Toast.makeText(this, "系统应用必须获取root权限才可卸载", Toast.LENGTH_SHORT).show();
			}
			break;

		default:
			break;
		}

	}

	/**
	 * 分享应用
	 */
	private void shareApplication() {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.SEND");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, "推荐您使用一款软件，名称是：" + appInfo.getName());
		startActivity(intent);
	}

	/**
	 * 卸载应用程序
	 */
	private void uninstallApplication() {
//		<action android:name="android.intent.action.VIEW" />
//        <action android:name="android.intent.action.DELETE" />
//        <category android:name="android.intent.category.DEFAULT" />
//        <data android:scheme="package" />
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		intent.setAction("android.intent.action.DELETE");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setData(Uri.parse("package:" + appInfo.getPakageName()));
//		startActivity(intent);
		startActivityForResult(intent, 0);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 重新获取应用程序数据
		fillData();
	}

	/**
	 * 启动应用程序
	 */
	private void startApplication() {
		PackageManager pm = getPackageManager();
		Intent intent = pm.getLaunchIntentForPackage(appInfo.getPakageName());
		if (intent != null) {
			startActivity(intent);
		}else {
			Toast.makeText(AppManagerActivity.this, "该应用无法打开", Toast.LENGTH_SHORT).show();
		}
	}
}
