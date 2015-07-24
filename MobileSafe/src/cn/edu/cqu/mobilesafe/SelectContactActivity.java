package cn.edu.cqu.mobilesafe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class SelectContactActivity extends Activity {
	
	private ListView select_contact_list;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_contact);
		
		select_contact_list = (ListView) findViewById(R.id.select_contact_list);
		final List<Map<String, String>> data = getSystemContact();
        SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.item_contact, 
        		new String[]{"name","phone"}, new int[]{R.id.name,R.id.phone});
        select_contact_list.setAdapter(adapter);
        select_contact_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				String phone = data.get(position).get("phone");
				Intent data = new Intent();
				data.putExtra("phone", phone);
				System.out.println("phone----" + phone);
				setResult(0,data);
				finish();
			}
		});
	}
	
	 // 查找手机联系人信息
		private List<Map<String, String>> getSystemContact() {
			
			List<Map<String, String>> lists = new ArrayList<Map<String,String>>();
			// 1 得到一个内容解析器
			ContentResolver resolver = getContentResolver();
			// 2 获得要查询的表的uri
			// 只需要这两张表
			Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
			Uri dataUri = Uri.parse("content://com.android.contacts/data");
			Cursor cursor = resolver.query(uri, new String[]{"contact_id"}, null, null, null);
			while (cursor.moveToNext()) {
				Map<String, String> map = new HashMap<String, String>();
				String contact_id = cursor.getString(0);
				if (contact_id != null) {
					Cursor dataCursor = resolver.query(dataUri, new String[]{"data1","mimetype"}, "contact_id=?", new String[]{contact_id}, null);
					while(dataCursor.moveToNext()){
						String data1 = dataCursor.getString(0);
						String mimetype = dataCursor.getString(1);
//						System.out.println("data1----" + data1 + "---mimetype---" + mimetype);
						if ("vnd.android.cursor.item/phone_v2".equals(mimetype)) {
							map.put("phone", data1);
						}else if ("vnd.android.cursor.item/name".equals(mimetype)) {
							map.put("name", data1);
						}
					}
					dataCursor.close();
				}
				lists.add(map);
//				System.out.println("lists----" + lists.toString());
			}
			cursor.close();
			return lists;
		}

}
