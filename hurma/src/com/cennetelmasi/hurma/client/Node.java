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
import com.google.gwt.user.client.ui.ListBox;
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
	private int nodeId;
	private String image;
	private String nodeTypeName;
	private String mib;
	private boolean created = false;
	private boolean isLoaded = true;
	
    // Create list of alarms and properties
    private final ArrayList<CheckBox> alarmList = new ArrayList<CheckBox>();
    private final ArrayList<TextBox> probList = new ArrayList<TextBox>();
    private final ArrayList<ListBox> frequencyList = new ArrayList<ListBox>();
    private final ArrayList<TextBox> propertyList = new ArrayList<TextBox>();
    
    private final TextBox numberOfDevices = new TextBox();
    private final TextBox ip = new TextBox();
    private final Image img = new Image();
    
	final VerticalPanel nodePanel = new VerticalPanel();
	final VerticalPanel alarmPanel = new VerticalPanel();
	final FlexTable layout = new FlexTable();
    
    final Button okButton = new Button("OK");
    final Button cancelButton = new Button("Cancel");
    final DialogBox dialogBox = new DialogBox();
    
    public Node(int id) {
    	this.nodeId = id;
    }
	
	public Node(String nodeTypeName, String mib, String img, boolean isLoaded) {
		this.setNodeTypeName(nodeTypeName);
		this.setMib(mib);
		this.setImage(img);
		this.isLoaded = isLoaded;
	}
	
	public void createNode() {
		greetingService.createNode(mib, new AsyncCallback<ArrayList<String>>() {

			@Override
			public void onFailure(Throwable caught) {
            	RootPanel.get("rpcLoad").setVisible(false);
            	RootPanel.get("rpcError").setVisible(true);
			}

			@Override
			public void onSuccess(ArrayList<String> result) {
			    init(result);
			}
		});
	}
	
	public void loadNode(String id) {
		greetingService.getNodeObjValuesById(id, new AsyncCallback<ArrayList<String>>() {

			@Override
			public void onFailure(Throwable caught) {
				RootPanel.get("rpcLoad").setVisible(false);
            	RootPanel.get("rpcError").setVisible(true);
			}

			@Override
			public void onSuccess(ArrayList<String> result) {
				init(result);
			}
		});
	}
	
	public void init(ArrayList<String> result) {
		/**
	     * result format:
	     * id, nodeName, numberOfDevices, ip, image
	     * numberofAlarms, (AlarmName, AlarmOID, isSelected, AlarmProb, AlarmFreq, ), ... 
	     * (ObjectName, ObjectOID, ObjectValue), ...
	     */
		
		nodeId = Integer.parseInt(result.get(0));
		if(result.get(1) != null)
			nodeTypeName = result.get(1);
		
		numberOfDevices.setText("Number Of Devices: ");
		numberOfDevices.setValue(result.get(2));
		
		ip.setText("IP: ");
		ip.setValue(result.get(3));
		
		if(result.get(4) != null)
			image = result.get(4);
		
		// Set alarms
		// Calculate alarm boundary
		// size = result[3]
		// Each alarm is 4 array element
		int alarmBoundary = Integer.parseInt(result.get(5))*5+6;
		for(int i = 6; i < alarmBoundary; i++) {
	        CheckBox checkBox = new CheckBox(result.get(i++));
	        checkBox.getElement().setId(result.get(i));
	        checkBox.ensureDebugId(result.get(i++));
	        checkBox.setValue(Boolean.parseBoolean(result.get(i++)));
	        alarmList.add(checkBox);
	        
	        TextBox prob = new TextBox();
	        prob.setValue(result.get(i++));
	        prob.setWidth("30px");
	        probList.add(prob);
	        
	        ListBox freq = new ListBox();
	        freq.addItem("second");
	        freq.addItem("minute");
	        freq.addItem("hour");
	        freq.addItem("day");
	        freq.addItem("week");
	        freq.addItem("month");
	        freq.addItem("year");
	        freq.setItemSelected(Integer.parseInt(result.get(i)), true);
	        frequencyList.add(freq);
	    }
		
		// Set objects
		int objectBoundary = result.size();
		for(int i = alarmBoundary; i < objectBoundary; i++) {
	    	String property = result.get(i++);
	        TextBox field = new TextBox();
	        field.setTitle(property);
	        field.getElement().setId(result.get(i++));
	        field.setValue(result.get(i));
	        getPropertyList().add(field);
	    }
		
		createDialogBox();
		RootPanel.get("rpcLoad").setVisible(false);
		if(!isLoaded)
			dialogBox.center();
		else
			addNode();
	}
	
	@Override
	public void onModuleLoad() {
		
		if(isLoaded)
			loadNode(nodeId+"");
		else
			createNode();
		
		/********************
		 * NODE Code        *
		 ********************/
        
		// Add the disclosure panels to a panel
		nodePanel.setSpacing(8);
		
		// Create a table to layout the form options
		layout.setCellSpacing(3);
		
		final Button removeButton = new Button("Remove");
		removeButton.addStyleName("right");
		final Button editButton = new Button("Edit");
		editButton.addStyleName("right");
		
		layout.setWidget(0, 2, removeButton);
		layout.setWidget(0, 1, editButton);
		layout.setWidget(1, 0, img);
		
		// Add advanced options to form in a disclosure panel
		final DisclosurePanel advancedDisclosure = new DisclosurePanel("Alarms and Values");
		advancedDisclosure.setAnimationEnabled(true);
		advancedDisclosure.ensureDebugId("debugId");
		advancedDisclosure.setContent(alarmPanel);
		
		final VerticalPanel innerPanel = new VerticalPanel();
		innerPanel.add(layout);
		innerPanel.add(advancedDisclosure);
		
		// Wrap the contents in a DecoratorPanel
		final DecoratorPanel decPanel = new DecoratorPanel();
		decPanel.setWidget(innerPanel);
		
		nodePanel.add(decPanel);
		
		/********************
		 * BUTTON Handlers  *
		 ********************/
		
        okButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
            	String ipAdress = ip.getText();
            	String[] ips = ipAdress.split("\\.");
            	if(ips.length != 4){
            		Window.alert("Wrong IP format!");
            		return;
            	} else {
            		for(int i = 0; i < ips.length; i++){
            			try {
            				int h = Integer.parseInt(ips[i]);
            			} catch (NumberFormatException e){
            				Window.alert("Wrong IP format!");
            				return;
            			}
            		}
            	}
            		addNode();
            }
        });
        
        cancelButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
				if(!created) {
					greetingService.deleteNodeObj(Integer.toString(nodeId), new AsyncCallback<Void>() {
	
						@Override
						public void onFailure(Throwable caught) {
							RootPanel.get("rpcLoad").setVisible(false);
			            	RootPanel.get("rpcError").setVisible(true);
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
            	nodePanel.removeFromParent();
            	try {
					finalize();
				} catch (Throwable e) {
					e.printStackTrace();
				}
				greetingService.deleteNodeObj(Integer.toString(nodeId), new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						RootPanel.get("rpcLoad").setVisible(false);
		            	RootPanel.get("rpcError").setVisible(true);
					}

					@Override
					public void onSuccess(Void result) {
					}
				});
            }
        });
        
	}
	
	public void addNode() {
		if(getNumberOfDevices().getValue().isEmpty()) {
        	Window.alert("Please enter number of devices!");
        } else {
        	img.setUrl("img/" + this.image);
        	layout.setHTML(0, 0, "<b>" + getNodeTypeName() + " - " + getNodeId() + "</b>");
        	
        	setCreated(true);
            dialogBox.hide();
            alarmPanel.clear();
            int size = alarmList.size();
            for(int i=0; i<size; i++) {
            	if(alarmList.get(i).getValue()) {
            		alarmPanel.add(new HTML(" -> " + alarmList.get(i).getHTML()));
            	}
            }
            for(int i=0; i<getPropertyList().size(); i++) {
            	alarmPanel.add(new HTML(getPropertyList().get(i).getTitle() + ": " + getPropertyList().get(i).getValue()));
            }
            nodePanel.setStyleName("left");
            RootPanel.get("networkTopology").add(nodePanel);
        }
	}
	
	public void createDialogBox() {
		// Create dialogBox and set properties
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
		
		final VerticalPanel mainContainer = new VerticalPanel();
		final VerticalPanel leftContainer = new VerticalPanel();
		final VerticalPanel rightContainer = new VerticalPanel();
		final VerticalPanel propertiesColoumn[] = new VerticalPanel[2];
		final VerticalPanel alarmsColoumn[] = new VerticalPanel[4];	
		final VerticalPanel fieldsColoumn[] = new VerticalPanel[3];
		
		mainContainer.setStyleName("pop-up");
		
		final HorizontalPanel mainContainerRow = new HorizontalPanel();
		final HorizontalPanel propertiesRow = new HorizontalPanel();
		final HorizontalPanel alarmsRow = new HorizontalPanel();
		final HorizontalPanel fieldsRow = new HorizontalPanel();
		final HorizontalPanel buttons = new HorizontalPanel();
		
		mainContainerRow.setSpacing(6);
		alarmsRow.setSpacing(6);
		fieldsRow.setSpacing(6);
		buttons.setSpacing(6);
		
		/********************
		 * LEFT CONTAINER   *
		 ********************/
		
		for(int i=0; i<2; i++) {
			propertiesColoumn[i] = new VerticalPanel();
			propertiesRow.add(propertiesColoumn[i]);
		}
		propertiesRow.setSpacing(6);
		
		// NUMBER_OF_DEVICES, IP, MAC
		HTML label = new HTML("Properties of Device");
		label.addStyleName("label-DialogBox");
		
		leftContainer.insert(label, 0);
		leftContainer.add(propertiesRow);
		
		label = new HTML("<b>Number Of Devices:</b>");
		propertiesColoumn[0].add(label);
		propertiesColoumn[1].add(numberOfDevices);
		
		label = new HTML("<b>IP:</b>");
		propertiesColoumn[0].add(label);
		propertiesColoumn[1].add(ip);
		
		// ALARMS
		label = new HTML("Alarms of device");
		label.addStyleName("label-DialogBox");
		
		leftContainer.add(label);
		leftContainer.add(alarmsRow);
		
		for(int i=0; i<4; i++) {
			alarmsColoumn[i] = new VerticalPanel();
			alarmsRow.add(alarmsColoumn[i]);
		}
		
		// Add alarms
		int size = getAlarmList().size();
		for(int i=0 ; i<size; i++) {
			alarmsColoumn[0].add(getAlarmList().get(i));
			alarmsColoumn[1].add(getProbList().get(i));
			label = new HTML("times in a");
			alarmsColoumn[2].add(label);
			alarmsColoumn[3].add(getFrequencyList().get(i));
		}
		
		/********************
		 * RIGHT CONTAINER  *
		 ********************/
		
		// REQUIRED FIELDS
        label = new HTML("Required Fields");
        label.addStyleName("label-DialogBox");
        
        rightContainer.insert(label, 0);
        rightContainer.add(fieldsRow);
        
		for(int i=0; i<3; i++) {
			fieldsColoumn[i] = new VerticalPanel();
			fieldsRow.add(fieldsColoumn[i]);
		}
        
		// Add required fields
		int objectBoundary = getPropertyList().size();
		int max = objectBoundary/3+1;
		//System.out.println("max: " + max + " size: " + objectBoundary);
		for(int i = 0, panelIndex = 0; i < objectBoundary;) {
			String property = getPropertyList().get(i).getElement().getTitle();
	    	label = new HTML("<b>"+ property +":</b>");
	        fieldsColoumn[panelIndex].add(label);
	        fieldsColoumn[panelIndex].add(getPropertyList().get(i));
	        i++;
	        if(i%max==0) panelIndex++;
	    }
		
        // Create Buttons and set properties
        okButton.getElement().setId("okButton");
        cancelButton.getElement().setId("cancelButton");
        okButton.addStyleName("left");
        cancelButton.addStyleName("left");

        buttons.add(okButton);
        buttons.add(cancelButton);
        
        // Unify all panels and buttons
        mainContainerRow.add(leftContainer);
        mainContainerRow.add(rightContainer);
        mainContainer.add(mainContainerRow);
        mainContainer.add(buttons);
        dialogBox.setWidget(mainContainer);
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
	
	public ArrayList<CheckBox> getAlarmList() {
		return alarmList;
	}

	public ArrayList<TextBox> getProbList() {
		return probList;
	}

	public ArrayList<ListBox> getFrequencyList() {
		return frequencyList;
	}
	
	public TextBox getNumberOfDevices() {
		return numberOfDevices;
	}

	public TextBox getIp() {
		return ip;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getImage() {
		return image;
	}

}
