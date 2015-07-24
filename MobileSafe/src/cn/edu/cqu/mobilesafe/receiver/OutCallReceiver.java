package cn.edu.cqu.mobilesafe.receiver;

import cn.edu.cqu.mobilesafe.db.dao.NumberQueryUtils;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class OutCallReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// 得到拨出的电话号码
		String phone = getResultData();
		// 查询数据库
		String address = NumberQueryUtils.queryNumber(phone);
		Toast.makeText(context, address, Toast.LENGTH_LONG).show();
	}

}
