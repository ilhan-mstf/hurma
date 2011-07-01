package com.cennetelmasi.hurma.server;

public class MIBObject {
	private String oid;
	private String description;
	private String type;
	private String value;
	private String name;
	private boolean isSendable;
	
	public boolean isSendable() {
		return isSendable;
	}

	public void setSendable(boolean isSendable) {
		this.isSendable = isSendable;
	}

	public MIBObject() {
		isSendable = true;
		oid = new String();
		description = new String();
		type = new String();
		value = new String();
	}
	
	public MIBObject(String oid, String description, String type, String value, boolean isIP) {
		this.oid = oid;
		this.description = description;
		this.type = type;
		this.value = value;
	}
	
	@Override
	public String toString() {
		return "MIBObject [oid=" + oid + ", description=" + description
				+ ", type=" + type + ", value=" + value + ", name=" + name
				+ "]";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getOid() {
		return oid;
	}
	
	public void setOid(String oid) {
		this.oid = oid;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
}
