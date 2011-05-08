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
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Node implements EntryPoint {

	private int nodeId;
	private String mib;
	private VerticalPanel dialogVPanel;
	private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);	
	Node(int t, String mib) {
		this.nodeId = t;
		this.mib = mib;
	}

	@Override
	public void onModuleLoad() {
		final DialogBox dialogBox = new DialogBox();
        dialogBox.setText("Select Alarms");
        dialogBox.setAnimationEnabled(true);
        final Button closeButton = new Button("Select");
        final Button cancelButton = new Button("Cancel");
        closeButton.getElement().setId("closeButton");
        cancelButton.getElement().setId("cancelButton");
        
        // Create a vertical panel to align the check boxes
        dialogVPanel = new VerticalPanel();
        
        HTML label = new HTML("<b>Avaliable alarms for this device</b>");
        dialogVPanel.add(label);
        
		// Add a checkbox of alarm
        dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_LEFT);
        final Vector<CheckBox> checkBoxList = new Vector<CheckBox>();
        final Vector<TextBox> propertyList = new Vector<TextBox>();
        greetingService.getAlarmListName(mib, new AsyncCallback<ArrayList<String>>() {
            public void onFailure(Throwable caught) {
                // Show the RPC error message to the user
            }

			@Override
			public void onSuccess(ArrayList<String> alarms) {
				for (int i = 0; i < alarms.size(); i++) {
			    	String alarm = alarms.get(i);
			        CheckBox checkBox = new CheckBox(alarm);
			        i++;
			        checkBox.getElement().setId(alarms.get(i));
			        checkBox.ensureDebugId(alarms.get(i));
			        dialogVPanel.add(checkBox);
			        checkBoxList.add(checkBox);
			    }
				greetingService.getObjectList(mib, new AsyncCallback<ArrayList<String>>() {
		            public void onFailure(Throwable caught) {
		                // Show the RPC error message to the user
		            }
					@Override
					public void onSuccess(ArrayList<String> objects) {
						for (int i = 0; i < objects.size(); i++) {
					    	String property = objects.get(i);
					    	HTML label = new HTML("<b>"+ property +"</b>");
					        dialogVPanel.add(label);
					        TextBox field = new TextBox();
					        field.getElement().setTitle(property);
					        propertyList.add(field);
					        dialogVPanel.add(field);
					        //dialogVPanel.add("<br />");
					    }
					}
		        });
			}
        });
        /**/
        
        dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
        dialogVPanel.add(closeButton);
        dialogBox.setWidget(dialogVPanel);
        dialogBox.center();
        
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
