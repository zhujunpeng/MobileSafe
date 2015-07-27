package cn.edu.cqu.mobilesafe;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.edu.cqu.mobilesafe.db.dao.BlackNumberDAO;
import cn.edu.cqu.mobilesafe.domain.BlackNumberInfo;

public class CallSmsActivity extends Activity {

	private ListView lv_callsms_safe;
	private List<BlackNumberInfo> infos;
	private BlackNumberDAO dao;
	private CallSmsSafeAdapter adapter;
	private LinearLayout ll_loading;
	private int offset = 0;
	private int maxnumber = 20;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_callsms);

		ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
		lv_callsms_safe = (ListView) findViewById(R.id.lv_callsms_safe);
		dao = new BlackNumberDAO(this);

		// 耗时操作放在子线程中
		// infos = dao.findAll();
		findblacknumber();

		// 注册一个滚动时间的监听器
		lv_callsms_safe.setOnScrollListener(new OnScrollListener() {
			// 当滚动状态放生改变时调用
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE: // 空闲状态
					// 判断当前listview滚动的位置
					// 获取最后一条可见条目在集合里面的位置
					int lastVisiblePosition = lv_callsms_safe
							.getLastVisiblePosition();
//					System.out.println("最后一个可见条目的位置---" + lastVisiblePosition);
					// 到了最后一个可见位置后继续查找
					if (lastVisiblePosition == infos.size() - 1) {
						offset += maxnumber;
						findblacknumber();
					}
					break;
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL: // 触摸状态

					break;
				case OnScrollListener.SCROLL_STATE_FLING: // 惯性滑行状态

					break;

				default:
					break;
				}
			}

			// 滚动时调用
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

			}
		});
	}

	private void findblacknumber() {
		ll_loading.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {

			@Override
			public void run() {
				if (infos == null) {
					infos = dao.findPart(offset, maxnumber);
				} else {
					infos.addAll(dao.findPart(offset, maxnumber));
				}
				runOnUiThread(new Runnable() {
					public void run() {
						ll_loading.setVisibility(View.INVISIBLE);
						if (adapter == null) {
							adapter = new CallSmsSafeAdapter();
							lv_callsms_safe.setAdapter(adapter);
						} else {
							// adapter存在的话，通知更新
							adapter.notifyDataSetChanged();
						}
					}
				});
			}
		}).start();
	}

	private class CallSmsSafeAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return infos.size();
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
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View view;
			ViewHolder holder;
			if (convertView == null) {
				view = View.inflate(getApplicationContext(),
						R.layout.list_item_balcknumber, null);
				holder = new ViewHolder();
				holder.tv_item_balck_number = (TextView) view
						.findViewById(R.id.tv_item_balck_number);
				holder.tv_item_balck_mode = (TextView) view
						.findViewById(R.id.tv_item_balck_mode);
				holder.iv_delete_number = (ImageView) view
						.findViewById(R.id.iv_delete_number);
				// 设置一个标示，当孩子生出来的时候找到他们的引用，存放在记事本里面，放在父亲的口袋
				view.setTag(holder);
			} else {
				view = convertView;
				holder = (ViewHolder) view.getTag();// 5%的效率提升
			}
			holder.tv_item_balck_number
					.setText(infos.get(position).getNumber());
			if ("1".equals(infos.get(position).getMode())) {
				holder.tv_item_balck_mode.setText("拦截电话");
			} else if ("2".equals(infos.get(position).getMode())) {
				holder.tv_item_balck_mode.setText("拦截短信");
			} else {
				holder.tv_item_balck_mode.setText("全部拦截");
			}
			holder.iv_delete_number.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// dao.delete(infos.get(position).getNumber());
					AlertDialog.Builder builder = new Builder(
							CallSmsActivity.this);
					builder.setTitle("删除黑名单");
					builder.setMessage("确定要删除该号码？");
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dao.delete(infos.get(position).getNumber());
									// 删除该数据
									infos.remove(position);
									// 通知数据适配器更新数据
									adapter.notifyDataSetChanged();
								}
							});
					builder.setNegativeButton("取消", null);
					builder.show();
				}
			});
			return view;
		}

	}

	/**
	 * view对象的容器，相当于一个记事本
	 * 
	 * @author Administrator 静态的字节码只加载了一次
	 */
	static class ViewHolder {
		TextView tv_item_balck_number;
		TextView tv_item_balck_mode;
		ImageView iv_delete_number;
	}

	/**
	 * 添加黑名单
	 * 
	 * @param view
	 */

	private EditText et_blacknumber;
	private CheckBox cb_phone;
	private CheckBox cb_sms;
	private Button bt_ok;
	private Button bt_cancel;

	public void add_black_number(View view) {
		AlertDialog.Builder builder = new Builder(this);
		final AlertDialog dialog = builder.create();
		View dialog_black_number = View.inflate(getApplicationContext(),
				R.layout.dialog_add_blacknumber, null);
		et_blacknumber = (EditText) dialog_black_number
				.findViewById(R.id.et_blacknumber);
		cb_phone = (CheckBox) dialog_black_number.findViewById(R.id.cb_phone);
		cb_sms = (CheckBox) dialog_black_number.findViewById(R.id.cb_sms);
		bt_ok = (Button) dialog_black_number.findViewById(R.id.btn_ok);
		bt_cancel = (Button) dialog_black_number.findViewById(R.id.btn_cancel);
		dialog.setView(dialog_black_number);
		dialog.show();
		bt_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		bt_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String blacknumber = et_blacknumber.getText().toString().trim();
				if (TextUtils.isEmpty(blacknumber)) {
					Toast.makeText(getApplicationContext(), "黑名单号码不能为空",
							Toast.LENGTH_SHORT).show();
					return;
				}
				String mode;
				if (cb_phone.isChecked() && cb_sms.isChecked()) {
					// 全部拦截
					mode = "3";
				} else if (cb_phone.isChecked()) {
					// 拦截电话
					mode = "1";
				} else if (cb_sms.isChecked()) {
					// 短信拦截
					mode = "2";
				} else {
					Toast.makeText(getApplicationContext(), "请选择拦截模式", 0)
							.show();
					return;
				}
				dao.add(blacknumber, mode);
				// 更新ListView中的内容
				BlackNumberInfo info = new BlackNumberInfo();
				info.setMode(mode);
				info.setNumber(blacknumber);
				// 加到了末尾哦
				// infos.add(info);
				infos.add(0, info);
				// 通知数据适配器更新数据
				adapter.notifyDataSetChanged();
				dialog.dismiss();
			}
		});
	}
}
