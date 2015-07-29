package cn.edu.cqu.mobilesafe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;
import cn.edu.cqu.mobilesafe.utils.StreamTools;

public class MainActivity extends Activity {

	protected static final String TAG = "MainActivity";
	protected static final int ENTER_HOME = 0;
	protected static final int SHOW_UODATE_DIALOG = 1;
	protected static final int URL_ERROR = 3;
	protected static final int NETWORK_ERROR = 2;
	protected static final int JSON_ERROR = 4;
	private TextView tv_main_version;
	private TextView tv_main_updateInfo;
	private String version;
	private String description;
	private String apkurl;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		tv_main_version = (TextView) findViewById(R.id.tv_main_version);
		tv_main_version.setText("�汾�ţ�" + getVersionName());
		tv_main_updateInfo = (TextView) findViewById(R.id.tv_main_updateInfo);
		// �������
		// checkUpdate();
		// ���Ƶ绰������������ݿ�
		copyDB();

		// �����Ч��
		AlphaAnimation aa = new AlphaAnimation(0.2f, 1.0f);
		aa.setDuration(500);
		findViewById(R.id.rl_root_main).startAnimation(aa);

		sp = getSharedPreferences("config", MODE_PRIVATE);
		boolean update = sp.getBoolean("update", false);
		if (update) {
			// �Զ������ѹر�
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					// ������ҳ��
					enterHome();
				}
			}, 2000);
		} else {
			// �������
			checkUpdate();
		}
		// ������ݷ�ʽ
		installShortCut();
	}

	/**
	 * ������ݷ�ʽ
	 */
	private void installShortCut() {
		boolean shortcut = sp.getBoolean("shortcut", false);
		if (shortcut) {
			return;
		}
		Editor editor = sp.edit();
		// ����������Ҫ��Ϣ��1��ͼ�꣬2�����ƣ�3.��ʲô����
		Intent intent = new Intent();
		intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		// ������ʾ�����ƺ�ͼ��
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "�ֻ���ʿ");
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON,
				BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
		// ����������ͼ
		Intent shortCutIntent = new Intent();
		shortCutIntent.setAction("android.intent.action.MAIN");
		shortCutIntent.addCategory("android.intent.category.LAUNCHER");
		shortCutIntent.setClassName(getPackageName(),
				"cn.edu.cqu.mobilesafe.MainActivity");
		// �������ͼ���ݳ�ȥ
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortCutIntent);
		sendBroadcast(intent);
		editor.putBoolean("shortcut", true);
		editor.commit();
	}

	/**
	 * �������ݿ⵽/data/data/cn.edu.cqu.mobilesafe/files/address.db
	 */
	private void copyDB() {
		try {
			InputStream is = getAssets().open("address.db");
			// ��/data/data/cn.edu.cqu.mobilesafe/files/Ŀ¼�´���һ���ļ�address.db
			File file = new File(getFilesDir(), "address.db");
			if (file.exists() && file.length() > 0) {
				// �ļ����ƹ���
			} else {
				FileOutputStream fos = new FileOutputStream(file);
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = is.read(buffer)) != -1) {
					fos.write(buffer, 0, len);
				}
				is.close();
				fos.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case ENTER_HOME: // ����������
				Log.i(TAG, "û���°汾����������������");
				enterHome();
				break;
			case SHOW_UODATE_DIALOG: // ��ʾ�����Ի���
				Log.i(TAG, "���°汾�������Ի���");
				showUpdataDialog();
				break;
			case URL_ERROR: // URL����
				Log.i(TAG, "URL����");
				enterHome();
				break;
			case NETWORK_ERROR:// �������
				Log.i(TAG, "ò��û������");
				enterHome();
				break;
			case JSON_ERROR:// JSON��������
				Log.i(TAG, "����JSON����");
				enterHome();
				break;
			default:
				break;
			}
		}

		/*
		 * ���������Ի���
		 */
		private void showUpdataDialog() {
			AlertDialog.Builder builder = new Builder(MainActivity.this);
			builder.setTitle("��ʾ����");
			// ���ò���ȡ����ǿ������
			// builder.setCancelable(false);
			builder.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					// ����������
					enterHome();
					dialog.dismiss();
				}
			});
			builder.setMessage(description);
			builder.setPositiveButton("��������", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// ����apk�������滻
					if (Environment.getExternalStorageState().equals(
							Environment.MEDIA_MOUNTED)) {
						// sdcard����
						FinalHttp http = new FinalHttp();
						http.download(apkurl, Environment
								.getExternalStorageDirectory()
								.getAbsolutePath()
								+ "/mobilesafe" + version,
								new AjaxCallBack<File>() {

									// ����ʧ��
									@Override
									public void onFailure(Throwable t,
											int errorNo, String strMsg) {
										t.printStackTrace();
										Toast.makeText(getApplicationContext(),
												"����ʧ��", Toast.LENGTH_SHORT)
												.show();
										super.onFailure(t, errorNo, strMsg);
									}

									@Override
									public void onLoading(long count,
											long current) {
										super.onLoading(count, current);
										tv_main_updateInfo
												.setVisibility(View.VISIBLE);
										int progress = (int) (current * 100 / count);
										tv_main_updateInfo.setText("���ؽ��ȣ�"
												+ progress + "%");
									}

									@Override
									public void onSuccess(File t) {
										super.onSuccess(t);
										instalAPK(t);
									}

									// ��װapk
									private void instalAPK(File t) {
										Intent intent = new Intent();
										intent.setAction("android.intent.action.VIEW");
										intent.addCategory("android.intent.category.DEFAULT");
										intent.setDataAndType(Uri.fromFile(t),
												"application/vnd.android.package-archive");
										startActivity(intent);
									}

								});
					} else {
						Toast.makeText(getApplicationContext(), "û��SD�����밲װ������",
								Toast.LENGTH_SHORT).show();
						enterHome();
						return;
					}

				}
			});
			builder.setNegativeButton("�´���˵", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					enterHome();
				}
			});
			builder.show();
		}

	};

	/*
	 * ����Ƿ����°汾
	 */
	private void checkUpdate() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Message msg = handler.obtainMessage();
				long startTime = System.currentTimeMillis();
				// url http://192.168.1.3:8080/updateInfo.html
				try {
					URL url = new URL(getString(R.string.serviceurl));
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.setConnectTimeout(4000);
					conn.setRequestMethod("GET");
					int code = conn.getResponseCode();
					if (code == 200) {
						InputStream is = conn.getInputStream();
						String reslut = StreamTools.readFromStream(is);
						Log.i(TAG, "�����ɹ� ��" + reslut);
						// ����JSON����
						JSONObject obj = new JSONObject(reslut);
						version = (String) obj.get("version");
						description = (String) obj.get("description");
						apkurl = (String) obj.get("apkurl");
						Log.i(TAG, version + description + apkurl);

						// У��ʱ�����°汾
						if (getVersionName().equals(version)) {
							// �汾һֱ��û���°汾��������ҳ��
							msg.what = ENTER_HOME;
						} else {
							// ���°汾������һ�������Ի���
							msg.what = SHOW_UODATE_DIALOG;
						}
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
					msg.what = URL_ERROR;
					Toast.makeText(MainActivity.this, "URL�쳣",
							Toast.LENGTH_SHORT).show();
				} catch (ProtocolException e) {
					e.printStackTrace();
					msg.what = NETWORK_ERROR;
					Toast.makeText(MainActivity.this, "�����쳣",
							Toast.LENGTH_SHORT).show();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
					msg.what = JSON_ERROR;
					Toast.makeText(MainActivity.this, "json�����쳣",
							Toast.LENGTH_SHORT).show();
				} finally {
					long endTime = System.currentTimeMillis();
					long dtime = endTime - startTime;
					if (dtime < 2000) {
						try {
							Thread.sleep(2000 - dtime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					handler.sendMessage(msg);
				}
			}
		}).start();
	}

	// ��ȡ����İ汾��
	private String getVersionName() {
		PackageManager manager = getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
			return info.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return null;
		}

	}

	private void enterHome() {
		Intent intent = new Intent(MainActivity.this, HomeActivity.class);
		startActivity(intent);
		finish();
	}
}
