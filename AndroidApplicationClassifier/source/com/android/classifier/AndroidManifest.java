package com.android.classifier;

import java.util.ArrayList;
import java.util.List;

public class AndroidManifest {
	
	private String applicationName;
	
	private String playStoreCategory;
	
	private String processName;
	
	private List<Permission> permissions;
	
	private List<String> usesPermissions;
	
	private List<String> usesfeatures;
	
	private List<String> usesPermissionsSDK;

	public AndroidManifest(String pApplicationName, String pPlayStoreCategory, String pManifestPath) {
		super();
		applicationName = pApplicationName;
		playStoreCategory = pPlayStoreCategory;
		processName = pManifestPath;
		permissions = new ArrayList<Permission>();
		usesfeatures = new ArrayList<String>();
		usesPermissions = new ArrayList<String>();
		usesPermissionsSDK = new ArrayList<String>();
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getPlayStoreCategory() {
		return playStoreCategory;
	}

	public void setPlayStoreCategory(String playStoreCategory) {
		this.playStoreCategory = playStoreCategory;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public List<Permission> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<Permission> permissions) {
		this.permissions = permissions;
	}

	public List<String> getUsesPermissions() {
		return usesPermissions;
	}

	public void setUsesPermissions(List<String> usesPermissions) {
		this.usesPermissions = usesPermissions;
	}

	public List<String> getUsesfeatures() {
		return usesfeatures;
	}

	public void setUsesfeatures(List<String> usesfeatures) {
		this.usesfeatures = usesfeatures;
	}

	public List<String> getUsesPermissionsSDK() {
		return usesPermissionsSDK;
	}

	public void setUsesPermissionsSDK(List<String> usesPermissionsSDK) {
		this.usesPermissionsSDK = usesPermissionsSDK;
	}
	
	
	
	
	
	
}
