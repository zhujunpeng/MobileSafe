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
	// �����Ĵ���
	private PopupWindow popupWindow;
	/**
	 * ����Ӧ��
	 */
	private LinearLayout ll_start;
	/**
	 * ����Ӧ��
	 */
	private LinearLayout ll_share;
	/**
	 * ж��Ӧ��
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
				.setText("SD�����ÿռ䣺" + Formatter.formatFileSize(this, sdSize));
		tv_avail_rom.setText("�ڴ���ÿռ䣺"
				+ Formatter.formatFileSize(this, romSize));

		// ��ȡӦ�ó�������
		fillData();

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
						tv_number.setText("ϵͳ�������:" + systemAppInfos.size()
								+ "��");
					} else {
						tv_number.setText("�û��������:" + userAppInfos.size() + "��");
					}
				}
			}
		});

		lv_app_manage.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
//				System.out.println("λ��---" + position);
				if (position == 0 || position == userAppInfos.size() + 1) {
					return;
				} else if (position <= userAppInfos.size()) {
					int newposition = position - 1;
					appInfo = userAppInfos.get(newposition);
					System.out.println("�û�appInfo---" + appInfo.toString());
				} else {
					int newposition = position - 1 - userAppInfos.size() - 1;
					appInfo = systemAppInfos.get(newposition);
					System.out.println("ϵͳappInfo---" + appInfo.toString());
				}
				// �������һ����ʱ��ɾ������
				dismissPopupWindows();
				View contentView = View.inflate(getApplicationContext(),
						R.layout.popup_item, null);
				// ��ʼ���ؼ�
				ll_start = (LinearLayout) contentView
						.findViewById(R.id.ll_start);
				ll_share = (LinearLayout) contentView
						.findViewById(R.id.ll_share);
				ll_uninstall = (LinearLayout) contentView
						.findViewById(R.id.ll_uninstall);
				// �󶨼����¼�
				ll_start.setOnClickListener(AppManagerActivity.this);
				ll_share.setOnClickListener(AppManagerActivity.this);
				ll_uninstall.setOnClickListener(AppManagerActivity.this);

				popupWindow = new PopupWindow(contentView, -2, -2);
				// popupWindow ����Ч������Ҫ�б�������ʵ��
				popupWindow.setBackgroundDrawable(new ColorDrawable(
						Color.TRANSPARENT));
				int[] location = new int[2];
				view.getLocationInWindow(location);
				// ������תΪdp
				int dip = DensityUtil.px2dip(getApplicationContext(), 100);
				popupWindow.showAtLocation(parent, Gravity.LEFT | Gravity.TOP,
						dip, location[1]);
				// ���ŵĶ���Ч��
				ScaleAnimation sa = new ScaleAnimation(0.3f, 1.0f, 0.3f, 1.0f,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0);
				sa.setDuration(300);
				// ����Ķ���
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
					System.out.println("�û�appInfo---" + appInfo.toString());
				} else {
					int newposition = position - 1 - userAppInfos.size() - 1;
					appInfo = systemAppInfos.get(newposition);
					System.out.println("ϵͳappInfo---" + appInfo.toString());
				}
				ViewHolder holder = (ViewHolder) view.getTag();
				if(appLockDAO.find(appInfo.getPakageName())){
					// ���ݿ��д��ڣ�ɾ����¼���޸�ui����Ϊδ����
					appLockDAO.delete(appInfo.getPakageName());
					holder.iv_lock_status.setImageResource(R.drawable.unlock);
					Toast.makeText(getApplicationContext(), appInfo.getName() + "�������", Toast.LENGTH_SHORT).show();
				}else {
					// ���ݿ��в����ڣ���Ӽ�¼���޸�ui����Ϊ����
					appLockDAO.add(appInfo.getPakageName());
					holder.iv_lock_status.setImageResource(R.drawable.lock);
					Toast.makeText(getApplicationContext(), appInfo.getName() + "����", Toast.LENGTH_SHORT).show();
				}
				return true;
			}
		});
	}

	/**
	 * ��ȡӦ�ó�������
	 */
	private void fillData() {
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
							// ֪ͨ���ݷ����ı�
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
				tv.setText("�û����������" + userAppInfos.size() + "��");
				return tv;
			} else if (position == (userAppInfos.size() + 1)) {
				TextView tv = new TextView(getApplicationContext());
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				tv.setText("ϵͳ���������" + systemAppInfos.size() + "��");
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
			// �жϸ���View�Ƿ�Ϊ�գ����ҷ��Ǻ��ʵ�����ȥ����
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
				holder.app_location.setText("�ֻ��ڴ�");
			} else {
				holder.app_location.setText("�ⲿ�洢");
			}
			// �������Ľ���
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
	 * ��ȡָ��Ŀ¼�Ŀ��ÿռ�
	 * 
	 * @param path
	 * @return
	 */
	private long getAvailSpace(String path) {
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

	@Override
	public void onClick(View v) {
		dismissPopupWindows();
		switch (v.getId()) {
		case R.id.ll_start:
			startApplication();
//			Log.i(TAG, "��---" + appInfo.getPakageName());
			break;
		case R.id.ll_share:
//			Log.i(TAG, "����---" + appInfo.getPakageName());
			shareApplication();
			break;
		case R.id.ll_uninstall:
//			Log.i(TAG, "ж��---" + appInfo.getPakageName());
			if (appInfo.isUserApp()) {
				uninstallApplication();
			}else {
				Toast.makeText(this, "ϵͳӦ�ñ����ȡrootȨ�޲ſ�ж��", Toast.LENGTH_SHORT).show();
			}
			break;

		default:
			break;
		}

	}

	/**
	 * ����Ӧ��
	 */
	private void shareApplication() {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.SEND");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, "�Ƽ���ʹ��һ������������ǣ�" + appInfo.getName());
		startActivity(intent);
	}

	/**
	 * ж��Ӧ�ó���
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
		// ���»�ȡӦ�ó�������
		fillData();
	}

	/**
	 * ����Ӧ�ó���
	 */
	private void startApplication() {
		PackageManager pm = getPackageManager();
		Intent intent = pm.getLaunchIntentForPackage(appInfo.getPakageName());
		if (intent != null) {
			startActivity(intent);
		}else {
			Toast.makeText(AppManagerActivity.this, "��Ӧ���޷���", Toast.LENGTH_SHORT).show();
		}
	}
}
