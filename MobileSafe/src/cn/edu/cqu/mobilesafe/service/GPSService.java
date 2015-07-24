package cn.edu.cqu.mobilesafe.service;

import java.io.IOException;
import java.io.InputStream;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class GPSService extends Service {
	
	// 位置服务
	private LocationManager lm;
	private MyLocationListener listener;
	private SharedPreferences sp;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		sp = getSharedPreferences("config", MODE_PRIVATE);
        
//      List<String> providers = lm.getAllProviders();
//      for (String string : providers) {
//			Log.i(TAG, "----" + string);
//		}
      listener = new MyLocationListener();
      // 给位置提供者设置条件,获得一个最好的定位方式
      Criteria criteria = new Criteria();
      criteria.setAccuracy(Criteria.ACCURACY_FINE);
      String bestProvider = lm.getBestProvider(criteria, true);
      // 监听位置服务
      lm.requestLocationUpdates(bestProvider, 0, 0, listener);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		// 取消位置监听服务
    	lm.removeUpdates(listener);
    	listener = null;
	}

	 public class MyLocationListener  implements LocationListener{

	    	private static final String TAG = "MyLocationListener";

			/*
	    	 * 位置发生变化回调
	    	 * */
			@Override
			public void onLocationChanged(Location location) {
				// 经度
				String longitude = "j:" + location.getLongitude() + "\n";
				String latitude = "w:" + location.getLatitude() + "\n";
				String accuracy = "a:" + location.getAccuracy() + "\n";
				// 发短信给安全号码
				
				// 把标准的GPS转换成火星坐标
//				try {
//					InputStream is = getAssets().open("axisoffset.dat");
//					ModifyOffset offset = ModifyOffset.getInstance(is);
//					offset.s2c(new PointDouble(location.getAltitude(), location.getLatitude()));
//					longitude = "j:" + offset.X + "\n";
//					latitude = "w:" + offset.Y + "\n";
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				
				Editor editor = sp.edit();
				editor.putString("lastLocation", longitude + latitude + accuracy);
				Log.i(TAG, "保存最后一次的位置信息");
				editor.commit();
			}

			/*
	    	 * 某一位置服务不可用了
	    	 * */
			@Override
			public void onProviderDisabled(String provider) {
				
			}

			/*
	    	 * 某一位置服务可用了
	    	 * */
			@Override
			public void onProviderEnabled(String provider) {
				
			}

			/*
	    	 * 位置状态发生变化回调
	    	 * */
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				
			}
	    	
	    }
}
