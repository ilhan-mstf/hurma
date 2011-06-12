package com.cennetelmasi.hurma.client;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;

public class Simulation {
	private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);
	
	private String simulationName;
	private String simulationId;
	private String simulationDurationHour;
	private String simulationDurationMinute;
	private String simulationDurationSecond;
	private int simulationType;
	private String simulationState;
	private ArrayList<Node> nodeList = new ArrayList<Node>();
	
	private ArrayList<String> values;
	private ArrayList<String> selectedAlarms;
	private ArrayList<String> requiredFields;
	
	/**
	 * values Format
	 * id, nodeName, numberOfDevices, ip
	 * 
	 * selectedAlarms Format
	 * (oid, prob, frequency), ...
	 * 
	 * requiredFields
	 * (oid, value), ...
	 */
	
	public Simulation() {
		simulationName ="";
		simulationId ="";
		simulationDurationHour = "00";
		simulationDurationMinute = "00";
		simulationDurationSecond = "00";
		simulationType = 0;
	}
	
	public void createNodeValues(boolean isSimulation, StringBuffer runText, TextArea console) {
		// Send simulation configuration to the server
        for(int i=0; i<getNodeList().size(); i++) {
        	final Node n = getNodeList().get(i);
        	if(n.isCreated()) {
        		// Values
        		values = new ArrayList<String>();
        		values.add(Integer.toString(n.getNodeId()));
        		values.add(n.getNodeTypeName());
        		values.add(n.getNumberOfDevices().getValue());
        		values.add(n.getIp().getValue());
        		values.add(n.getImage());
        		// Alarms
        		selectedAlarms = new ArrayList<String>();
        		for(int j=0; j<n.getAlarmList().size(); j++) {
        			CheckBox cb = n.getAlarmList().get(j);
        			if(cb.getValue()) {
        				selectedAlarms.add(cb.getElement().getId());
        				selectedAlarms.add(n.getProbList().get(j).getValue());
        				selectedAlarms.add(n.getFrequencyList().get(j).getSelectedIndex()+"");
        			}
        		}
        		// Required fields
        		requiredFields = new ArrayList<String>();
        		for(int j=0; j<n.getPropertyList().size(); j++) {
        			TextBox tb = n.getPropertyList().get(j);
        			requiredFields.add(tb.getElement().getId());
        			requiredFields.add(tb.getValue());
        		}
        		// Make server call
        		// if it is called from simulation
        		if(isSimulation)
        			sendNodeValuesForSimulation(runText, console);
        		// or from save operation
        		else sendNodeValuesForSave();
        	}
        	else {
        		getNodeList().remove(i);
        		i--;
        	}
        }
	}
	
	public void sendNodeValuesForSimulation(final StringBuffer runText, final TextArea console) {
		final String str = values.get(3);
		System.out.println(str);
		greetingService.setNodeObjValues(values, selectedAlarms, requiredFields,
				new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						runText.append("> ERROR: SNMP Agent is not created for " + str + "\n");
						console.setText(runText.toString());
					}

					@Override
					public void onSuccess(Void result) {
						runText.append("> SNMP Agent is created for " + str + "\n");
						console.setText(runText.toString());
					}
				});
	}
	
	public void sendNodeValuesForSave() {
		greetingService.setNodeObjValues(values, selectedAlarms, requiredFields,
				new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {

					}

					@Override
					public void onSuccess(Void result) {

					}
				});
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

	public int getSimulationType() {
		return simulationType;
	}

	public void setSimulationType(int simulationType) {
		this.simulationType = simulationType;
	}

	public ArrayList<Node> getNodeList() {
		return nodeList;
	}

	public void setNodeList(ArrayList<Node> nodeList) {
		this.nodeList = nodeList;
	}

	public void setSimulationState(String simulationState) {
		this.simulationState = simulationState;
	}

	public String getSimulationState() {
		return simulationState;
	}

}
