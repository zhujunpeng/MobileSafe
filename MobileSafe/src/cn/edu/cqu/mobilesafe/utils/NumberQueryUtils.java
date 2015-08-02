package cn.edu.cqu.mobilesafe.utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class NumberQueryUtils {
	
	private static final String TAG = "NumberQueryUtils";
	private static String path = "/data/data/cn.edu.cqu.mobilesafe/files/address.db";
	/**
	 * ��һ�������������һ��������
	 * @param number
	 * @return
	 */
	public static String queryNumber(String number){
		
		String address = number;
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		// �ֻ������������ʽ
		if (number.matches("^1[34568]\\d{9}$")) {
			// �ֻ�����
			Cursor cursor = db.rawQuery("select location from data2 where id=(select outkey from data1 where id=?)", 
					new String[]{number.substring(0, 7)});
			if (cursor.moveToNext()) {
				address = cursor.getString(0);
			}
			cursor.close();
		}else {
			// �����ĺ���
			switch (number.length()) {
			case 3:
				// 110,120,119
				address = "�����绰";
				break;
			case 4:
				// 110,120,119
				address = "ģ����";
				break;
			case 5:
				// 110,120,119
				address = "�ͷ��绰";
				break;
			case 7:
				// 110,120,119
				address = "���ص绰";
				break;
			case 8:
				// 110,120,119
				address = "���ص绰";
				break;

			default:
				// ����;�绰
				if (number.length() > 10 && number.startsWith("0")) {
					Log.i(TAG, "��;�绰");
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
