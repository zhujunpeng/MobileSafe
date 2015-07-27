package cn.edu.cqu.mobilesafe.domain;

import android.graphics.drawable.Drawable;

public class AppInfo {

	private Drawable icon;
	private String name;
	private String pakageName;
	private boolean userApp;
	private boolean romApp;
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPakageName() {
		return pakageName;
	}
	public void setPakageName(String pakageName) {
		this.pakageName = pakageName;
	}
	public boolean isUserApp() {
		return userApp;
	}
	public void setUserApp(boolean userApp) {
		this.userApp = userApp;
	}
	public boolean isRomApp() {
		return romApp;
	}
	public void setRomApp(boolean romApp) {
		this.romApp = romApp;
	}
	@Override
	public String toString() {
		return "AppInfo [icon=" + icon + ", name=" + name + ", pakageName="
				+ pakageName + ", userApp=" + userApp + ", romApp=" + romApp
				+ "]";
	}
	
}
