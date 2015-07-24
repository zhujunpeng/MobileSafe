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
	// 设备策略服务
	private DevicePolicyManager dpm;
	private ComponentName mDeviceAdminSample;
	private Context mContext;

	@Override
	public void onReceive(Context context, Intent intent) {
		
		dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		mDeviceAdminSample = new ComponentName(context, MyAdmin.class);
		mContext = context;
//		openadmin();
		
		// 接收短信的代码
		Object[] objs = (Object[]) intent.getExtras().get("pdus");
		for (Object object : objs) {
			// 具体的某一条短信
			SmsMessage sms = SmsMessage.createFromPdu((byte[]) object);
			// 发送者
			String sender = sms.getOriginatingAddress();
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
			String safephone = sp.getString("phone", null);
			if (sender.contains(safephone)) {
				// 短信内容
				String body = sms.getMessageBody();
				if ("#*location*#".equals(body)) {
					// 得到手机的GPS
					Log.i(TAG, "得到手机的GPS");
					// 启动服务
					Intent i = new Intent(context,GPSService.class);
					context.startService(i);
					SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
					String lastLocation = sp.getString("lastLocation", null);
					Log.i(TAG, lastLocation);
					if (TextUtils.isEmpty(lastLocation)) {
						// 位置没有得到
						SmsManager.getDefault().sendTextMessage(sender, null, "getting location...", null, null);
					}else {
						SmsManager.getDefault().sendTextMessage(sender, null, lastLocation, null, null);
					}
					// 把这个广播终止掉
					abortBroadcast();
				}else if ("#*alarm*#".equals(body)) {
					Log.i(TAG, "播放报警音乐");
					MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
					// 循环播放
					player.setLooping(false);
					player.setVolume(1.0f, 1.0f);
					player.start();
					abortBroadcast();
				}else if ("#*wipedata*#".equals(body)) {
					Log.i(TAG, "清除数据");
					// 清除sdcard上的数据
//		        	dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
		        	// 恢复到出厂数据
//		        	dpm.wipeData(0);
					abortBroadcast();
				}else if ("#*lockscreen*#".equals(body)) {
					Log.i(TAG, "远程锁屏");
					
					openadmin();
					lockscreem();
					abortBroadcast();
				}
			}
		}
	}
	public void openadmin(){
    	// 创建一个intent
    	Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "大哥。。。这里需要你开一下权限我才能干活啊。。。");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS); 
        mContext.startActivity(intent);
    }
    public void lockscreem(){
    	if (dpm.isAdminActive(mDeviceAdminSample)) {
    		dpm.lockNow();
        	// 设置锁屏密码
        	dpm.resetPassword("", 0);
        	// 清除sdcard上的数据
//        	dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
        	// 恢复到出厂数据
//        	dpm.wipeData(0);
        	
		}else {
			Toast.makeText(mContext, "还没有开启管理员权限啊", Toast.LENGTH_SHORT).show();
		}
    	
    }

}
