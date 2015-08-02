package cn.edu.cqu.mobilesafe.utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class NumberQueryUtils {
	
	private static final String TAG = "NumberQueryUtils";
	private static String path = "/data/data/cn.edu.cqu.mobilesafe/files/address.db";
	/**
	 * 传一个号码进来返回一个归属地
	 * @param number
	 * @return
	 */
	public static String queryNumber(String number){
		
		String address = number;
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		// 手机号码的正则表达式
		if (number.matches("^1[34568]\\d{9}$")) {
			// 手机号码
			Cursor cursor = db.rawQuery("select location from data2 where id=(select outkey from data1 where id=?)", 
					new String[]{number.substring(0, 7)});
			if (cursor.moveToNext()) {
				address = cursor.getString(0);
			}
			cursor.close();
		}else {
			// 其他的号码
			switch (number.length()) {
			case 3:
				// 110,120,119
				address = "公安电话";
				break;
			case 4:
				// 110,120,119
				address = "模拟器";
				break;
			case 5:
				// 110,120,119
				address = "客服电话";
				break;
			case 7:
				// 110,120,119
				address = "本地电话";
				break;
			case 8:
				// 110,120,119
				address = "本地电话";
				break;

			default:
				// 处理长途电话
				if (number.length() > 10 && number.startsWith("0")) {
					Log.i(TAG, "长途电话");
					// 010-1257765
					Cursor cursor = db.rawQuery("select location from data2 where area=?", 
							new String[]{number.substring(1, 3)});
					while (cursor.moveToNext()) {
						String str = cursor.getString(0);
						address = str.substring(0, str.length() - 2);
					}
					cursor.close();
					// 0794-4330269
					cursor = db.rawQuery("select location from data2 where area=?", 
							new String[]{number.substring(1, 4)});
					while (cursor.moveToNext()) {
						String str = cursor.getString(0);
						address = str.substring(0, str.length() - 2);
					}
					cursor.close();
				}
				break;
			}
		}
		
		
		return address;
	}

}
