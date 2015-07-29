package cn.edu.cqu.mobilesafe;

import cn.edu.cqu.mobilesafe.utils.MD5Utils;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends Activity {
	
	private GridView gv_list_home;
	private MyAdapter adapter;
	private SharedPreferences sp;
	private AlertDialog dialog;
	private static String [] names = {
		"�ֻ�����","ͨѶ��ʿ","�������",
		"���̹���","����ͳ��","�ֻ�ɱ��",
		"��������","�߼�����","��������",
	};
	
	private static int [] images = {
		R.drawable.safe,R.drawable.callmsgsafe,R.drawable.app,
		R.drawable.taskmanager,R.drawable.netmanager,R.drawable.trojan,
		R.drawable.sysoptimize,R.drawable.atools,R.drawable.settings
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		gv_list_home = (GridView) findViewById(R.id.gv_list_home);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		adapter = new MyAdapter();
		gv_list_home.setAdapter(adapter);
		gv_list_home.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				switch (position) {
				case 0:// �����ֻ�����
					showLostFindDialog();
					break;
				case 2:// ����Ӧ���������
					startActivity(new Intent(HomeActivity.this, AppManagerActivity.class));
					break;
				case 1:// ����ͨѶ��ʿ
					startActivity(new Intent(HomeActivity.this, CallSmsActivity.class));
					break;
				case 3:// ������̹���
					startActivity(new Intent(HomeActivity.this, TaskManagerActivity.class));
					break;
				case 7:// �߼�����
					startActivity(new Intent(HomeActivity.this, AToolsActivity.class));
					break;
				case 8://��ת������ҳ��
					Intent intent = new Intent(HomeActivity.this, SettingActivity.class);
					startActivity(intent);
					break;

				default:
					break;
				}
			}
		});
	}
	
	private void showLostFindDialog(){
		// �ж��Ƿ񱣴�������
		System.out.println("����---" + isSetPwd());
		if (isSetPwd()) {
			// �Ѿ����ù������ˣ���������Ի���
			showEnterDialog();
		}else {
			// û���������룬������������Ի���
			showSetPwdDialog();
		}
	}
	
	/*
	 * ��������Ի���
	 * */
	private void showSetPwdDialog() {
		AlertDialog.Builder builder = new Builder(HomeActivity.this);
		// �Զ�һ�Ĳ����ļ�
		View view = View.inflate(HomeActivity.this, R.layout.dialog_setup_password, null);
		final EditText ev_setup_pwd = (EditText) view.findViewById(R.id.ev_setup_pwd);
		final EditText ev_setup_confirm = (EditText) view.findViewById(R.id.ev_comfirm_pwd);
		Button btn_ok = (Button) view.findViewById(R.id.btn_ok);
		Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
		btn_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// �ѶԻ���ȡ��
				dialog.dismiss();
			}
		});
		btn_ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// ȡ������
				String password = ev_setup_pwd.getText().toString().trim();
				String password_confirm = ev_setup_confirm.getText().toString().trim();
				if (TextUtils.isEmpty(password) || TextUtils.isEmpty(password_confirm)) {
					Toast.makeText(HomeActivity.this, "����Ϊ��", Toast.LENGTH_SHORT).show();
					return;
				}
				// �ж��Ƿ�һ��
				if (password.equals(password_confirm)) {
					// һֱ�Ļ����ͱ������룬�ѶԻ����������������ҳ��
					Editor editor = sp.edit();
					editor.putString("password", MD5Utils.md5Password(password));
					editor.commit();
					dialog.dismiss();
					startActivity(new Intent(HomeActivity.this, LostFindActivity.class));
				}else {
					Toast.makeText(HomeActivity.this, "���벻һ��", Toast.LENGTH_SHORT).show();
					return;
				}
			}
		});
		builder.setView(view);
		dialog = builder.show();
	}

	/*
	 * ��������Ի���
	 * */
	private void showEnterDialog() {
		AlertDialog.Builder builder = new Builder(HomeActivity.this);
		// �Զ�һ�Ĳ����ļ�
		View view = View.inflate(HomeActivity.this, R.layout.dialog_enter_password, null);
		final EditText ev_setup_pwd = (EditText) view.findViewById(R.id.ev_setup_pwd);
		Button btn_ok = (Button) view.findViewById(R.id.btn_ok);
		Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
		btn_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// �ѶԻ���ȡ��
				dialog.dismiss();
			}
		});
		btn_ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String savePwd = sp.getString("password", null);
				// ȡ������
				String password = ev_setup_pwd.getText().toString().trim();
				if (TextUtils.isEmpty(password)) {
					Toast.makeText(HomeActivity.this, "����Ϊ��", Toast.LENGTH_SHORT).show();
					return;
				}
				// �ж��Ƿ�һ��
				if (MD5Utils.md5Password(password).equals(savePwd)) {
					dialog.dismiss();
					startActivity(new Intent(HomeActivity.this, LostFindActivity.class));
				}else {
					Toast.makeText(HomeActivity.this, "�������", Toast.LENGTH_SHORT).show();
					return;
				}
			}
		});
		builder.setView(view);
		dialog = builder.show();
	
	}

	private boolean isSetPwd(){
		String password = sp.getString("password", null);
//		if (TextUtils.isEmpty(password)) {
//			return false;
//		}else {
//			return true;
//		}
		System.out.println("���룺" + password);
		return !TextUtils.isEmpty(password);
	}
	private class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return names.length;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			ViewHolder viewHolder;
			if (view == null) {
				view = View.inflate(HomeActivity.this, R.layout.list_item_home, null);
				viewHolder = new ViewHolder();
				viewHolder.iv_item = (ImageView) view.findViewById(R.id.iv_item);
				viewHolder.tv_item = (TextView) view.findViewById(R.id.tv_item);
				view.setTag(viewHolder);
			}else {
				viewHolder = (ViewHolder) view.getTag();
			}
			viewHolder.tv_item.setText(names[position]);
			viewHolder.iv_item.setImageResource(images[position]);
			return view;
		}
		private class ViewHolder{
			TextView tv_item;
			ImageView iv_item;
		}
		
	}
}
