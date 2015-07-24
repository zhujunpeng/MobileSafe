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
		// ��������ʱ
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

		// ע��㲥������
		receiver = new OutCallReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
		registerReceiver(receiver, filter);

		// ʵ����
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
			case TelephonyManager.CALL_STATE_RINGING: // ����ʱ
				// ����ʱȥ��ѯ���ݿ⣬�õ�������
				String address = NumberQueryUtils.queryNumber(incomingNumber);
				// Toast.makeText(getApplicationContext(), phone,
				// Toast.LENGTH_LONG).show();
				myToast(address);

				break;
			case TelephonyManager.CALL_STATE_IDLE:// ����״̬
				if (view != null) {
					// �Ƴ�view
					wm.removeView(view);
				}

			default:
				break;
			}
		}

	}

	/**
	 * �㲥���������ڸ�����һ����
	 * 
	 * @author Administrator
	 * 
	 */
	class OutCallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// �õ������ĵ绰����
			String phone = getResultData();
			// ��ѯ���ݿ�
			String address = NumberQueryUtils.queryNumber(phone);
			Toast.makeText(context, address, Toast.LENGTH_LONG).show();
			// �Զ���Toast
			myToast(address);
		}

	}

	/**
	 * �Զ������˾
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
				// ������λص��м�λ��
				System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
				mHits[mHits.length - 1] = SystemClock.uptimeMillis();
				if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
					// ˫�������ˡ�����
					params.x = wm.getDefaultDisplay().getWidth()/2-view.getWidth()/2;
					wm.updateViewLayout(view, params);
					Editor editor = sp.edit();
					editor.putInt("lastx", params.x);
					editor.commit();
				}
			}
		});
		text.setText(address);

		// ��View����һ�������ļ�����
		view.setOnTouchListener(new OnTouchListener() {

			int startX;
			int startY;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:// ��ָ����
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					Log.i(TAG, "��ָ��������Ļ");
					break;
				case MotionEvent.ACTION_MOVE: //��ָ����Ļ���ƶ�
					int newX = (int) event.getRawX();
					int newY = (int) event.getRawY();
					// ƫ����
					int dx = newX - startX;
					int dy = newY - startY;
					Log.i(TAG, "��ָ������Ļ");
					params.x += dx;
					params.y += dy;
					
					// �߽�����
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
					// ����λ��
					wm.updateViewLayout(view, params);
					// ��ʼ���µ�λ��
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_UP:
					Log.i(TAG, "��ָ�뿪��Ļ");
					// ��¼�ؼ�����Ļ�ϵľ���
					Editor editor = sp.edit();
					editor.putInt("lastX", (int) event.getRawX());
					editor.putInt("lastY", (int) event.getRawY());
					editor.commit();
					break;

				default:
					break;
				}
				// ����true���ø��ؼ���Ҫ����Ӧ�¼���
				return true;
			}
		});

		params = new WindowManager.LayoutParams();
		// z���ô���Ĳ���
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;

		// �޸Ŀؼ�����Ļ�ϵ�λ��
		params.gravity = Gravity.TOP + Gravity.LEFT;
		// �ؼ���xy��ֵ
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
