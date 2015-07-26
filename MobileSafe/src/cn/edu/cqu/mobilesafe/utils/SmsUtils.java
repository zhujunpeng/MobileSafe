package cn.edu.cqu.mobilesafe.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import android.Manifest.permission;
import android.R.integer;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;

/**
 * ���ŵĹ�����
 * @author Administrator
 *
 */
public class SmsUtils {
	private static final String TAG = "SmsUtils";
	
	public interface BackUpCallBack{
		/**
		 * ���ø��µĽ���
		 * @param progress
		 */
		public void onSmsBackup(int progress);
		
		/**
		 * ���ø��µ��ܽ���
		 * @param max
		 */
		public void SmsMaxBackup(int max);
	}

	/**
	 * �����û��Ķ���
	 * @param context
	 * @param progressDialog ������
	 * @throws FileNotFoundException 
	 */
	public static void backuoSms(Context context,BackUpCallBack callBack) throws Exception{
		ContentResolver resolver = context.getContentResolver();
		File file = new File(Environment.getExternalStorageDirectory(),"backup.xml");
		FileOutputStream fos = new FileOutputStream(file);
		// ���û��Ķ���һ��һ���Ĵ���������������XML�ļ���
		XmlSerializer serializer = Xml.newSerializer();//��ȡ��Ŀ���ļ���������
		// c��ʼ��������
		serializer.setOutput(fos,"utf-8");
		// ͷ
		serializer.startDocument("utf-8", true);
		serializer.startTag(null, "smss");
		// ��ȡ�û��Ķ���
		Uri uri = Uri.parse("content://sms/");
		Cursor cursor = resolver.query(uri, new String[]{"body","address","type","date"}, null, null, null);
		// ��ȡ����������
		int max = cursor.getCount();
//		pd.setMax(max);
		callBack.SmsMaxBackup(max);
		serializer.attribute(null,"max", max + "");
		int progress = 0;
		while (cursor.moveToNext()) {
			String body = cursor.getString(0);
			String address = cursor.getString(1);
			String type = cursor.getString(2);
			String date = cursor.getString(3);
			serializer.startTag(null, "sms");
			serializer.startTag(null, "body");
			serializer.text(body);
			serializer.endTag(null, "body");
			
			serializer.startTag(null, "address");
			serializer.text(address);
			serializer.endTag(null, "address");
			
			serializer.startTag(null, "type");
			serializer.text(type);
			serializer.endTag(null, "type");
			
			serializer.startTag(null, "date");
			serializer.text(date);
			serializer.endTag(null, "date");
			
			serializer.endTag(null, "sms");
			// ���ñ��ݵĽ���
			progress ++;
//			pd.setProgress(progress);
			callBack.onSmsBackup(progress);
		}
		cursor.close();
		serializer.endTag(null, "smss");
		serializer.endDocument();
		fos.close();
	}
	/**
	 * ��ԭ����
	 * @param context
	 * @param flag �Ƿ�ɾ����ǰ�Ķ���
	 * @throws Exception 
	 */
	public static void restoreSms(Context context,boolean flag,BackUpCallBack callBack) throws Exception{
		ContentResolver resolver = context.getContentResolver();
		Uri uri = Uri.parse("content://sms/");
		if (flag) {
			// ȫ��ɾ������
			resolver.delete(uri, null, null);
		}
		// ��ȡSD���ϵ�XML�ļ�
		File file = new File(Environment.getExternalStorageDirectory(),"backup.xml");
		FileInputStream fis = new FileInputStream(file);
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(fis, "utf-8");
		int enventType = parser.getEventType();
		String body = null;
		String address = null;
		String type = null;
		String date = null;
		int progress = 0;
		while(enventType != XmlPullParser.END_DOCUMENT){
			// ��õ�ǰ�ڵ�
			String nodeName = parser.getName();
			switch (enventType) {
			case XmlPullParser.START_TAG:
				if ("smss".equals(nodeName)) {
					String max = parser.getAttributeValue(null, "max");
					callBack.SmsMaxBackup(Integer.parseInt(max));
					Log.i(TAG, "��������---" + max);
				}else if("body".equals(nodeName)){
					body = parser.nextText();
				}else if("address".equals(nodeName)){
					address = parser.nextText();
				}else if("type".equals(nodeName)){
					type = parser.nextText();
				}else if("date".equals(nodeName)){
					date = parser.nextText();
				}
				break;
			case XmlPullParser.END_TAG:
				if ("sms".equals(nodeName)) {
					ContentValues values = new ContentValues();
					values.put("body", body);
					values.put("address", address);
					values.put("type", type);
					values.put("date", date);
					resolver.insert(uri, values);
					progress ++;
					callBack.onSmsBackup(progress);
				}
			default:
				break;
			}
			enventType = parser.next();
			fis.close();
		}
	}
}
