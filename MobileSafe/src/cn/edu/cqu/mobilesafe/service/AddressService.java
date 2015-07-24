package cn.edu.cqu.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import cn.edu.cqu.mobilesafe.R;
import cn.edu.cqu.mobilesafe.db.dao.NumberQueryUtils;

public class AddressService extends Service {

	protected static final String TAG = "AddressService";
	private TelephonyManager tm;
	private MyPhoneStateListener listener;
	private OutCallReceiver receiver;
	private WindowManager wm;
	private View view;
	private WindowManager.LayoutParams params;
	private SharedPreferences sp;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyPhoneStateListener();
		// 监听响铃时
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

		// 注册广播接收器
		receiver = new OutCallReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
		registerReceiver(receiver, filter);

		// 实例化
		wm = (WindowManager) getSystemService(WINDOW_SERVICE);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener = null;
		
		unregisterReceiver(receiver);
		receiver = null;
	}

	private class MyPhoneStateListener extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING: // 响铃时
				// 响铃时去查询数据库，得到归属地
				String address = NumberQueryUtils.queryNumber(incomingNumber);
				// Toast.makeText(getApplicationContext(), phone,
				// Toast.LENGTH_LONG).show();
				myToast(address);

				break;
			case TelephonyManager.CALL_STATE_IDLE:// 空闲状态
				if (view != null) {
					// 移除view
					wm.removeView(view);
				}

			default:
				break;
			}
		}

	}

	/**
	 * 广播的生命周期跟服务一样了
	 * 
	 * @author Administrator
	 * 
	 */
	class OutCallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 得到拨出的电话号码
			String phone = getResultData();
			// 查询数据库
			String address = NumberQueryUtils.queryNumber(phone);
			Toast.makeText(context, address, Toast.LENGTH_LONG).show();
			// 自定义Toast
			myToast(address);
		}

	}

	/**
	 * 自定义的吐司
	 * 
	 * @param address
	 */
	long[] mHits = new long[2];
	public void myToast(String address) {
		view = View.inflate(getApplicationContext(), R.layout.address_show,
				null);
		TextView text = (TextView) view.findViewById(R.id.tv_address_show);

		int ids[] = { R.drawable.call_locate_white,
				R.drawable.call_locate_orange, R.drawable.call_locate_blue,
				R.drawable.call_locate_gray, R.drawable.call_locate_green };
		sp = getSharedPreferences("config", MODE_PRIVATE);
		int which = sp.getInt("which", 0);
		view.setBackgroundResource(ids[which]);
		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 点击两次回到中间位置
				System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
				mHits[mHits.length - 1] = SystemClock.uptimeMillis();
				if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
					// 双击居中了。。。
					params.x = wm.getDefaultDisplay().getWidth()/2-view.getWidth()/2;
					wm.updateViewLayout(view, params);
					Editor editor = sp.edit();
					editor.putInt("lastx", params.x);
					editor.commit();
				}
			}
		});
		text.setText(address);

		// 给View设置一个触摸的监听器
		view.setOnTouchListener(new OnTouchListener() {

			int startX;
			int startY;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:// 手指按下
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					Log.i(TAG, "手指触摸到屏幕");
					break;
				case MotionEvent.ACTION_MOVE: //手指在屏幕上移动
					int newX = (int) event.getRawX();
					int newY = (int) event.getRawY();
					// 偏移量
					int dx = newX - startX;
					int dy = newY - startY;
					Log.i(TAG, "手指按下屏幕");
					params.x += dx;
					params.y += dy;
					
					// 边界问题
					if (params.x < 0) {
						params.x = 0;
					}
					if (params.y < 0) {
						params.y = 0;
					}
					if (params.x > wm.getDefaultDisplay().getWidth() - view.getWidth()) {
						params.x = wm.getDefaultDisplay().getWidth() - view.getWidth();
					}
					if (params.y > wm.getDefaultDisplay().getHeight() - view.getHeight()) {
						params.y = wm.getDefaultDisplay().getHeight() - view.getHeight();
					}
					// 更新位置
					wm.updateViewLayout(view, params);
					// 初始化新的位置
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_UP:
					Log.i(TAG, "手指离开屏幕");
					// 记录控件在屏幕上的距离
					Editor editor = sp.edit();
					editor.putInt("lastX", (int) event.getRawX());
					editor.putInt("lastY", (int) event.getRawY());
					editor.commit();
					break;

				default:
					break;
				}
				// 返回true，让父控件不要在响应事件了
				return true;
			}
		});

		params = new WindowManager.LayoutParams();
		// z设置窗体的参数
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;

		// 修改控件在屏幕上的位置
		params.gravity = Gravity.TOP + Gravity.LEFT;
		// 控件的xy的值
		params.x = sp.getInt("lastX", 0);
		params.y = sp.getInt("lastY", 0);
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.format = PixelFormat.TRANSLUCENT;
		params.type = WindowManager.LayoutParams.TYPE_TOAST;
		params.setTitle("Toast");
		wm.addView(view, params);
	}
}
