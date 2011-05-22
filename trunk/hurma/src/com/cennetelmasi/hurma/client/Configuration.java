package com.cennetelmasi.hurma.client;

import java.util.ArrayList;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Configuration implements EntryPoint {
	private final static GreetingServiceAsync greetingService = GWT.create(GreetingService.class);
    static Simulation simulation = new Simulation();
	
    final Button saveButton 		= new Button("Save");
    final Button clearButton 		= new Button("Clear");
    final Button loadButton 		= new Button("Load");
    final Button logoutButton 		= new Button("Logout");
    final Button runButton 			= new Button("Run");
    final Button addNewDevice		= new Button("New Device");
    final TextBox nameTextField 	= new TextBox();
    final ListBox simulationTypeLB 	= new ListBox(false);
    final TextBox durationHour 		= new TextBox();
    final TextBox durationMinute 	= new TextBox();
    final TextBox durationSecond 	= new TextBox();
    
    public Configuration(final Simulation sim) {
    	simulation = sim;
    	
    	// TODO Check for a simulation is already running or not...
    	
    	// Make server call to generate NodeTypes
    	greetingService.getNodeTypes(new AsyncCallback<ArrayList<String>>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Show the RPC error message to the user
				
			}

			@Override
			public void onSuccess(ArrayList<String> result) {
			    /**
			     * Result format:
			     * numberOfNodeTypes, (id, name, mib) ...
			     */
				int size = Integer.parseInt(result.get(0));
				for(int i=0; i<size; i++) {
					int index = i*4+1;
					new NodeType(result.get(index), result.get(index+1), 
								 result.get(index+2), result.get(index+3), simulation).onModuleLoad();
				}
				
			}
		});
    }
	
    public void onModuleLoad() {    	
        durationHour.setWidth("18px");
        durationMinute.setWidth("18px");
        durationSecond.setWidth("18px");
        durationHour.setText("00");
        durationMinute.setText("00");
        durationSecond.setText("00");
        
        simulationTypeLB.setVisibleItemCount(1);
        simulationTypeLB.addItem("Real Time");
        simulationTypeLB.addItem("Reduced Time");
        
    	RootPanel.get("loginPage").setVisible(false);
    	RootPanel.get("clearButton").add(clearButton);
    	RootPanel.get("saveButton").add(saveButton);
    	RootPanel.get("loadButton").add(loadButton);
    	RootPanel.get("logoutButton").add(logoutButton);
    	RootPanel.get("runButton").add(runButton);
    	RootPanel.get("addNewDevice").add(addNewDevice);
    	RootPanel.get("nameTextField").add(nameTextField);
    	RootPanel.get("simulationTypeLB").add(simulationTypeLB);
    	RootPanel.get("durationHour").add(durationHour);
    	RootPanel.get("durationMinute").add(durationMinute);
    	RootPanel.get("durationSecond").add(durationSecond);
    	
		/********************
		 * BUTTON Handlers  *
		 ********************/
		 
    	addNewDevice.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				NewDevice dv = new NewDevice();
				dv.onModuleLoad();
			}
		});
    	
    	runButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
            	
            	// Set simulation values
            	simulation.setSimulationDurationHour(durationHour.getValue());
            	simulation.setSimulationDurationMinute(durationMinute.getValue());
            	simulation.setSimulationDurationSecond(durationSecond.getValue());
            	simulation.setSimulationType(simulationTypeLB.getValue(simulationTypeLB.getSelectedIndex()));
            	
            	// TODO Create pop-up
            	SimulationConsole console = new SimulationConsole(simulation);
            	console.onModuleLoad();
                
            }
    	});
    	    	
    	saveButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
            	
            	System.out.println(RootPanel.get("networkTopology").getWidget(0).getTitle());
            	
            	/** 
            	 * Steps to handle;
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
            	dialogBox.setAnimationEnabled(true);
            	dialogBox.setGlassEnabled(true);
            	final VerticalPanel dialogVPanel = new VerticalPanel();
                final Button closeButton = new Button("OK");
                final ListBox fileListBox = new ListBox(false);
                fileListBox.setVisibleItemCount(5);
                
                /**
                 * Following items are temporary
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
                    	// Add a function to handle the events after loading the file
                    	dialogBox.removeFromParent();
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
                RootPanel.get("networkTopology").clear();
            }
		});
            
        logoutButton.addClickHandler(new ClickHandler() {
        	public void onClick(ClickEvent event) {
                RootPanel.get("clearButton").remove(clearButton);
            	RootPanel.get("saveButton").remove(saveButton);
            	RootPanel.get("loadButton").remove(loadButton);
            	RootPanel.get("logoutButton").remove(logoutButton);
            	RootPanel.get("runButton").remove(runButton);
            	RootPanel.get("nameTextField").remove(nameTextField);
            	RootPanel.get("simulationTypeLB").remove(simulationTypeLB);
            	RootPanel.get("durationHour").remove(durationHour);
            	RootPanel.get("durationMinute").remove(durationMinute);
            	RootPanel.get("durationSecond").remove(durationSecond);
            	RootPanel.get("node").clear();
        		RootPanel.get("loginPage").setVisible(true);
            }
        });
    
    }

	public static void refreshTheNodeList() {
		greetingService.getNodeTypes(new AsyncCallback<ArrayList<String>>() {
			public void onFailure(Throwable caught) {}
			@Override
			public void onSuccess(ArrayList<String> result) {
			    /**
			     * Result format:
			     * numberOfNodeTypes, (id, name, mib) ...
			     */
				int size = Integer.parseInt(result.get(0));
				int i = size - 1;
				int index = i*4+1;
				new NodeType(result.get(index), result.get(index+1), 
							 result.get(index+2), result.get(index+3), simulation).onModuleLoad();
				
			}
		});
		
	}

}