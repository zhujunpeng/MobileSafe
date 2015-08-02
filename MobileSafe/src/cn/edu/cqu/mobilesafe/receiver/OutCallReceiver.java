package cn.edu.cqu.mobilesafe.receiver;

import cn.edu.cqu.mobilesafe.utils.NumberQueryUtils;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class OutCallReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// �õ������ĵ绰����
		String phone = getResultData();
		// ��ѯ���ݿ�
		String address = NumberQueryUtils.queryNumber(phone);
		Toast.makeText(context, address, Toast.LENGTH_LONG).show();
	}

}
