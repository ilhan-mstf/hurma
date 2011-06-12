package com.cennetelmasi.hurma.client;

import java.util.ArrayList;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
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
    	
    	// Check for a simulation is already running or not...
    	greetingService.getSimulationState(new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				RootPanel.get("rpcError").setVisible(true);
			}

			@Override
			public void onSuccess(String result) {
				simulation.setSimulationState(result);
				if(!result.equals("ready")) {
					load("lastSession.xml");
					SimulationConsole console = new SimulationConsole(simulation);
					console.onModuleLoad();
				}
			}
		});
    	
    	// Make server call to generate NodeTypes
    	greetingService.getNodeTypes(new AsyncCallback<ArrayList<String>>() {

			@Override
			public void onFailure(Throwable caught) {
				RootPanel.get("rpcError").setVisible(true);
			}

			@Override
			public void onSuccess(ArrayList<String> result) {
			    /**
			     * Result format:
			     * numberOfNodeTypes, (id, name, mib, icon) ...
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
        simulationTypeLB.addItem("2 x Reduced Time");
        simulationTypeLB.addItem("4 x Reduced Time");
        simulationTypeLB.addItem("8 x Reduced Time");
        simulationTypeLB.addItem("16 x Reduced Time");
        
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
            	simulation.setSimulationType(simulationTypeLB.getSelectedIndex());
            	simulation.setSimulationState("ready");
            	
            	// TODO Create pop-up
            	SimulationConsole console = new SimulationConsole(simulation);
            	console.onModuleLoad();
                
            }
    	});
    	    	
    	saveButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
            	if(nameTextField.getText().isEmpty())
            		Window.alert("Simulation name empty");
            	else
            		save(nameTextField.getText());
            }
    	});
    	
    	loadButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {            	
            	final DialogBox dialogBox = new DialogBox();
            	dialogBox.setAnimationEnabled(true);
            	dialogBox.setGlassEnabled(true);
            	
            	final VerticalPanel dialogVPanel = new VerticalPanel();
                // Add a button to upload the file
                Button uploadButton = new Button("OK");
                Button cancelButton = new Button("Cancel");
                HorizontalPanel buttons = new HorizontalPanel();
                buttons.add(uploadButton);
                buttons.add(cancelButton);
                buttons.setSpacing(6);
                
                final ListBox fileListBox = new ListBox(false);
                fileListBox.setVisibleItemCount(5);
                
                greetingService.getSavedSimulationName(new AsyncCallback<ArrayList<String>>() {

					@Override
					public void onFailure(Throwable arg0) {
						RootPanel.get("rpcLoad").setVisible(false);
		            	RootPanel.get("rpcError").setVisible(true);
					}

					@Override
					public void onSuccess(ArrayList<String> arg0) {
						for(int i=0; i<arg0.size(); i++)
							fileListBox.addItem(arg0.get(i));
					}
				});
                
                dialogBox.setText("Please select a file to load");
                dialogBox.setAnimationEnabled(true);
                dialogBox.center();

                dialogVPanel.addStyleName("dialogVPanel");
                dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
                dialogVPanel.add(fileListBox);
                dialogVPanel.add(new HTML("<br/><br/>"));
                dialogVPanel.add(buttons);
                
                dialogBox.setWidget(dialogVPanel);
                dialogBox.center();
                
                uploadButton.addClickHandler(new ClickHandler() {
                    public void onClick(ClickEvent event) {
                    	dialogBox.hide();
                    	String fileName = fileListBox.getValue(fileListBox.getSelectedIndex());
                        if (fileName.length() == 0)
                            Window.alert("Error!");
                        load(fileName);
                    }
                });
                
                cancelButton.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						dialogBox.hide();
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
                simulation.getNodeList().clear();
                greetingService.clear(new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						RootPanel.get("rpcLoad").setVisible(false);
		            	RootPanel.get("rpcError").setVisible(true);
					}

					@Override
					public void onSuccess(Void result) {
						// TODO Auto-generated method stub
					}
				});
            }
		});
            
        logoutButton.addClickHandler(new ClickHandler() {
        	public void onClick(ClickEvent event) {
        		
        		save("lastSession");

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
    
    public void save(String fileName) {
    	// save node values
    	simulation.createNodeValues(false, null, null);
    	ArrayList<String> values = new ArrayList<String>();
    	values.add(fileName);
    	values.add(Integer.toString(simulationTypeLB.getSelectedIndex()));
    	values.add(durationHour.getText());
    	values.add(durationMinute.getText());
    	values.add(durationSecond.getText());
    	greetingService.saveSimulation(values, new AsyncCallback<Void>() {
    		@Override
			public void onFailure(Throwable caught) {
				RootPanel.get("rpcLoad").setVisible(false);
            	RootPanel.get("rpcError").setVisible(true);
    		}
			@Override
			public void onSuccess(Void result) {}
			
		});
    }
    
    public void load(String fileName) {
    	RootPanel.get("rpcLoad").setVisible(true);
    	greetingService.loadSimulation(fileName, new AsyncCallback<ArrayList<String>>() {

			@Override
			public void onFailure(Throwable caught) {
				RootPanel.get("rpcLoad").setVisible(false);
            	RootPanel.get("rpcError").setVisible(true);
			}

			@Override
			public void onSuccess(ArrayList<String> result) {
				//for(String str : result) System.out.println(str);
				/**
				 * result format:
				 * name, simulationType, duration, numberOfNode, 
				 * (nodeId), ...
				 */
				simulation.setSimulationName(result.get(0));
				simulation.setSimulationType(Integer.parseInt(result.get(1)));
				String[] duration = result.get(2).split(":");
				simulation.setSimulationDurationHour(duration[0]);
				simulation.setSimulationDurationMinute(duration[1]);
				simulation.setSimulationDurationSecond(duration[2]);
				
				nameTextField.setText(result.get(0));
				simulationTypeLB.setSelectedIndex(Integer.parseInt(result.get(1)));
				durationHour.setValue(duration[0]);
				durationMinute.setValue(duration[1]);
				durationHour.setValue(duration[2]);
				
				int size = Integer.parseInt(result.get(3));
				for(int i=0; i<size; i++) {
					RootPanel.get("rpcLoad").setVisible(true);
					Node n = new Node(Integer.parseInt(result.get(i+4)));
					n.onModuleLoad();
				}
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
			     * numberOfNodeTypes, (id, name, mib, icon) ...
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