package cn.edu.cqu.mobilesafe.test;

import java.util.List;

import cn.edu.cqu.mobilesafe.db.BlackNumberDBOpenHelper;
import cn.edu.cqu.mobilesafe.db.dao.BlackNumberDAO;
import cn.edu.cqu.mobilesafe.domain.BlackNumberInfo;
import android.test.AndroidTestCase;

public class TestBlackNumberDB extends AndroidTestCase {
	public void testCreateDB() throws Exception{
		BlackNumberDBOpenHelper helper = new BlackNumberDBOpenHelper(getContext());
		helper.getWritableDatabase();
	}
	
	public void testadd() throws Exception{
		BlackNumberDAO dao = new BlackNumberDAO(getContext());
		for (int i = 0; i <100; i++) {
			dao.add("1234" + i, "1");
		}
	}
	public void testdelete() throws Exception{
		BlackNumberDAO dao = new BlackNumberDAO(getContext());
		dao.delete("1234");
	}
	public void testupdate() throws Exception{
		BlackNumberDAO dao = new BlackNumberDAO(getContext());
		dao.update("1234", "2");
	}
	public void testfind() throws Exception{
		BlackNumberDAO dao = new BlackNumberDAO(getContext());
		boolean reslut = dao.find("1234");
		assertEquals(true, reslut);
	}
	public void testfindAll() throws Exception{
		BlackNumberDAO dao = new BlackNumberDAO(getContext());
		List<BlackNumberInfo> list = dao.findAll();
		for (BlackNumberInfo blackNumberInfo : list) {
			System.out.println(blackNumberInfo.toString());
		}
	}
}
