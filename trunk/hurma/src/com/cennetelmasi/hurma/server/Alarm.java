package com.cennetelmasi.hurma.server;

import java.util.ArrayList;

import net.percederberg.mibble.MibValue;

public class Alarm {
	private MibValue oid;
	private String description;
	private String name;
	private ArrayList<Object> requiredObjects;
	
	public Alarm(){
		oid = null;
		description = null;
		name = null;
		requiredObjects = new ArrayList<Object>();
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<Object> getRequiredObjects() {
		return requiredObjects;
	}
	public void setRequiredObjects(ArrayList<Object> requiredObjects) {
		this.requiredObjects = requiredObjects;
	}
	
	public void addToRequiredObjects(Object o){
		this.requiredObjects.add(o);
	}
	
	public Object getObjectByOid(String oid){
		for(int i = 0; i < requiredObjects.size(); i++){
			if(requiredObjects.get(i).equals(oid))
				return requiredObjects.get(i);
		}
		return null;
	}
}
