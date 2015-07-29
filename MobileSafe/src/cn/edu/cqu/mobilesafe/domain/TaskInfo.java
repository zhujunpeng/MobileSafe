package cn.edu.cqu.mobilesafe.domain;

import android.graphics.drawable.Drawable;

public class TaskInfo {

	private String name;
	private Drawable icon;
	private String packageName;
	private boolean userApp;
	private long appMem;
	private boolean checked;
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	public long getAppMem() {
		return appMem;
	}
	public void setAppMem(long appMem) {
		this.appMem = appMem;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public boolean isUserApp() {
		return userApp;
	}
	public void setUserApp(boolean userApp) {
		this.userApp = userApp;
	}
	@Override
	public String toString() {
		return "TaskInfo [name=" + name + ", packageName=" + packageName
				+ ", userApp=" + userApp + "]";
	}
}
