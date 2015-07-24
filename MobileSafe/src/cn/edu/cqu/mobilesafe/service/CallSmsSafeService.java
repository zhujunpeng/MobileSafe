package cn.edu.cqu.mobilesafe.service;

import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import cn.edu.cqu.mobilesafe.db.dao.BlackNumberDAO;

public class CallSmsSafeService extends Service {
	public static final String TAG = "CallSmsSafeService";
	private InnerSmsReceiver receiver;
	private BlackNumberDAO dao;
	private TelephonyManager tm;
	private MyListener listener;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private class InnerSmsReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG,"�ڲ��㲥�����ߣ� ���ŵ�����");
			//��鷢�����Ƿ��Ǻ��������룬���ö�������ȫ�����ء�
			Object[] objs = (Object[]) intent.getExtras().get("pdus");
			for(Object obj:objs){
				SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
				//�õ����ŷ�����
				String sender = smsMessage.getOriginatingAddress();
				String result = dao.findMode(sender);
				if("2".equals(result)||"3".equals(result)){
					Log.i(TAG,"���ض���");
					abortBroadcast();
					Log.i(TAG,"���ض���000");
				}
				//��ʾ���롣
				String body = smsMessage.getMessageBody();
				if(body.contains("fapiao")){
					//���ͷ��Ʊ���ĺ�  ���Էִʼ�����
					Log.i(TAG,"���ط�Ʊ����");
					abortBroadcast();
				}
			}
		}
	}
	
	@Override
	public void onCreate() {
		dao = new BlackNumberDAO(this);
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		receiver = new InnerSmsReceiver();
		IntentFilter filter =  new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		registerReceiver(receiver,filter);
		super.onCreate();
	}
	
	@Override
	public void onDestroy() {
		unregisterReceiver(receiver);
		receiver = null;
		
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		super.onDestroy();
	}
	
	private class MyListener extends PhoneStateListener{

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING://����״̬��
				String result = dao.findMode(incomingNumber);
				if("1".equals(result)||"3".equals(result)){
					Log.i(TAG,"�Ҷϵ绰��������");
					endCall();
					deleteCallLog(incomingNumber);
				}
				break;
			}
			
		}
	}

	public void endCall() {
		//IBinder iBinder = ServiceManager.getService(TELEPHONY_SERVICE);
		try {
			//����servicemanager���ֽ���
			Class clazz = CallSmsSafeService.class.getClassLoader().loadClass("android.os.ServiceManager");
			Method method = clazz.getDeclaredMethod("getService", String.class);
			IBinder ibinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);
			ITelephony.Stub.asInterface(ibinder).endCall();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ɾ��ͨ����¼
	 * @param incomingNumber
	 */
	public void deleteCallLog(String incomingNumber) {
		ContentResolver resolver = getContentResolver();
		Uri uri = Uri.parse("content://call_log/calls");
		resolver.delete(uri, "number=?", new String[]{incomingNumber});
	}
}
