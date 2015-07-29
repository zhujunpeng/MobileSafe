package cn.edu.cqu.mobilesafe;

import java.text.Format;
import java.util.ArrayList;
import java.util.List;

import cn.edu.cqu.mobilesafe.domain.TaskInfo;
import cn.edu.cqu.mobilesafe.engine.TaskInfoProvider;
import cn.edu.cqu.mobilesafe.utils.SystemInfosUtils;
import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TaskManagerActivity extends Activity {

	private TextView tv_running_progress;
	private TextView tv_story_status;
	private LinearLayout ll_loading;
	private ListView lv_app_taskmanager;
	private TextView tv_number;
	
	private List<TaskInfo> allTaskInfos;
	private List<TaskInfo> userTaskInfos;
	private List<TaskInfo> systemTaskInfos;
	
	private TaskInfoManageAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_manager);

		tv_running_progress = (TextView) findViewById(R.id.tv_running_progress);
		tv_story_status = (TextView) findViewById(R.id.tv_story_status);
		lv_app_taskmanager = (ListView) findViewById(R.id.lv_app_taskmanager);
		ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
		tv_number = (TextView) findViewById(R.id.tv_number);

		int progressCount = SystemInfosUtils.getRunningProgressCount(this);
		tv_running_progress.setText("正在运行的程序:" + progressCount + "个");
		long availMen = SystemInfosUtils.getAvailMen(this);
		long totalMen = SystemInfosUtils.getTotalMen(this);
		tv_story_status.setText("剩余/总内存:"
				+ Formatter.formatFileSize(this, availMen) + "/"
				+ Formatter.formatFileSize(this, totalMen));
		
		fillData();
		lv_app_taskmanager.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (userTaskInfos != null && systemTaskInfos != null) {
					if (firstVisibleItem < userTaskInfos.size()) {
						tv_number.setText("用户程序：" + userTaskInfos.size() + "个");
					}else {
						tv_number.setText("系统程序：" + systemTaskInfos.size() + "个");
					}
				}
			}
		});
		
		lv_app_taskmanager.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TaskInfo taskInfo;
				if (position == 0) {// 显示用户程序个数
					return ;
				}else if (position == (userTaskInfos.size() + 1)) {
					return;
				}else if (position <= userTaskInfos.size()) {
					int newpostion = position - 1;
					taskInfo = userTaskInfos.get(newpostion);
				}else {
					int newpostion = position - 1 - userTaskInfos.size() - 1;
					taskInfo = systemTaskInfos.get(newpostion);
				}
				ViewHolder holder = (ViewHolder) view.getTag();
				if (taskInfo.isChecked()) {
					holder.cb_status.setChecked(false);
					taskInfo.setChecked(false);
				}else {
					holder.cb_status.setChecked(true);
					taskInfo.setChecked(true);
				}
			}
		});
	}

	/**
	 * 给listView填充数据
	 */
	private void fillData() {
		ll_loading.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				allTaskInfos = TaskInfoProvider.getTaskInfos(getApplicationContext());
				userTaskInfos = new ArrayList<TaskInfo>();
				systemTaskInfos = new ArrayList<TaskInfo>();
				for (TaskInfo taskInfo : allTaskInfos) {
					if (taskInfo.isUserApp()) {
						// 用户程序
						userTaskInfos.add(taskInfo);
					}else {
						// 系统程序
						systemTaskInfos.add(taskInfo);
					}
				}
				runOnUiThread(new  Runnable() {
					public void run() {
						adapter = new TaskInfoManageAdapter();
						lv_app_taskmanager.setAdapter(adapter);
						tv_number.setVisibility(View.VISIBLE);
						ll_loading.setVisibility(View.INVISIBLE);
					}
				});
			}
		}).start();
		
	}
	
	private class TaskInfoManageAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return userTaskInfos.size() + 1 + systemTaskInfos.size() + 1;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TaskInfo taskInfo;
			if (position == 0) {// 显示用户程序个数
				TextView tv = new TextView(getApplicationContext());
				tv.setBackgroundColor(Color.GRAY);
				tv.setTextColor(Color.WHITE);
				tv.setTextSize(20);
				tv.setText("用户程序：" + userTaskInfos.size() + "个");
				return tv;
			}else if (position == (userTaskInfos.size() + 1)) {
				TextView tv = new TextView(getApplicationContext());
				tv.setBackgroundColor(Color.GRAY);
				tv.setTextColor(Color.WHITE);
				tv.setTextSize(20);
				tv.setText("系统程序：" + systemTaskInfos.size() + "个");
				return tv;
			}else if (position <= userTaskInfos.size()) {
				int newpostion = position - 1;
				taskInfo = userTaskInfos.get(newpostion);
			}else {
				int newpostion = position - 1 - userTaskInfos.size() - 1;
				taskInfo = systemTaskInfos.get(newpostion);
			}
			
			View view ;
			ViewHolder holder;
			if (convertView != null && convertView instanceof RelativeLayout) {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}else {
				view = View.inflate(getApplicationContext(), R.layout.list_item_taskmanager, null);
				holder = new ViewHolder();
				holder.iv_app_icon = (ImageView) view.findViewById(R.id.iv_app_icon);
				holder.tv_app_name = (TextView) view.findViewById(R.id.tv_app_name);
				holder.tv_app_appmem = (TextView) view.findViewById(R.id.tv_app_appmem);
				holder.cb_status = (CheckBox) view.findViewById(R.id.cb_status);
				view.setTag(holder);
			}
			holder.iv_app_icon.setImageDrawable(taskInfo.getIcon());
			holder.tv_app_name.setText(taskInfo.getName());
			holder.tv_app_appmem.setText("内存占用："+Formatter.formatFileSize(getApplicationContext(), taskInfo.getAppMem()));
			holder.cb_status.setChecked(taskInfo.isChecked());
			return view;
		}
		
	}
	
	static class ViewHolder{
		ImageView iv_app_icon;
		TextView tv_app_name;
		TextView tv_app_appmem;
		CheckBox cb_status;
	}

}
