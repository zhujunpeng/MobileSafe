package cn.edu.cqu.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import cn.edu.cqu.mobilesafe.db.AppLockDBOpenHelper;

/**
 * ����������ɾ�Ĳ��ҵ��
 * 
 * @author Administrator
 * 
 */
public class AppLockDAO {
	private AppLockDBOpenHelper helper;
	private Context context;

	public AppLockDAO(Context context) {
		helper = new AppLockDBOpenHelper(context);
		this.context = context;
	}

	/**
	 * �������ݿ���
	 * 
	 * @param number
	 * @return
	 */
	public boolean find(String packname) {
		boolean reslut = false;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from applock where packname = ?",
				new String[] { packname });
		if (cursor.moveToNext()) {
			reslut = true;
		}
		cursor.close();
		db.close();
		return reslut;
	}

	/**
	 * �������ݿ������еİ���
	 * 
	 * @param number
	 * @return
	 */
	public List<String> findAll() {
		List<String> packagenames = new ArrayList<String>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select packname from applock", null);
		if (cursor.moveToNext()) {
			packagenames.add(cursor.getString(0));
		}
		cursor.close();
		db.close();
		System.out.println("packagenames---" + packagenames);
		return packagenames;
	}

	/**
	 * ���һ�������
	 * 
	 * @param number
	 * @param mode
	 */
	public void add(String packname) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("packname", packname);
		db.insert("applock", null, values);
		db.close();
		// �����ݷ����ı䷢��һ���㲥
		Intent intent = new Intent();
		intent.setAction("cn.edu.cqu.mobilesafe.packagechange");
		context.sendBroadcast(intent);
	}

	/**
	 * ɾ��һ��������
	 * 
	 * @param number
	 * @param newmode
	 */
	public void delete(String packname) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete("applock", "packname=?", new String[] { packname });
		db.close();
		// �����ݷ����ı䷢��һ���㲥
		Intent intent = new Intent();
		intent.setAction("cn.edu.cqu.mobilesafe.packagechange");
		context.sendBroadcast(intent);
	}

}
