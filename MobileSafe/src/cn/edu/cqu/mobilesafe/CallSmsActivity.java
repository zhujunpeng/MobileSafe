package cn.edu.cqu.mobilesafe;

import java.util.List;

import cn.edu.cqu.mobilesafe.db.BlackNumberDBOpenHelper;
import cn.edu.cqu.mobilesafe.db.dao.BlackNumberDAO;
import cn.edu.cqu.mobilesafedomain.BlackNumberInfo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class CallSmsActivity extends Activity {
	
	private ListView lv_callsms_safe;
	private List<BlackNumberInfo> infos;
	private BlackNumberDAO dao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_callsms);
		
		lv_callsms_safe = (ListView) findViewById(R.id.lv_callsms_safe);
		dao = new BlackNumberDAO(this);
		infos = dao.findAll();
		lv_callsms_safe.setAdapter(new CallSmsSafeAdapter());
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
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder holder;
			if (convertView == null) {
				view = View.inflate(getApplicationContext(), R.layout.list_item_balcknumber, null);
				holder = new ViewHolder();
				holder.tv_item_balck_number = (TextView) view.findViewById(R.id.tv_item_balck_number);
				holder.tv_item_balck_mode = (TextView) view.findViewById(R.id.tv_item_balck_mode);
				// ����һ����ʾ����������������ʱ���ҵ����ǵ����ã�����ڼ��±����棬���ڸ��׵Ŀڴ�
				view.setTag(holder);
			}else {
				view = convertView;
				holder = (ViewHolder) view.getTag();// 5%��Ч������
			}
			holder = new ViewHolder();
			holder.tv_item_balck_number = (TextView) view.findViewById(R.id.tv_item_balck_number);
			holder.tv_item_balck_mode = (TextView) view.findViewById(R.id.tv_item_balck_mode);
			holder.tv_item_balck_number.setText(infos.get(position).getNumber());
			if ("1".equals(infos.get(position).getMode())) {
				holder.tv_item_balck_mode.setText("���ص绰");
			}else if("2".equals(infos.get(position).getMode())) {
				holder.tv_item_balck_mode.setText("���ض���");
			}else{
				holder.tv_item_balck_mode.setText("ȫ������");
			}
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
	}
}
