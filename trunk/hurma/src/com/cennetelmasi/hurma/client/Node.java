package com.cennetelmasi.hurma.client;

import java.util.ArrayList;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Firstly it creates a pop dialog box
 * shows alarms and required fields and
 * then it creates the node at network-topology
 */

public class Node implements EntryPoint {
	private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);
	private int id;
	private int nodeId;
	private String image;
	private String nodeTypeName;
	private String mib;
	private int numberOfDevice;
	private float prob;
	private boolean created = false;
	
    // Create list of alarms and properties
    private final ArrayList<CheckBox> checkBoxList = new ArrayList<CheckBox>();
    private final ArrayList<TextBox> propertyList = new ArrayList<TextBox>();
	
	public Node(int id, String nodeTypeName, String mib, String img) {
		this.id = id;
		this.setNodeTypeName(nodeTypeName);
		this.setMib(mib);
		this.image = img;
	}
	
	@Override
	public void onModuleLoad() {
		
		/********************
		 * DialogBox Code   *
		 ********************/
		
        // Create dialogBox, set properties
		final DialogBox dialogBox = new DialogBox();
        dialogBox.setText("Select alarms and fill required fields.");
        dialogBox.setGlassEnabled(true);
        dialogBox.setAnimationEnabled(true);
		        
        /********************************************************************
         * DialogBox style													*
         * 																	*
         * V Panel-0														*
         * ---------------------------------------------------------------- *
         * H Panel-0														*
         * ---------------------------------------------------------------- *
         * V Panel-1		| V Panel-2										*
         * -Alarms...		| H Panel-1										*
         * -				| --------------------------------------------- *
         * -				| V Panel-3 	| V Panel-4 	| V Panel-5		*
         * -				|				|				|				*
         * -				|				|				|				*
         * ---------------------------------------------------------------- *
         * H Panel-3														*
         * OK, Cancel Buttons												*
         ********************************************************************/
		
        // Create Horizontal and Vertical Panels, set properties
		final VerticalPanel[] dialogVPanel = new VerticalPanel[6];
		final HorizontalPanel[] dialogHPanel = new HorizontalPanel[3];
		for(int i=0; i<6; i++)
			dialogVPanel[i] = new VerticalPanel();
		for(int i=0; i<3; i++) {
			dialogHPanel[i] = new HorizontalPanel();
			dialogHPanel[i].setSpacing(5);
		}
        
        HTML label = new HTML("<b>Alarms of device</b><br />");
        label.addStyleName("label-DialogBox");
        dialogVPanel[1].add(label);
		// Add check-boxes of alarms
        // Make server call
        greetingService.getNodeObjValues(getMib(), new AsyncCallback<ArrayList<String>>() {
            public void onFailure(Throwable caught) {
            	RootPanel.get("rpcLoad").setVisible(false);
            	RootPanel.get("rpcError").setVisible(true);
            }

			@Override
			public void onSuccess(ArrayList<String> result) {
				/**
			     * result format:
			     * id, numberofAlarms, (AlarmName, AlarmOID), ... (ObjectName, ObjectOID) ...
			     */
				// set id
				setNodeId(Integer.parseInt(result.get(0)));
				// calculate alarm boundary
				// size = result[1]
				// each alarm is 2 array element
				int alarmBoundary = Integer.parseInt(result.get(1))*2+2;
				for(int i = 2; i < alarmBoundary; i++) {
			        CheckBox checkBox = new CheckBox(result.get(i));
			        i++;
			        checkBox.getElement().setId(result.get(i));
			        checkBox.ensureDebugId(result.get(i));
			        dialogVPanel[1].add(checkBox);
			        getCheckBoxList().add(checkBox);
			    }
		        HTML label = new HTML("<b>Required Fields</b>");
		        label.addStyleName("label-DialogBox");
		        dialogVPanel[2].insert(label, 0);
				// Number of devices
				TextBox numberOfDevices = new TextBox();
				numberOfDevices.getElement().setTitle("numberOfDevices");
				label = new HTML("<b>Number of devices:</b>");
				dialogVPanel[3].add(label);
				dialogVPanel[3].add(numberOfDevices);
				getPropertyList().add(numberOfDevices);
				// Probability
				TextBox prob = new TextBox();
				prob.getElement().setTitle("errorProbability");
				label = new HTML("<b>Error probability:</b>");
				dialogVPanel[3].add(label);
				dialogVPanel[3].add(prob);
				getPropertyList().add(prob);
				// required fields
				int objectBoundary = result.size();
				int objectSize = objectBoundary-alarmBoundary+2;
				int max = objectSize/4-1;
				for(int i = alarmBoundary, panelIndex = 3; i < objectBoundary; i++) {
			    	String property = result.get(i++);
			    	label = new HTML("<b>"+ property +":</b>");
			        dialogVPanel[panelIndex].add(label);
			        TextBox field = new TextBox();
			        field.getElement().setTitle(property);
			        field.getElement().setId(result.get(i));
			        getPropertyList().add(field);
			        dialogVPanel[panelIndex].add(field);
			        if(i/2%max==0) panelIndex++;
			    }
            	RootPanel.get("rpcLoad").setVisible(false);
				dialogBox.center();
			}
        });
        
        // Create Buttons and set properties
        final Button okButton = new Button("OK");
        final Button cancelButton = new Button("Cancel");
        okButton.getElement().setId("okButton");
        cancelButton.getElement().setId("cancelButton");
        okButton.addStyleName("left");
        cancelButton.addStyleName("left");

        // Unify all panels and buttons
        dialogHPanel[2].add(okButton);
        dialogHPanel[2].add(cancelButton);
        for(int i=3; i<6; i++)
        	dialogHPanel[1].add(dialogVPanel[i]);
        dialogVPanel[2].add(dialogHPanel[1]);
        dialogHPanel[0].add(dialogVPanel[1]);
        dialogHPanel[0].add(dialogVPanel[2]);
        dialogVPanel[0].add(dialogHPanel[0]);
        dialogVPanel[0].add(dialogHPanel[2]);
        dialogBox.setWidget(dialogVPanel[0]);
        
		/********************
		 * NODE Code        *
		 ********************/
        
		// Add the disclosure panels to a panel
		final VerticalPanel vPanel = new VerticalPanel();
		vPanel.setSpacing(8);
		
		// Create a table to layout the form options
		final FlexTable layout = new FlexTable();
		layout.setCellSpacing(3);
		
		final Button removeButton = new Button("Remove");
		removeButton.addStyleName("right");
		final Button editButton = new Button("Edit");
		editButton.addStyleName("right");
		
		final Image img = new Image("img/" + this.image);
		
		// Add a title to the form
		layout.setHTML(0, 0, "<b>" + getNodeTypeName() + " - " + getNodeId() + "</b>");
		layout.setWidget(0, 2, removeButton);
		layout.setWidget(0, 1, editButton);
		layout.setWidget(1, 0, img);
		
		// Add advanced options to form in a disclosure panel
		final DisclosurePanel advancedDisclosure = new DisclosurePanel("Alarms and Values");
		advancedDisclosure.setAnimationEnabled(true);
		advancedDisclosure.ensureDebugId("debugId");
		final VerticalPanel alarmPanel = new VerticalPanel();
		advancedDisclosure.setContent(alarmPanel);
		
		final VerticalPanel innerPanel = new VerticalPanel();
		innerPanel.add(layout);
		innerPanel.add(advancedDisclosure);
		
		// Wrap the contents in a DecoratorPanel
		final DecoratorPanel decPanel = new DecoratorPanel();
		decPanel.setWidget(innerPanel);
		
		vPanel.add(decPanel);
		
		/********************
		 * BUTTON Handlers  *
		 ********************/
		
        okButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
            	if(getPropertyList().get(0).getValue().isEmpty() && getPropertyList().get(1).getValue().isEmpty()){
                	Window.alert("Please enter number of devices and error probability!");
					
                } else if(getPropertyList().get(0).getValue().isEmpty()){
                	Window.alert("Please enter number of devices!");
					
                } else if(getPropertyList().get(1).getValue().isEmpty()){
                	Window.alert("Please enter error probability!");
					
                } else {
                	layout.setHTML(0, 0, "<b>" + getNodeTypeName() + " - " + getNodeId() + "</b>");
                	setCreated(true);
                    dialogBox.hide();
                    alarmPanel.clear();
                    for(int i=0; i<getCheckBoxList().size(); i++) {
                    	if(getCheckBoxList().get(i).getValue()) {
                    		alarmPanel.add(new HTML(" -> " + getCheckBoxList().get(i).getHTML()));
                    	}
                    }
                    for(int i=0; i<getPropertyList().size(); i++){
                    	alarmPanel.add(new HTML(getPropertyList().get(i).getTitle() + ": " + getPropertyList().get(i).getValue()));
                    }
                    vPanel.setStyleName("left");
                    RootPanel.get("networkTopology").add(vPanel);
                	numberOfDevice = Integer.parseInt(getPropertyList().get(0).getValue());
                    prob = Float.parseFloat(getPropertyList().get(1).getValue());
                }
            }
        });
        
        cancelButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
				if(!created) {
					greetingService.deleteNodeObj(Integer.toString(id), new AsyncCallback<Void>() {
	
						@Override
						public void onFailure(Throwable caught) {
						}
	
						@Override
						public void onSuccess(Void result) {
						}
					});
				}
			}
		});
        
        editButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.center();
			}
		});
        
        removeButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
            	setCreated(false);
            	vPanel.removeFromParent();
            	try {
					finalize();
				} catch (Throwable e) {
					e.printStackTrace();
				}
				greetingService.deleteNodeObj(Integer.toString(id), new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(Void result) {
					}
				});
            }
        });
        
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setNodeTypeName(String nodeTypeName) {
		this.nodeTypeName = nodeTypeName;
	}

	public String getNodeTypeName() {
		return nodeTypeName;
	}

	public void setMib(String mib) {
		this.mib = mib;
	}

	public String getMib() {
		return mib;
	}

	public void setNumberOfDevice(int numberOfDevice) {
		this.numberOfDevice = numberOfDevice;
	}

	public int getNumberOfDevice() {
		return numberOfDevice;
	}

	public void setProb(float prob) {
		this.prob = prob;
	}

	public float getProb() {
		return prob;
	}

	public ArrayList<CheckBox> getCheckBoxList() {
		return checkBoxList;
	}

	public ArrayList<TextBox> getPropertyList() {
		return propertyList;
	}

	public void setCreated(boolean created) {
		this.created = created;
	}

	public boolean isCreated() {
		return created;
	}

	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}

	public int getNodeId() {
		return nodeId;
	}

}
