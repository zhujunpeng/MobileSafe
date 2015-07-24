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
	
	// λ�÷���
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
      // ��λ���ṩ����������,���һ����õĶ�λ��ʽ
      Criteria criteria = new Criteria();
      criteria.setAccuracy(Criteria.ACCURACY_FINE);
      String bestProvider = lm.getBestProvider(criteria, true);
      // ����λ�÷���
      lm.requestLocationUpdates(bestProvider, 0, 0, listener);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		// ȡ��λ�ü�������
    	lm.removeUpdates(listener);
    	listener = null;
	}

	 public class MyLocationListener  implements LocationListener{

	    	private static final String TAG = "MyLocationListener";

			/*
	    	 * λ�÷����仯�ص�
	    	 * */
			@Override
			public void onLocationChanged(Location location) {
				// ����
				String longitude = "j:" + location.getLongitude() + "\n";
				String latitude = "w:" + location.getLatitude() + "\n";
				String accuracy = "a:" + location.getAccuracy() + "\n";
				// �����Ÿ���ȫ����
				
				// �ѱ�׼��GPSת���ɻ�������
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
				Log.i(TAG, "�������һ�ε�λ����Ϣ");
				editor.commit();
			}

			/*
	    	 * ĳһλ�÷��񲻿�����
	    	 * */
			@Override
			public void onProviderDisabled(String provider) {
				
			}

			/*
	    	 * ĳһλ�÷��������
	    	 * */
			@Override
			public void onProviderEnabled(String provider) {
				
			}

			/*
	    	 * λ��״̬�����仯�ص�
	    	 * */
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				
			}
	    	
	    }
}
