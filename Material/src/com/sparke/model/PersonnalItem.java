package com.sparke.model;

public class PersonnalItem {
	
	private String mKey;
	private String mValue;
	
	public PersonnalItem(String key, String value) {
		mKey = key;
		mValue = value;
	}
	
	public String getKey() {
		return mKey;
	}
	
	public String getValue() {
		return mValue;
	}
}
