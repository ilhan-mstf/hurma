package com.cennetelmasi.hurma.client;

import java.util.Vector;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Node implements EntryPoint {

	private int nodeId;
	
	Node(int t) {
		nodeId = t;
	}

	@Override
	public void onModuleLoad() {
		
		final DialogBox dialogBox = new DialogBox();
        dialogBox.setText("Remote Procedure Call");
        dialogBox.setAnimationEnabled(true);
        final Button closeButton = new Button("Close");
        closeButton.getElement().setId("closeButton");
        
        // Create a vertical panel to align the check boxes
        VerticalPanel dialogVPanel = new VerticalPanel();
        HTML label = new HTML("<b>Avaliable alarms for this device</b>");
        dialogVPanel.add(label);

        // Add a checkbox of alarm
        final Vector<CheckBox> hede = new Vector<CheckBox>();
        String[] alarms = {"Alarm 1", "Alarm 2", "Alarm 3", "Alarm 4", "Alarm 5"}; 
        for (int i = 0; i < alarms.length; i++) {
          String alarm = alarms[i];
          CheckBox checkBox = new CheckBox(alarm);
          checkBox.ensureDebugId("alarm" + i);
          dialogVPanel.add(checkBox);
          hede.add(checkBox);
        }
        
        dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
        dialogVPanel.add(closeButton);
        dialogBox.setWidget(dialogVPanel);
        dialogBox.center();
        
		// Add the disclosure panels to a panel
		final VerticalPanel vPanel = new VerticalPanel();
		vPanel.setSpacing(3);
		
		// Create a table to layout the form options
		final FlexTable layout = new FlexTable();
		layout.setCellSpacing(3);
		layout.setWidth("185px");
		
		final Button removeButton = new Button("Remove");
		
		// Add a title to the form
		layout.setHTML(0, 0, "Device " + nodeId);
		layout.setWidget(0, 1, removeButton);
		
		final VerticalPanel he = new VerticalPanel();
		he.add(layout);
		
		// Wrap the contents in a DecoratorPanel
		final DecoratorPanel decPanel = new DecoratorPanel();
		decPanel.setWidget(he);
		
		vPanel.add(decPanel);
		
        closeButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                dialogBox.hide();
                for(int i=0; i<hede.size(); i++) {
                	if(hede.get(i).getValue()) {
                		he.add(new HTML(hede.get(i).getHTML()));
                	}
                }
                RootPanel.get("networkTopology").add(vPanel);
            }
        });
        
        removeButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
            	vPanel.removeFromParent();
            	try {
					finalize();
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
        
	}

}

