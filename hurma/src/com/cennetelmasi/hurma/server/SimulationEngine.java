package com.cennetelmasi.hurma.server;

import java.util.ArrayList;


public class SimulationEngine {
	private ArrayList<SNMPagent> agents = new ArrayList<SNMPagent>(); 
	private ArrayList<NodeObj> nodes = new ArrayList<NodeObj>();
	private Protocol protocol;
	private SNMPagent timer;
	
	public SimulationEngine() {
		setProtocol(new Protocol());
	}
	
	public void start(int time) {
		for(NodeObj node : nodes) {
			SNMPagent agent = new SNMPagent("NODE", getProtocol(), node);
			agents.add(agent);
			agent.start();
		}
		timer = new SNMPagent("TIMER", getProtocol(), time);
		timer.start();
		System.out.println("server: threads are created.");
	}
	
	public void pause() {
		timer.pauseScheduler();
	}
	
	public void resume() {
		timer.resumeScheduler();
	}
	
	public void stop() {
		timer.pauseScheduler();
		timer.setStop(true);
		for(SNMPagent agent : agents)
			agent.setStop(true);
	}

	public void setNodes(ArrayList<NodeObj> nodes) {
		this.nodes = nodes;
	}

	public ArrayList<NodeObj> getNodes() {
		return nodes;
	}

	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}
	
	public Protocol getProtocol() {
		return protocol;
	}
	
}
