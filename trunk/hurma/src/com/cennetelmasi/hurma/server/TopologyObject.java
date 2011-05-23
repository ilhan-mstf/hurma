package com.cennetelmasi.hurma.server;

import java.util.ArrayList;

public class TopologyObject {
	private String duration;
	private String simulationType;
	private String name;
	private ArrayList<NodeObj> nodes;
	
	public TopologyObject(){
		nodes = new ArrayList<NodeObj>();
		duration = null;
		simulationType = null;
		name = null;
	}
	
	public void addToNode(NodeObj node){
		nodes.add(node);
	}
	
	public NodeObj getNodeAt(int index){
		if(index < nodes.size())
			return nodes.get(index);
		else
			return null;
	}
	
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public String getSimulationType() {
		return simulationType;
	}
	public void setSimulationType(String simulationType) {
		this.simulationType = simulationType;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<NodeObj> getNodes() {
		return nodes;
	}
	public void setNodes(ArrayList<NodeObj> nodes) {
		this.nodes = nodes;
	}
}
