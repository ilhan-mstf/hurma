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
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sun.org.apache.bcel.internal.generic.NEW;

public class Node implements EntryPoint {

	private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);
	private int nodeId;
	private String mib;
	
	private VerticalPanel[] dialogVPanel = new VerticalPanel[4];
	private HorizontalPanel dialogHPanel = new HorizontalPanel();
	
	Node(int t, String mib) {
		this.nodeId = t;
		this.mib = mib;
	}
	
	/**
	 * Firstly it creates a pop dialog box
	 * shows alarms and required fields and
	 * then it creates the node on the screen.
	 */

	@Override
	public void onModuleLoad() {
		// create dialogBox
		final DialogBox dialogBox = new DialogBox();
        dialogBox.setText("Select alarms and fill required fields.");
        dialogBox.setAnimationEnabled(true);
        
        // create Buttons
        final Button closeButton = new Button("OK");
        final Button cancelButton = new Button("Cancel");
        closeButton.getElement().setId("closeButton");
        cancelButton.getElement().setId("cancelButton");
        
        /**
         * Create four vertical panel
         * one for alarms and the other
         * for required fields.
         * 
         * Later add these vertical panels to the
         * horizontal panel and 
         */
        
        for(int i=0; i<4; i++)
        	dialogVPanel[i] = new VerticalPanel();
        
        // first vertical panel
        HTML label = new HTML("<b>Avaliable alarms for this device</b>");
        dialogVPanel[0].add(label);
        
        // Create list of alarms and properties
        final Vector<CheckBox> checkBoxList = new Vector<CheckBox>();
        final Vector<TextBox> propertyList = new Vector<TextBox>();
        
		// Add a checkbox of alarm
        // Make server call
        greetingService.getAlarmListName(mib, new AsyncCallback<ArrayList<String>>() {
            public void onFailure(Throwable caught) {
                // TODO Show the RPC error message to the user
            }

			@Override
			public void onSuccess(ArrayList<String> alarms) {
				int size = alarms.size();
				for (int i = 0; i < size; i++) {
			    	String alarm = alarms.get(i);
			        CheckBox checkBox = new CheckBox(alarm);
			        i++;
			        checkBox.getElement().setId(alarms.get(i));
			        checkBox.ensureDebugId(alarms.get(i));
			        dialogVPanel[0].add(checkBox);
			        checkBoxList.add(checkBox);
			    }
				greetingService.getObjectList(mib, new AsyncCallback<ArrayList<String>>() {
		            public void onFailure(Throwable caught) {
		                // Show the RPC error message to the user
		            }
					@Override
					public void onSuccess(ArrayList<String> objects) {
						HTML label = new HTML("<b>Required Fields</b>");
						dialogVPanel[1].add(label);
						int size = objects.size();
						int max = size/4+1;
						for (int i = 0, j = 1; i < size; i++) {
					    	String property = objects.get(i);
					    	label = new HTML("<b>"+ property +"</b>");
					        dialogVPanel[j].add(label);
					        TextBox field = new TextBox();
					        field.getElement().setTitle(property);
					        propertyList.add(field);
					        dialogVPanel[j].add(field);
					        //dialogVPanel.add("<br />");
					        // TODO there will be bug here...
					        if(i%5==0) j++;
					    }
					}
		        });
			}
        });
        
        // Add all panels
        dialogVPanel[3].add(closeButton);
        dialogVPanel[3].add(cancelButton);
        for(int i=0; i<4; i++)
        	dialogHPanel.add(dialogVPanel[i]);
        dialogBox.setWidget(dialogHPanel);
        dialogBox.center();
        
        /**
         * Node starts here...
         */
        
		// Add the disclosure panels to a panel
		final VerticalPanel vPanel = new VerticalPanel();
		vPanel.setSpacing(8);
		
		// Create a table to layout the form options
		final FlexTable layout = new FlexTable();
		layout.setCellSpacing(3);
		layout.setWidth("185px");
		
		final Button removeButton = new Button("Remove");
		removeButton.setStyleName("right");
		
		final Image img = new Image("img/" + nodeId + ".jpg");
		
		// Add a title to the form
		layout.setHTML(0, 0, "<b>Device " + nodeId + "</b>");
		layout.setWidget(0, 1, removeButton);
		layout.setWidget(1, 0, img);
		
		final VerticalPanel innerPanel = new VerticalPanel();
		innerPanel.add(layout);
		
		// Wrap the contents in a DecoratorPanel
		final DecoratorPanel decPanel = new DecoratorPanel();
		decPanel.setWidget(innerPanel);
		
		vPanel.add(decPanel);
		
        closeButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                dialogBox.hide();
                for(int i=0; i<checkBoxList.size(); i++) {
                	if(checkBoxList.get(i).getValue()) {
                		innerPanel.add(new HTML(" > " + checkBoxList.get(i).getHTML()));
                	}
                }
                for(int i=0; i<propertyList.size(); i++){
                	innerPanel.add(new HTML(propertyList.get(i).getTitle() + ": " + propertyList.get(i).getValue()));
                }
                vPanel.setTitle("Device " + nodeId);
                vPanel.setStyleName("left");
                RootPanel.get("networkTopology").add(vPanel);
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
