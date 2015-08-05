package cn.edu.cqu.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


/**
 * 病毒数据库业务
 * 
 * @author Administrator
 * 
 */
public class AntivirusDAO {
	
	private static String path = "/data/data/cn.edu.cqu.mobilesafe/files/antivirus.db";
	public static boolean isVirus(String md5){
		boolean reslut = false;
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select * from datable where md5 = ?", new String[]{md5});
		if (cursor.moveToNext()) {
			reslut = true;
		}
		cursor.close();
		db.close();
		return reslut;
	}
}
