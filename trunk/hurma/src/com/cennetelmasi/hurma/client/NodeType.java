package com.cennetelmasi.hurma.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class NodeType implements EntryPoint {

	private int nodeId;
	
	NodeType(int t) {
		nodeId = t;
	}

	@Override
	public void onModuleLoad() {
		
		// Add the disclosure panels to a panel
		VerticalPanel vPanel = new VerticalPanel();
		vPanel.setSpacing(3);
		
		// Create a table to layout the form options
		FlexTable layout = new FlexTable();
		layout.setCellSpacing(3);
		layout.setWidth("185px");
		
		final Button addButton = new Button("Add");
		
		// Add a title to the form
		layout.setHTML(0, 0, "Device " + nodeId);
		layout.setWidget(0, 1, addButton);
		
		FlexTable advancedLayout = new FlexTable();
		advancedLayout.setCellSpacing(3);
		advancedLayout.setWidth("100px");
		advancedLayout.setHTML(0, 0, "more info...");

		// Add advanced options to form in a disclosure panel
		DisclosurePanel advancedDisclosure = new DisclosurePanel("More Info");
		advancedDisclosure.setAnimationEnabled(true);
		advancedDisclosure.ensureDebugId("debugId");
		advancedDisclosure.setContent(advancedLayout);
		layout.setWidget(1, 0, advancedDisclosure);
		
		// Wrap the contents in a DecoratorPanel
		DecoratorPanel decPanel = new DecoratorPanel();
		decPanel.setWidget(layout);
		
		vPanel.add(decPanel);
		RootPanel.get("node").add(vPanel);
		
        addButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
            	Node n = new Node(nodeId);
            	n.onModuleLoad();
            }
        });

	}

}
