package com.android.classifier;

public class Permission {
	
	private String name;
	
	
	private String protectionLevel;
	
	private String permissionGroup;
	
	

	public Permission(String pName, String pProtectionLevel, String pPermissionGroup) {
		name = pName;
		protectionLevel = pProtectionLevel;
		permissionGroup = pPermissionGroup;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public String getProtectionLevel() {
		return protectionLevel;
	}

	public void setProtectionLevel(String protectionLevel) {
		this.protectionLevel = protectionLevel;
	}

	public String getPermissionGroup() {
		return permissionGroup;
	}

	public void setPermissionGroup(String permissionGroup) {
		this.permissionGroup = permissionGroup;
	}
	
	public String toString(){
		return "Name: " + name+"\nProtection Level :"+ protectionLevel+"\nPermission Group: "+ permissionGroup+"\n";
	}

}
