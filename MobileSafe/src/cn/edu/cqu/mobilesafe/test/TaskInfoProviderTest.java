package cn.edu.cqu.mobilesafe.test;

import java.util.List;

import cn.edu.cqu.mobilesafe.domain.TaskInfo;
import cn.edu.cqu.mobilesafe.engine.TaskInfoProvider;
import android.test.AndroidTestCase;

public class TaskInfoProviderTest extends AndroidTestCase {
	
	public void test() throws Exception{
		List<TaskInfo> taskInfos = TaskInfoProvider.getTaskInfos(getContext());
		for (TaskInfo taskInfo : taskInfos) {
			System.out.println(taskInfo.toString());
		}
		System.out.println(taskInfos.size());
	}
	

}
