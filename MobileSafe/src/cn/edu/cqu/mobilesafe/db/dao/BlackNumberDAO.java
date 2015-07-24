package cn.edu.cqu.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import cn.edu.cqu.mobilesafe.db.BlackNumberDBOpenHelper;
import cn.edu.cqu.mobilesafedomain.BlackNumberInfo;

/**
 * ���������ݿ����ɾ�Ĳ��ҵ��
 * @author Administrator
 *
 */
public class BlackNumberDAO {
	private BlackNumberDBOpenHelper helper;

	public BlackNumberDAO(Context context) {
		helper = new BlackNumberDBOpenHelper(context); 
	}
	
	/**
	 * �������ݿ���
	 * @param number
	 * @return
	 */
	public boolean find(String number){
		boolean reslut = false;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from blacknumber where number = ?", new String[]{number});
		if (cursor.moveToNext()) {
			reslut = true;
		}
		cursor.close();
		db.close();
		return reslut; 
	}
	/**
	 * �������ݿ���ָ�����������ģʽ
	 * @param number
	 * @return Ĭ�Ϸ���null
	 */
	public String findMode(String number){
		String reslut = null;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select mode from blacknumber where number = ?", new String[]{number});
		if (cursor.moveToNext()) {
			reslut = cursor.getString(0);
		}
		cursor.close();
		db.close();
		return reslut; 
	}
	/**
	 * ��Ӻ�����
	 * @param number
	 * @param mode
	 */
	public void add(String number , String mode){
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("number", number);
		values.put("mode", mode);
		db.insert("blacknumber", null, values);
		db.close();
	}
	
	/**
	 * �޸ĺ�����������ģʽ
	 * @param number
	 * @param newmode
	 */
	public void update(String number , String newmode){
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("mode", newmode);
		db.update("blacknumber", values, "number=?", new String[]{number});
		db.close();
	}
	
	/**
	 * ɾ��������
	 * @param number
	 * @param newmode
	 */
	public void delete(String number){
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete("blacknumber", "number=?", new String []{number});
		db.close();
	}
	
	/**
	 * �������ݿ������еĺ�����
	 * @param number
	 * @return
	 */
	public List<BlackNumberInfo> findAll(){
		List<BlackNumberInfo> reslut = new ArrayList<BlackNumberInfo>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select number,mode from blacknumber order by _id desc", null);
		while (cursor.moveToNext()) {
			BlackNumberInfo info = new BlackNumberInfo();
			String number = cursor.getString(0);
			String mode = cursor.getString(1);
			info.setNumber(number);
			info.setMode(mode);
			reslut.add(info);
		}
		cursor.close();
		db.close();
		return reslut; 
	}
}
