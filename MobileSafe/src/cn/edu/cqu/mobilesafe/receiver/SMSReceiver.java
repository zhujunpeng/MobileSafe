package cn.edu.cqu.mobilesafe.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import cn.edu.cqu.mobilesafe.R;
import cn.edu.cqu.mobilesafe.service.GPSService;

public class SMSReceiver extends BroadcastReceiver {

	private static final String TAG = "SMSReceiver";
	private SharedPreferences sp;
	// �豸���Է���
	private DevicePolicyManager dpm;
	private ComponentName mDeviceAdminSample;
	private Context mContext;

	@Override
	public void onReceive(Context context, Intent intent) {
		
		dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		mDeviceAdminSample = new ComponentName(context, MyAdmin.class);
		mContext = context;
//		openadmin();
		
		// ���ն��ŵĴ���
		Object[] objs = (Object[]) intent.getExtras().get("pdus");
		for (Object object : objs) {
			// �����ĳһ������
			SmsMessage sms = SmsMessage.createFromPdu((byte[]) object);
			// ������
			String sender = sms.getOriginatingAddress();
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
			String safephone = sp.getString("phone", null);
			if (sender.contains(safephone)) {
				// ��������
				String body = sms.getMessageBody();
				if ("#*location*#".equals(body)) {
					// �õ��ֻ���GPS
					Log.i(TAG, "�õ��ֻ���GPS");
					// ��������
					Intent i = new Intent(context,GPSService.class);
					context.startService(i);
					SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
					String lastLocation = sp.getString("lastLocation", null);
					Log.i(TAG, lastLocation);
					if (TextUtils.isEmpty(lastLocation)) {
						// λ��û�еõ�
						SmsManager.getDefault().sendTextMessage(sender, null, "getting location...", null, null);
					}else {
						SmsManager.getDefault().sendTextMessage(sender, null, lastLocation, null, null);
					}
					// ������㲥��ֹ��
					abortBroadcast();
				}else if ("#*alarm*#".equals(body)) {
					Log.i(TAG, "���ű�������");
					MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
					// ѭ������
					player.setLooping(false);
					player.setVolume(1.0f, 1.0f);
					player.start();
					abortBroadcast();
				}else if ("#*wipedata*#".equals(body)) {
					Log.i(TAG, "�������");
					// ���sdcard�ϵ�����
//		        	dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
		        	// �ָ�����������
//		        	dpm.wipeData(0);
					abortBroadcast();
				}else if ("#*lockscreen*#".equals(body)) {
					Log.i(TAG, "Զ������");
					
					openadmin();
					lockscreem();
					abortBroadcast();
				}
			}
		}
	}
	public void openadmin(){
    	// ����һ��intent
    	Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "��硣����������Ҫ�㿪һ��Ȩ���Ҳ��ܸɻ������");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS); 
        mContext.startActivity(intent);
    }
    public void lockscreem(){
    	if (dpm.isAdminActive(mDeviceAdminSample)) {
    		dpm.lockNow();
        	// ������������
        	dpm.resetPassword("", 0);
        	// ���sdcard�ϵ�����
//        	dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
        	// �ָ�����������
//        	dpm.wipeData(0);
        	
		}else {
			Toast.makeText(mContext, "��û�п�������ԱȨ�ް�", Toast.LENGTH_SHORT).show();
		}
    	
    }

}
