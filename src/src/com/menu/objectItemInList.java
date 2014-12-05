package com.menu;

public class objectItemInList {
	private String pathToObjectImage;
	private String objectName;
	private String objectValue;
	
	public objectItemInList(String path, String name, String value) {
		this.pathToObjectImage = path;
		this.objectName = name;
		this.objectValue = value;
	}
	
	public void setImagePath(String newPath) {
		this.pathToObjectImage = newPath;
	}
	
	public void setObjectName(String newName) {
		this.objectName = newName;
	}
	
	public void setObjectValue(String newValue) {
		this.objectValue = newValue;
	}
	
	public String getImagePath() {
		return this.pathToObjectImage;
	}
	
	public String getObjectName() {
		return this.objectName;
	}
	
	public String getObjectValue() {
		return this.objectValue;
	}
}
