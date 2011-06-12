package com.cennetelmasi.hurma.server;

import java.util.ArrayList;

public class Alarm {
	private String oid;
	private String description;
	private String name;
	private ArrayList<Object> requiredObjects;
	private boolean selected = false;
	private float prob;
	private int freq;

	public Alarm() {
		oid = null;
		description = null;
		name = null;
		requiredObjects = new ArrayList<Object>();
		selected = false;
	}
	
	public boolean isSelected(){
		return selected;
	}
	
	public void setSelectStatus(boolean status){
		selected = status;
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
	
	public void addToRequiredObjects(Object o) {
		this.requiredObjects.add(o);
	}
	
	public Object getObjectByOid(String oid) {
		for(int i = 0; i < requiredObjects.size(); i++){
			if(requiredObjects.get(i).equals(oid))
				return requiredObjects.get(i);
		}
		return null;
	}

	public void setProb(float prob) {
		this.prob = prob;
	}

	public float getProb() {
		return prob;
	}

	
	public void setFreq(int freq) {
		this.freq = freq;
	}

	
	public int getFreq() {
		return freq;
	}

}
