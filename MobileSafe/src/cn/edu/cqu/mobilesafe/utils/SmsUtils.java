package cn.edu.cqu.mobilesafe.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.xmlpull.v1.XmlSerializer;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;

/**
 * 短信的工具类
 * @author Administrator
 *
 */
public class SmsUtils {
	
	public interface BackUpCallBack{
		/**
		 * 设置更新的进度
		 * @param progress
		 */
		public void onSmsBackup(int progress);
		
		/**
		 * 设置更新的总进度
		 * @param max
		 */
		public void SmsMaxBackup(int max);
	}

	/**
	 * 备份用户的短信
	 * @param context
	 * @param progressDialog 进度条
	 * @throws FileNotFoundException 
	 */
	public static void backuoSms(Context context,BackUpCallBack callBack) throws Exception{
		ContentResolver resolver = context.getContentResolver();
		File file = new File(Environment.getExternalStorageDirectory(),"backup.xml");
		FileOutputStream fos = new FileOutputStream(file);
		// 将用户的短信一条一条的创建出来，保存在XML文件中
		XmlSerializer serializer = Xml.newSerializer();//获取项目了文件的生成器
		// c初始化生成器
		serializer.setOutput(fos,"utf-8");
		// 头
		serializer.startDocument("utf-8", true);
		serializer.startTag(null, "smss");
		// 获取用户的短信
		Uri uri = Uri.parse("content://sms/");
		Cursor cursor = resolver.query(uri, new String[]{"body","address","type","date"}, null, null, null);
		// 获取短信总数量
		int max = cursor.getCount();
//		pd.setMax(max);
		callBack.SmsMaxBackup(max);
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
			// 设置备份的进度
			progress ++;
//			pd.setProgress(progress);
			callBack.onSmsBackup(progress);
		}
		cursor.close();
		serializer.endTag(null, "smss");
		serializer.endDocument();
		fos.close();
	}
}
