package com.cennetelmasi.hurma.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SimulationConsole implements EntryPoint {
	private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);
	private Simulation simulation;
	
	private int cofactor;
	int hour = 0, minute = 0, second = 0;
	
	final Button closeButton 		= new Button("Close");
	final Button showButton			= new Button("Show Console");
    final Button pauseButton 		= new Button("Pause");
    final Button resumeButton 		= new Button("Resume");
    final Button stopButton 		= new Button("Stop");
    final Label simulationDuration	= new Label("");
    final Label passedTime 			= new Label("");
    final String height 			= new String("15px");
	final StringBuffer durationText = new StringBuffer("00:00:00");
	final StringBuffer runText 		= new StringBuffer("");
	final TextArea console			= new TextArea();
	
	public SimulationConsole(Simulation simulation) {
		this.simulation = simulation;
	}
	
	@Override
	public void onModuleLoad() {
		
    	/*********************
		 * Initialize pop-up *
		 *********************/
		
		// Calculate simulation type time
		cofactor = 1;
		for(int mustafa = 0; mustafa < simulation.getSimulationType(); mustafa++)
			cofactor *= 2;
		
		HorizontalPanel controlPanel = new HorizontalPanel();
		final HorizontalPanel buttons = new HorizontalPanel();
		VerticalPanel vPanel = new VerticalPanel();
		final DialogBox dialogBox = new DialogBox();
		dialogBox.setText("Simulation outputs");
		dialogBox.setAnimationEnabled(true);
		dialogBox.setGlassEnabled(true);
		controlPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		controlPanel.setWidth("100%");
		
		// Console Options 
    	console.addStyleName("console");
    	console.setWidth("600px");
    	console.setHeight("400px");
    	
    	buttons.add(pauseButton);
    	buttons.add(stopButton);
    	buttons.add(closeButton);
    	
    	RootPanel.get("showButton").add(showButton);
    	
    	String str = simulation.getSimulationDurationHour() + ":" +
    				 simulation.getSimulationDurationMinute() + ":" +
    				 simulation.getSimulationDurationSecond();
    	simulationDuration.setText("Simulation duration: " + str);
    	passedTime.setText("Passed Time: " + durationText);
    	controlPanel.add(simulationDuration);
    	controlPanel.add(passedTime);
    	controlPanel.add(buttons);
    	controlPanel.setCellHorizontalAlignment(buttons, HasHorizontalAlignment.ALIGN_RIGHT);
    	
    	vPanel.add(controlPanel);
    	vPanel.add(console);
    	dialogBox.setWidget(vPanel);
    	
    	dialogBox.center();
    	
    	/********************
		 * Timer			*
		 ********************/
    	
    	final Timer serverTimer = new Timer() {
    		@Override
			public void run() {
    			greetingService.getOutputs(new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) {
						runText.append("> ERROR getting outputs");
						console.setText(runText.toString());
					}

					@Override
					public void onSuccess(String result) {
						runText.append(result);
						console.setText(runText.toString());
					}
				});
    			
    		}
    	};
    	
    	final Timer timer = new Timer() {
    		
    		@Override
			public void run() {
    			second++;
    			if(second >= 60){
    				second = 0;
    				minute++;
    				if(minute >= 60){
    					minute = 0;
    					hour++;
    				}
    			}
    			
    			durationText.delete(0, durationText.length());
    			if(hour < 10)	durationText.append("0");
    			durationText.append(hour + ":");
    			if(minute < 10)	durationText.append("0");
    			durationText.append(minute + ":");
    			if(second < 10)	durationText.append("0");
    			durationText.append(second);
    			passedTime.setText("Passed Time: " + durationText.toString());
    
    			if(Integer.parseInt(simulation.getSimulationDurationHour()) <= hour 
    			&& Integer.parseInt(simulation.getSimulationDurationMinute()) <= minute 
    			&& Integer.parseInt(simulation.getSimulationDurationSecond()) <= second ) {
    				this.cancel();
    				runText.append("> Simulation successfully ended.");
        			console.setText(runText.toString());
                    return;
    			}
    		}
    	};
    	
    	/***********************
		 * Continue simulation *
		 ***********************/
    	
    	if(!simulation.getSimulationState().equals("ready")) {
    		greetingService.getPassedTime(new AsyncCallback<Integer>() {

				@Override
				public void onFailure(Throwable caught) {
					
				}

				@Override
				public void onSuccess(Integer result) {
					hour = result / 3600;
					minute = (result-3600*hour) / 60;
					second = result-minute*60 + hour*3600;
					
			    	String str = simulation.getSimulationDurationHour() + ":" +
						  		simulation.getSimulationDurationMinute() + ":" +
						  		simulation.getSimulationDurationSecond();
					simulationDuration.setText("Simulation duration: " + str);
					passedTime.setText("Passed Time: " + durationText);
					
					for(int mustafa = 0; mustafa < simulation.getSimulationType(); mustafa++)
						cofactor *= 2;

			    	if(simulation.getSimulationState().equals("running")) {
			    		runText.append("> Simulation is going to continue...\n");
						console.setText(runText.toString());
						timer.scheduleRepeating(1000/cofactor);
						serverTimer.scheduleRepeating(1000/cofactor);
			    	} else if(simulation.getSimulationState().equals("paused")) {
			    		runText.append("> Simulation was paused.\n");
			    		console.setText(runText.toString());
			        	buttons.remove(pauseButton);
			        	buttons.insert(resumeButton, 0);
			        	stopButton.setEnabled(false);
			    	}	    	
				}
			});
    	} else {
    		
    	/*********************
		 * Start simulation	 *
		 *********************/
    	
	        // Send simulation configuration to the server
	        simulation.createNodeValues(true, runText, console);
	    	
	        // Start simulation
	        int time = Integer.parseInt(simulation.getSimulationDurationHour())*60 +
	        		   Integer.parseInt(simulation.getSimulationDurationMinute())*60 +
	        		   Integer.parseInt(simulation.getSimulationDurationSecond());
	        
	        greetingService.startSimulation(time, cofactor, new AsyncCallback<Void>() {
	
				@Override
				public void onFailure(Throwable caught) {
					runText.append("> ERROR: Simulation is not started.\n");
					console.setText(runText.toString());
				}
	
				@Override
				public void onSuccess(Void result) {
					runText.append("> Simulation is started successfully...\n");
					console.setText(runText.toString());
				timer.scheduleRepeating(1000/cofactor);
				serverTimer.scheduleRepeating(1000/cofactor);
				}
			});
    	}

		/********************
		 * BUTTON Handlers  *
		 ********************/
    	
    	pauseButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
            	greetingService.pause(new AsyncCallback<String>() {

            		@Override
					public void onFailure(Throwable caught) {
						runText.append("> ERROR: Server is not paused.\n");
						console.setText(runText.toString());
					}

            		@Override
					public void onSuccess(String result) {
            			runText.append(result);
						runText.append("> Server is paused successfully...\n");
						console.setText(runText.toString());
					}
				});
            	buttons.remove(pauseButton);
            	buttons.insert(resumeButton, 0);
            	timer.cancel();
            	serverTimer.cancel();
            	stopButton.setEnabled(false);
            }
    	});
    	
    	resumeButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
            	greetingService.resume(new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						runText.append("> ERROR: Server is not resumed.\n");
						console.setText(runText.toString());
					}

					@Override
					public void onSuccess(Void result) {
						runText.append("> Server is resumed successfully...\n");
						console.setText(runText.toString());
					}
				});
            	buttons.remove(resumeButton);
            	buttons.insert(pauseButton, 0);
            	timer.scheduleRepeating(1000/cofactor);
            	serverTimer.scheduleRepeating(1000/cofactor);
            	stopButton.setEnabled(true);
            }
    	});
    	
    	stopButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
            	greetingService.stop(new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) {
						runText.append("> ERROR: Server is not stoped.\n");
						console.setText(runText.toString());
					}

					@Override
					public void onSuccess(String result) {
						runText.append(result);
						runText.append("> Server is stopped successfully...\n");
						console.setText(runText.toString());
					}
				});
            	timer.cancel();
            	pauseButton.setEnabled(false);
                stopButton.setEnabled(false);
            }
    	});
    	
    	closeButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				dialogBox.hide();
			}
		});
    	
    	showButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				dialogBox.center();
			}
		});
	
	}

}
