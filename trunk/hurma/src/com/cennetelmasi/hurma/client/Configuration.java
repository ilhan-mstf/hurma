package com.cennetelmasi.hurma.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Configuration implements EntryPoint {
    Simulation simulation = new Simulation();
	final Button saveButton = 	new Button("  Save  ");
    final Button clearButton = 	new Button(" Clear ");
    final Button loadButton = 	new Button("  Load  ");
    final Button logoutButton = new Button(" Logout ");
    final Button runButton = 	new Button("  Run  ");
    final Button pauseButton = 	new Button(" Pause ");
    final Button stopButton = 	new Button(" Stop ");
    final TextBox nameTextField = new TextBox();
    final ListBox simulationTypeLB = new ListBox(false);
    final TextBox durationHour = new TextBox();
    final TextBox durationMinute = new TextBox();
    final TextBox durationSecond = new TextBox();
    final String height = new String("15px");
    
    public Configuration(Simulation simulation){
    	this.simulation.setSimulationName(simulation.getSimulationName());
    	this.simulation.setSimulationId(simulation.getSimulationId());
    	this.simulation.setSimulationDurationHour(simulation.getSimulationDurationHour());
    	this.simulation.setSimulationDurationMinute(simulation.getSimulationDurationMinute());
    	this.simulation.setSimulationDurationSecond(simulation.getSimulationDurationSecond());
    	this.simulation.setNodeList(simulation.getNodeList());
    }
	
        public void onModuleLoad(){
            durationHour.setWidth("20px");
            durationMinute.setWidth("20px");
            durationSecond.setWidth("20px");
            durationHour.setText("00");
            durationMinute.setText("00");
            durationSecond.setText("00");
            
            simulationTypeLB.setVisibleItemCount(1);
            simulationTypeLB.addItem("Real Time");
            simulationTypeLB.addItem("Reduced");
            
        	RootPanel.get("loginPage").setVisible(false);
        	RootPanel.get("clearButton").add(clearButton);
        	RootPanel.get("saveButton").add(saveButton);
        	RootPanel.get("loadButton").add(loadButton);
        	RootPanel.get("logoutButton").add(logoutButton);
        	RootPanel.get("runButton").add(runButton);
        	RootPanel.get("pauseButton").add(pauseButton);
        	RootPanel.get("stopButton").add(stopButton);
        	RootPanel.get("nameTextField").add(nameTextField);
        	RootPanel.get("simulationTypeLB").add(simulationTypeLB);
        	RootPanel.get("durationHour").add(durationHour);
        	RootPanel.get("durationMinute").add(durationMinute);
        	RootPanel.get("durationSecond").add(durationSecond);
        	
        	saveButton.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                	/* Steps to handle;
                	 * 1. Check if there is a file which has name nameTextField.getText(); (e.g. simulation_01)
                	 * 2. If there exists a file with name nameTextField.getText();
                	 * 	  whether save it as nameTextField.getText() + "_2" (e.g. simulation_01_02)
                	 *    or ask for another name with a pop-up
                	 * 3. Save the xml file
                	 * 	  - Read data from fields
                	 * 	  - Read data from devices
                	 *    - construct xml structure
                	 * 4. After saving check whether it succeeded or not
                	 * 	  check if the nameTextField.getText.xml exists.
                	 * 5. If so pop up "Success" else "Error" 
                	 */
                }
        	});
        	
        	loadButton.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                	final DialogBox dialogBox = new DialogBox();
                	final VerticalPanel dialogVPanel = new VerticalPanel();
                    final Button closeButton = new Button("OK");
                    final ListBox fileListBox = new ListBox(false);
                    fileListBox.setVisibleItemCount(5);
                    
                    setButtons(false);
                    
                    /*following items are temporary
                     * that is, in real system, we will get file names 
                     * from a  specific directory
                     */
                    
                    fileListBox.addItem("Simulation_27.11.2010.xml");
                    fileListBox.addItem("Simulation_03.12.2010.xml");
                    fileListBox.addItem("Simulation_12.12.2010.xml");
                    fileListBox.addItem("Simulation_13.12.2010.xml");
                    fileListBox.addItem("Simulation_01.01.2011.xml");
                    fileListBox.addItem("Simulation_04.01.2011.xml");
                    fileListBox.addItem("Simulation_21.01.2011.xml");
                    
                    dialogBox.setText("Please select a file to load");
                    dialogBox.setAnimationEnabled(true);
                    dialogBox.center();

                    dialogVPanel.addStyleName("dialogVPanel");
                    dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
                    dialogVPanel.add(fileListBox);
                    dialogVPanel.add(new HTML("<br/><br/>"));
                    dialogVPanel.add(closeButton);
                    
                    dialogBox.setWidget(dialogVPanel);
                    dialogBox.center();
                    
                    closeButton.addClickHandler(new ClickHandler() {
                        public void onClick(ClickEvent event) {
                        	//add a function to handle the events after loading the file
                        	dialogBox.removeFromParent();
                        	setButtons(true);
                        }
                    });    
                }
			});
            
			clearButton.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                	durationHour.setText("00");
                    durationMinute.setText("00");
                    durationSecond.setText("00");
                    nameTextField.setText("");
                }
			});
                
            logoutButton.addClickHandler(new ClickHandler() {
            	public void onClick(ClickEvent event) {
                    RootPanel.get("clearButton").remove(clearButton);
                	RootPanel.get("saveButton").remove(saveButton);
                	RootPanel.get("loadButton").remove(loadButton);
                	RootPanel.get("logoutButton").remove(logoutButton);
                	RootPanel.get("runButton").remove(runButton);
                	RootPanel.get("pauseButton").remove(pauseButton);
                	RootPanel.get("stopButton").remove(stopButton);
                	RootPanel.get("nameTextField").remove(nameTextField);
                	RootPanel.get("simulationTypeLB").remove(simulationTypeLB);
                	RootPanel.get("durationHour").remove(durationHour);
                	RootPanel.get("durationMinute").remove(durationMinute);
                	RootPanel.get("durationSecond").remove(durationSecond);
            		RootPanel.get("loginPage").setVisible(true);
                }
            });
        }
        
        public void setButtons(boolean bool){
        	saveButton.setEnabled(bool);
            clearButton.setEnabled(bool);
            loadButton.setEnabled(bool);
            logoutButton.setEnabled(bool);
            runButton.setEnabled(bool);
            pauseButton.setEnabled(bool);
            stopButton.setEnabled(bool);
        }
}