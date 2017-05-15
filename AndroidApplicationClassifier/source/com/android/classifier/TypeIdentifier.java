package com.android.classifier;

public class TypeIdentifier {

	public final static String GROUP = "Permission Group: ";
	
	public final static String HARDWARE_FEATURE = "Hardware Feature: ";
	
	private String nameType;
	
	private String securityClasses;
	
	private String reason;
	
	private String group;
	
	private String groupType;

	public TypeIdentifier(String nameType, String securityClasses, String reason, String group, String groupType) {
		super();
		this.nameType = nameType;
		this.securityClasses = securityClasses;
		this.reason = reason;
		this.group = group;
		this.groupType = groupType;
	}

	public String getNameType() {
		return nameType;
	}

	public void setNameType(String nameType) {
		this.nameType = nameType;
	}

	public String getSecurityClasses() {
		return securityClasses;
	}

	public void setSecurityClasses(String securityClasses) {
		this.securityClasses = securityClasses;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getGroupType() {
		return groupType;
	}

	public void setGroupType(String groupType) {
		this.groupType = groupType;
	}
	
	
	
}
