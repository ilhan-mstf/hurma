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
            	
            	// TODO Create pop-up
            	SimulationConsole console = new SimulationConsole(simulation);
            	console.onModuleLoad();
                
            }
    	});
    	    	
    	saveButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
            	// save node values
            	simulation.createNodeValues(false, null, null);
            	ArrayList<String> values = new ArrayList<String>();
            	values.add(nameTextField.getText());
            	values.add(Integer.toString(simulationTypeLB.getSelectedIndex()));
            	values.add(durationHour.getText());
            	values.add(durationMinute.getText());
            	values.add(durationSecond.getText());
            	greetingService.saveSimulation(values, new AsyncCallback<Void>() {
					@Override
					public void onSuccess(Void result) {
						Window.alert("Succesfully saved.");
					}
					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Operation failed.");
					}
				});
            	System.out.println(RootPanel.get("networkTopology").getWidget(0).getTitle());
            }
    	});
    	
    	loadButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
            	RootPanel.get("rpcLoad").setVisible(true);
            	final DialogBox dialogBox = new DialogBox();
            	dialogBox.setAnimationEnabled(true);
            	dialogBox.setGlassEnabled(true);
            	final VerticalPanel dialogVPanel = new VerticalPanel();
                final Button closeButton = new Button("OK");
                final ListBox fileListBox = new ListBox(false);
                fileListBox.setVisibleItemCount(5);
                
                greetingService.getSavedSimulationName(new AsyncCallback<ArrayList<String>>() {

					@Override
					public void onFailure(Throwable caught) {
		            	RootPanel.get("rpcLoad").setVisible(false);
		            	RootPanel.get("rpcError").setVisible(true);
					}

					@Override
					public void onSuccess(ArrayList<String> result) {
						for(int i=0; i<result.size(); i++)
							fileListBox.addItem(result.get(i));
						RootPanel.get("rpcLoad").setVisible(false);
						dialogBox.center();
					}
				});
                
                dialogBox.setText("Please select a file to load");
                dialogBox.setAnimationEnabled(true);
                dialogBox.center();

                dialogVPanel.addStyleName("dialogVPanel");
                dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
                dialogVPanel.add(fileListBox);
                dialogVPanel.add(new HTML("<br/><br/>"));
                dialogVPanel.add(closeButton);
                
                dialogBox.setWidget(dialogVPanel);
                
                closeButton.addClickHandler(new ClickHandler() {
                    public void onClick(ClickEvent event) {
                    	// Add a function to handle the events after loading the file
                    	dialogBox.hide();
                    	greetingService.loadSimulation("simulation.xml", new AsyncCallback<ArrayList<String>>() {

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
								int size = Integer.parseInt(result.get(3));
								for(int i=0; i<size; i++) {
									Node n = new Node(Integer.parseInt(result.get(i+4)));
									n.onModuleLoad();
								}
							}
						});
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
						// TODO Auto-generated method stub
						
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