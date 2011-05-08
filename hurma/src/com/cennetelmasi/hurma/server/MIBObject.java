package com.cennetelmasi.hurma.server;

import net.percederberg.mibble.MibValue;

public class MIBObject {
	private MibValue oid;
	private String description;
	private String type;
	@Override
	public String toString() {
		return "MIBObject [oid=" + oid + ", description=" + description
				+ ", type=" + type + ", value=" + value + ", name=" + name
				+ "]";
	}
	private String value;
	private String name;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public MIBObject(){
		oid = null;
		description = null;
		type = null;
		value = null;
	}
	
	public MIBObject(MibValue oid, String description, String type, String value){
		this.oid = oid;
		this.description = description;
		this.type = type;
		this.value = value;
	}
	
	public MibValue getOid() {
		return oid;
	}
	public void setOid(MibValue oid) {
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
