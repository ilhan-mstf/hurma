package com.cennetelmasi.hurma.client;

import java.util.ArrayList;

public class Simulation {
	private String simulationName;
	private String simulationId;
	private String simulationDurationHour;
	private String simulationDurationMinute;
	private String simulationDurationSecond;
	private String simulationType;
	private ArrayList<Node> nodeList = new ArrayList<Node>();
	
	public Simulation() {
		simulationName ="";
		simulationId ="";
		simulationDurationHour = "00";
		simulationDurationMinute = "00";
		simulationDurationSecond = "00";
		simulationType = "Reduced";
	}

	public String getSimulationName() {
		return simulationName;
	}

	public void setSimulationName(String simulationName) {
		this.simulationName = simulationName;
	}

	public String getSimulationId() {
		return simulationId;
	}

	public void setSimulationId(String simulationId) {
		this.simulationId = simulationId;
	}

	public String getSimulationDurationHour() {
		return simulationDurationHour;
	}

	public void setSimulationDurationHour(String simulationDurationHour) {
		this.simulationDurationHour = simulationDurationHour;
	}

	public String getSimulationDurationMinute() {
		return simulationDurationMinute;
	}

	public void setSimulationDurationMinute(String simulationDurationMinute) {
		this.simulationDurationMinute = simulationDurationMinute;
	}

	public String getSimulationDurationSecond() {
		return simulationDurationSecond;
	}

	public void setSimulationDurationSecond(String simulationDurationSecond) {
		this.simulationDurationSecond = simulationDurationSecond;
	}

	public String getSimulationType() {
		return simulationType;
	}

	public void setSimulationType(String simulationType) {
		this.simulationType = simulationType;
	}

	public ArrayList<Node> getNodeList() {
		return nodeList;
	}

	public void setNodeList(ArrayList<Node> nodeList) {
		this.nodeList = nodeList;
	}
}
