package cn.edu.cqu.mobilesafe;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.widget.Toast;

public abstract class BaseSetupActivity extends Activity {

	protected static final String TAG = "BaseSetupActivity";
	// 1 ����һ������ʶ����
	private GestureDetector detector;
	protected SharedPreferences sp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		sp = getSharedPreferences("config", MODE_PRIVATE);
		// 2 ʵ�����������ʶ����
				detector = new GestureDetector(this, new OnGestureListener() {
					
					@Override
					public boolean onSingleTapUp(MotionEvent e) {
						return false;
					}
					
					@Override
					public void onShowPress(MotionEvent e) {
						
					}
					
					@Override
					public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
							float distanceY) {
						return false;
					}
					
					@Override
					public void onLongPress(MotionEvent e) {
						
					}
					/*
					 * ����ָ�����滬����ʱ��ص�
					 * */
					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
							float velocityY) {
						
						// ���λ������������
						if (Math.abs(velocityX) < 200) {
							Toast.makeText(BaseSetupActivity.this, "������̫����", Toast.LENGTH_SHORT).show();
							return true;
						}
						
						// ����б�������
						if (Math.abs(e2.getRawY() - e1.getRawY()) > 100){
							Toast.makeText(BaseSetupActivity.this, "�����⻬��", Toast.LENGTH_SHORT).show();
							return true;
						}
						
						if (e2.getRawX() - e1.getRawX() > 200) {
							// ��ʾ��һ��ҳ��
							Log.i(TAG, "��ʾ��һ��ҳ��");
							ShowPreActivity();
						}
						if (e1.getRawX() - e2.getRawX() > 200) {
							// ��ʾ��һ��ҳ��
							Log.i(TAG, "��ʾxiayi��ҳ��");
							ShowNextActivity();
							return true;
						}
						return false;
					}
					
					@Override
					public boolean onDown(MotionEvent e) {
						return false;
					}
				});
	}	
	// ����ĳ��󷽷��������ʵ��
	public abstract void ShowNextActivity();
	public abstract void ShowPreActivity();
	/*
	 * ��һ���ĵ���¼�
	 * */
	public void next(View v){
		ShowNextActivity();
	}
	/*
	 * ��һ���ĵ���¼�
	 * */
	public void pre(View v){
		ShowPreActivity();
	}
	/*
	 * 3 ʹ������ʶ����
	 * */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		detector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}
}
