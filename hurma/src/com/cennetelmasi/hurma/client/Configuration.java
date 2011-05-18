package com.cennetelmasi.hurma.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Configuration implements EntryPoint {
	private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);
    Simulation simulation = new Simulation();
	
    final Button saveButton 		= new Button("  Save  ");
    final Button clearButton 		= new Button(" Clear ");
    final Button loadButton 		= new Button("  Load  ");
    final Button logoutButton 		= new Button(" Logout ");
    final Button runButton 			= new Button("  Run  ");
    final Button pauseButton 		= new Button(" Pause ");
    final Button resumeButton 		= new Button(" Resume ");
    final Button stopButton 		= new Button(" Stop ");
    final Button addNewDevice		= new Button(" New Device");
    final TextBox nameTextField 	= new TextBox();
    final ListBox simulationTypeLB 	= new ListBox(false);
    final TextBox durationHour 		= new TextBox();
    final TextBox durationMinute 	= new TextBox();
    final TextBox durationSecond 	= new TextBox();
    final Label duration 			= new Label();
    final String height 			= new String("15px");
    
    public Configuration(Simulation simulation) {
    	greetingService.nodeTypeNumber(new AsyncCallback<String>() {
            public void onFailure(Throwable caught) {
            	// TODO Show the RPC error message to the user
            }

			@Override
			public void onSuccess(String result) {
				int length = Integer.parseInt(result);
				
				for(int i = 0; i < length - 1; i++) {
					greetingService.getNodeTypeValues(i, new AsyncCallback<String[]>() {
						public void onFailure(Throwable caught) {}
						@Override
						public void onSuccess(String[] values) {
							new NodeType(values).onModuleLoad();
						}
					});
				}
			}
    	});
    	
    	this.simulation.setSimulationName(simulation.getSimulationName());
    	this.simulation.setSimulationId(simulation.getSimulationId());
    	this.simulation.setSimulationDurationHour(simulation.getSimulationDurationHour());
    	this.simulation.setSimulationDurationMinute(simulation.getSimulationDurationMinute());
    	this.simulation.setSimulationDurationSecond(simulation.getSimulationDurationSecond());
    	this.simulation.setNodeList(simulation.getNodeList());
    }
	
    public void onModuleLoad() {
    	final StringBuffer durationText = new StringBuffer("");
    	final StringBuffer runText = new StringBuffer("Simulation begins!..\n\n");
    	runText.append("Devices in the network:\n");
    	final TextArea text = new TextArea();
    	text.setWidth("190px");
    	text.setHeight("500px");
    	final int initialLength = runText.length();
    	
    	final Timer timer = new Timer() {
			@Override
			public void run() {
				int i = Random.nextInt();
    			if(i >0)
    				runText.append("alarm\n");
    			text.setText(runText.toString());
    		}
    	};
    	
    	final Timer clock = new Timer() {
    		int hour = 0, min = 0;
			int sec = 0;
			
    		@Override
			public void run() {
    			sec++;
    			if(sec >= 60){
    				sec = 0;
    				min++;
    				if(min >= 60){
    					min = 0;
    					hour++;
    				}
    			}
    			
    			durationText.delete(0, durationText.length());
    			if(hour < 10)	durationText.append("0");
    			durationText.append(hour + ":");
    			if(min < 10)	durationText.append("0");
    			durationText.append(min + ":");
    			if(sec < 10)	durationText.append("0");
    			durationText.append(sec);
    			duration.setText(durationText.toString());
    
    			if(stringToInteger(durationHour.getText()) <= hour 
    			&& stringToInteger(durationMinute.getText()) <= min 
    			&& stringToInteger(durationSecond.getText()) <= sec 
    			) {
    				this.cancel();
    				timer.cancel();
    				runText.append("\nTerminated\n");
        			text.setText(runText.toString());
        			pauseButton.setEnabled(false);
                    stopButton.setEnabled(false);
                    runButton.setEnabled(true);
                    return;
        			//RootPanel.get("node").add(text);
    			}
    		}
    	};
    	
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
    	RootPanel.get("pauseButton").add(pauseButton);
    	RootPanel.get("stopButton").add(stopButton);
    	RootPanel.get("addNewDevice").add(addNewDevice);
    	RootPanel.get("nameTextField").add(nameTextField);
    	RootPanel.get("simulationTypeLB").add(simulationTypeLB);
    	RootPanel.get("durationHour").add(durationHour);
    	RootPanel.get("durationMinute").add(durationMinute);
    	RootPanel.get("durationSecond").add(durationSecond);
    	RootPanel.get("duration").add(duration);
    	
    	pauseButton.setEnabled(false);
        stopButton.setEnabled(false);
    	
    	runButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
            	int size = RootPanel.get("networkTopology").getWidgetCount();
            	runText.delete(initialLength, runText.length());
            	for(int i=0; i<size; i++) {
            		runText.append(RootPanel.get("networkTopology").getWidget(i).getTitle() + "\n");
            	}
            	runText.append("\n");
            	RootPanel.get("node").clear();
            	RootPanel.get("node").add(text);
            	//RootPanel.get("devices").setVisible(false);
            	clock.run();
            	timer.scheduleRepeating(2000);
            	clock.scheduleRepeating(1000);
            	
            	pauseButton.setEnabled(true);
                stopButton.setEnabled(true);
                runButton.setEnabled(false);
            }
    	});
    	
    	pauseButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
            	RootPanel.get("pauseButton").remove(pauseButton);
            	RootPanel.get("pauseButton").add(resumeButton);
            	timer.cancel();
            	clock.cancel();
            	runText.append("Paused\n");
            	text.setText(runText.toString());
            	RootPanel.get("node").add(text);
            	stopButton.setEnabled(false);
            	
            }
    	});
    	
    	resumeButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
            	RootPanel.get("pauseButton").add(pauseButton);
            	RootPanel.get("pauseButton").remove(resumeButton);
            	timer.scheduleRepeating(2000);
            	clock.scheduleRepeating(1000);
            	runText.append("Resumed\n");
            	text.setText(runText.toString());
            	RootPanel.get("node").add(text);
            	stopButton.setEnabled(true);
            }
    	});
    	
    	stopButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
            	timer.cancel();
            	clock.cancel();
            	runText.append("Stop\n");
            	text.setText(runText.toString());
    			RootPanel.get("node").add(text);
            	pauseButton.setEnabled(false);
                stopButton.setEnabled(false);
                runButton.setEnabled(true);
            }
    	});
    	
    	saveButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
            	
            	System.out.println(RootPanel.get("networkTopology").getWidget(0).getTitle());
            	
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
            	RootPanel.get("pauseButton").remove(pauseButton);
            	RootPanel.get("stopButton").remove(stopButton);
            	RootPanel.get("nameTextField").remove(nameTextField);
            	RootPanel.get("simulationTypeLB").remove(simulationTypeLB);
            	RootPanel.get("durationHour").remove(durationHour);
            	RootPanel.get("durationMinute").remove(durationMinute);
            	RootPanel.get("durationSecond").remove(durationSecond);
            	RootPanel.get("node").clear();
            	timer.cancel();
        		RootPanel.get("loginPage").setVisible(true);
            }
        });
    
    }
    
    public int stringToInteger(String s) {
		int i, j, num = 0;
		for(i=s.length()-1, j=1; i>-1; i--, j*=10) {
			num += (s.charAt(i) - 48)*j;
		}
		return num;
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