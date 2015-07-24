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
	// 1 定义一个手势识别器
	private GestureDetector detector;
	protected SharedPreferences sp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		sp = getSharedPreferences("config", MODE_PRIVATE);
		// 2 实例化这个手势识别器
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
					 * 当手指在上面滑动的时候回调
					 * */
					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
							float velocityY) {
						
						// 屏蔽滑动很慢的情况
						if (Math.abs(velocityX) < 200) {
							Toast.makeText(BaseSetupActivity.this, "滑动的太慢了", Toast.LENGTH_SHORT).show();
							return true;
						}
						
						// 屏蔽斜滑的情况
						if (Math.abs(e2.getRawY() - e1.getRawY()) > 100){
							Toast.makeText(BaseSetupActivity.this, "不能这滑动", Toast.LENGTH_SHORT).show();
							return true;
						}
						
						if (e2.getRawX() - e1.getRawX() > 200) {
							// 显示上一个页面
							Log.i(TAG, "显示上一个页面");
							ShowPreActivity();
						}
						if (e1.getRawX() - e2.getRawX() > 200) {
							// 显示下一个页面
							Log.i(TAG, "显示xiayi个页面");
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
	// 基类的抽象方法子类必须实现
	public abstract void ShowNextActivity();
	public abstract void ShowPreActivity();
	/*
	 * 下一步的点击事件
	 * */
	public void next(View v){
		ShowNextActivity();
	}
	/*
	 * 上一步的点击事件
	 * */
	public void pre(View v){
		ShowPreActivity();
	}
	/*
	 * 3 使用手势识别器
	 * */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		detector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}
}
