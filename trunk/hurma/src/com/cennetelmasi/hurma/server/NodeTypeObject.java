package com.cennetelmasi.hurma.server;

public class NodeTypeObject {
	private int id;
	private String name;
	private String MIB;
	private String icon;
	
	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getMIB() {
		return MIB;
	}
	
	public void setMIB(String mIB) {
		MIB = mIB;
	}

}
