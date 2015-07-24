package cn.edu.cqu.mobilesafe;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.edu.cqu.mobilesafe.db.dao.BlackNumberDAO;
import cn.edu.cqu.mobilesafedomain.BlackNumberInfo;

public class CallSmsActivity extends Activity {
	
	private ListView lv_callsms_safe;
	private List<BlackNumberInfo> infos;
	private BlackNumberDAO dao;
	private CallSmsSafeAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_callsms);
		
		lv_callsms_safe = (ListView) findViewById(R.id.lv_callsms_safe);
		dao = new BlackNumberDAO(this);
		infos = dao.findAll();
		adapter = new CallSmsSafeAdapter();
		lv_callsms_safe.setAdapter(adapter);
	}
	
	private class CallSmsSafeAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return infos.size();
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
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder holder;
			if (convertView == null) {
				view = View.inflate(getApplicationContext(), R.layout.list_item_balcknumber, null);
				holder = new ViewHolder();
				holder.tv_item_balck_number = (TextView) view.findViewById(R.id.tv_item_balck_number);
				holder.tv_item_balck_mode = (TextView) view.findViewById(R.id.tv_item_balck_mode);
				holder.iv_delete_number = (ImageView) view.findViewById(R.id.iv_delete_number);
				// ����һ����ʾ����������������ʱ���ҵ����ǵ����ã�����ڼ��±����棬���ڸ��׵Ŀڴ�
				view.setTag(holder);
			}else {
				view = convertView;
				holder = (ViewHolder) view.getTag();// 5%��Ч������
			}
			holder = new ViewHolder();
			holder.tv_item_balck_number = (TextView) view.findViewById(R.id.tv_item_balck_number);
			holder.tv_item_balck_mode = (TextView) view.findViewById(R.id.tv_item_balck_mode);
			holder.iv_delete_number = (ImageView) view.findViewById(R.id.iv_delete_number);
			holder.tv_item_balck_number.setText(infos.get(position).getNumber());
			if ("1".equals(infos.get(position).getMode())) {
				holder.tv_item_balck_mode.setText("���ص绰");
			}else if("2".equals(infos.get(position).getMode())) {
				holder.tv_item_balck_mode.setText("���ض���");
			}else{
				holder.tv_item_balck_mode.setText("ȫ������");
			}
			holder.iv_delete_number.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
//					dao.delete(infos.get(position).getNumber());
					AlertDialog.Builder builder = new Builder(CallSmsActivity.this);
					builder.setTitle("ɾ��������");
					builder.setMessage("ȷ��Ҫɾ���ú��룿");
					builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dao.delete(infos.get(position).getNumber());
							// ɾ��������
							infos.remove(position);
							// ֪ͨ������������������
							adapter.notifyDataSetChanged();
						}
					});
					builder.setNegativeButton("ȡ��", null);
					builder.show();
				}
			});
			return view;
		}
		
	}
	/**
	 * view������������൱��һ�����±�
	 * @author Administrator
	 * ��̬���ֽ���ֻ������һ��
	 */
	static class ViewHolder{
		TextView tv_item_balck_number;
		TextView tv_item_balck_mode;
		ImageView iv_delete_number;
	}
	/**
	 * ��Ӻ�����
	 * @param view
	 */
	
	private EditText et_blacknumber;
	private CheckBox cb_phone;
	private CheckBox cb_sms;
	private Button bt_ok;
	private Button bt_cancel;
	public void add_black_number(View view){
		AlertDialog.Builder builder = new Builder(this);
		final AlertDialog dialog = builder.create();
		View dialog_black_number = View.inflate(getApplicationContext(), R.layout.dialog_add_blacknumber, null);
		et_blacknumber = (EditText) dialog_black_number.findViewById(R.id.et_blacknumber);
		cb_phone = (CheckBox) dialog_black_number.findViewById(R.id.cb_phone);
		cb_sms = (CheckBox) dialog_black_number.findViewById(R.id.cb_sms);
		bt_ok = (Button) dialog_black_number.findViewById(R.id.btn_ok);
		bt_cancel = (Button) dialog_black_number.findViewById(R.id.btn_cancel);
		dialog.setView(dialog_black_number);
		dialog.show();
		bt_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		bt_ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String blacknumber = et_blacknumber.getText().toString().trim();
				if (TextUtils.isEmpty(blacknumber)) {
					Toast.makeText(getApplicationContext(), "���������벻��Ϊ��", 0).show();
					return;
				}
				String mode;
				if (cb_phone.isChecked() && cb_sms.isChecked()) {
					// ȫ������
					mode = "3";
				}else if (cb_phone.isChecked()) {
					// ���ص绰
					mode = "1";
				}else if(cb_sms.isChecked()){
					// ��������
					mode = "2";
				}else {
					Toast.makeText(getApplicationContext(), "��ѡ������ģʽ", 0).show();
					return;
				}
				dao.add(blacknumber, mode);
				// ����ListView�е�����
				BlackNumberInfo info = new BlackNumberInfo();
				info.setMode(mode);
				info.setNumber(blacknumber);
				// �ӵ���ĩβŶ
//				infos.add(info);
				infos.add(0, info);
				// ֪ͨ������������������
				adapter.notifyDataSetChanged();
				dialog.dismiss();
			}
		});
	}
}
