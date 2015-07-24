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
				// 设置一个标示，当孩子生出来的时候找到他们的引用，存放在记事本里面，放在父亲的口袋
				view.setTag(holder);
			}else {
				view = convertView;
				holder = (ViewHolder) view.getTag();// 5%的效率提升
			}
			holder = new ViewHolder();
			holder.tv_item_balck_number = (TextView) view.findViewById(R.id.tv_item_balck_number);
			holder.tv_item_balck_mode = (TextView) view.findViewById(R.id.tv_item_balck_mode);
			holder.tv_item_balck_number.setText(infos.get(position).getNumber());
			if ("1".equals(infos.get(position).getMode())) {
				holder.tv_item_balck_mode.setText("拦截电话");
			}else if("2".equals(infos.get(position).getMode())) {
				holder.tv_item_balck_mode.setText("拦截短信");
			}else{
				holder.tv_item_balck_mode.setText("全部拦截");
			}
			return view;
		}
		
	}
	/**
	 * view对象的容器，相当于一个记事本
	 * @author Administrator
	 * 静态的字节码只加载了一次
	 */
	static class ViewHolder{
		TextView tv_item_balck_number;
		TextView tv_item_balck_mode;
	}
}
