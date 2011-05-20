package com.cennetelmasi.hurma.client;

import java.util.ArrayList;
import java.util.Vector;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
	private int nodeId;
	private String mib;
	private int numberOfDevice;
	
	public Node(int t, String mib) {
		this.nodeId = t;
		this.mib = mib;
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
         * -----------------------------------------------------------------*
         * H Panel-0														*
         * -----------------------------------------------------------------*
         * V Panel-1		| V Panel-2										*
         * -Alarms...		| H Panel-1										*
         * -				| ----------------------------------------------*
         * -				| V Panel-3 	| V Panel-4 	| V Panel-5		*
         * -				|				|				|				*
         * -				|				|				|				*
         * -----------------------------------------------------------------*
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
        
        // Create list of alarms and properties
        final Vector<CheckBox> checkBoxList = new Vector<CheckBox>();
        final Vector<TextBox> propertyList = new Vector<TextBox>();
        
        HTML label = new HTML("<b>Alarms of device</b><br />");
        label.addStyleName("label-DialogBox");
        dialogVPanel[1].add(label);
		// Add check-boxes of alarms
        // Make server call
        greetingService.getAlarmListName(mib, new AsyncCallback<ArrayList<String>>() {
            public void onFailure(Throwable caught) {
            	RootPanel.get("rpcLoad").setVisible(false);
            	RootPanel.get("rpcError").setVisible(true);
            }

			@Override
			public void onSuccess(ArrayList<String> alarms) {
				int size = alarms.size();
				for(int i = 0; i < size; i++) {
			    	String alarm = alarms.get(i);
			        CheckBox checkBox = new CheckBox(alarm);
			        i++;
			        checkBox.getElement().setId(alarms.get(i));
			        checkBox.ensureDebugId(alarms.get(i));
			        dialogVPanel[1].add(checkBox);
			        checkBoxList.add(checkBox);
			    }
				greetingService.getObjectList(mib, new AsyncCallback<ArrayList<String>>() {
		            public void onFailure(Throwable caught) {
		            	RootPanel.get("rpcLoad").setVisible(false);
		            	RootPanel.get("rpcError").setVisible(true);
		            }
					@Override
					public void onSuccess(ArrayList<String> objects) {
				        HTML label = new HTML("<b>Required Fields</b>");
				        label.addStyleName("label-DialogBox");
				        dialogVPanel[2].insert(label, 0);
						int size = objects.size();
						int max = size/4-1;
						for(int i = 0, j = 3; i < size; i++) {
							String oid = objects.get(i++);
					    	String property = objects.get(i);
					    	label = new HTML("<b>"+ property +":</b>");
					        dialogVPanel[j].add(label);
					        TextBox field = new TextBox();
					        field.getElement().setTitle(property);
					        field.getElement().setId(oid);
					        propertyList.add(field);
					        dialogVPanel[j].add(field);
					        if(i/2%max==(max-1)) j++;
					    }
		            	RootPanel.get("rpcLoad").setVisible(false);
						dialogBox.center();
					}
		        });
			}
        });
        
        // Create Buttons and set properties
        final Button closeButton = new Button("OK");
        final Button cancelButton = new Button("Cancel");
        closeButton.getElement().setId("closeButton");
        cancelButton.getElement().setId("cancelButton");
        closeButton.addStyleName("left");
        cancelButton.addStyleName("left");

        // Unify all panels and buttons
        dialogHPanel[2].add(closeButton);
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
		layout.setWidth("185px");
		
		final Button removeButton = new Button("Remove");
		removeButton.addStyleName("right");
		
		final Image img = new Image("img/" + nodeId + ".jpg");
		
		// Add a title to the form
		layout.setHTML(0, 0, "<b>Device " + nodeId + "</b>");
		layout.setWidget(0, 1, removeButton);
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
		
        closeButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                dialogBox.hide();
                for(int i=0; i<checkBoxList.size(); i++) {
                	if(checkBoxList.get(i).getValue()) {
                		alarmPanel.add(new HTML(" -> " + checkBoxList.get(i).getHTML()));
                	}
                }
                for(int i=0; i<propertyList.size(); i++){
                	alarmPanel.add(new HTML(propertyList.get(i).getTitle() + ": " + propertyList.get(i).getValue()));
                }
                vPanel.setTitle("Device " + nodeId);
                vPanel.setStyleName("left");
                RootPanel.get("networkTopology").add(vPanel);
            }
        });
        
        cancelButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
			}
		});
        
        removeButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
            	vPanel.removeFromParent();
            	try {
					finalize();
				} catch (Throwable e) {
					e.printStackTrace();
				}
            }
        });
        
	}

}
