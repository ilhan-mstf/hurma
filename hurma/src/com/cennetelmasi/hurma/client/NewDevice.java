package com.cennetelmasi.hurma.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;

public class NewDevice {
	public void onModuleLoad() {
		final DialogBox box = new DialogBox();
		box.setText("New Device");
		box.setGlassEnabled(true);
        box.setAnimationEnabled(true);
		
        VerticalPanel vp = new VerticalPanel();
        HorizontalPanel panel = new HorizontalPanel();
        HorizontalPanel buttonPanel = new HorizontalPanel();
        final VerticalPanel[] dialogHPanel = new VerticalPanel[3];
		for(int i=0; i<3; i++) {
			dialogHPanel[i] = new VerticalPanel();
			dialogHPanel[i].setSpacing(5);
		}
		
		panel.add(dialogHPanel[0]);
		panel.add(dialogHPanel[1]);
		panel.add(dialogHPanel[2]);
		
		final FormPanel form = new FormPanel();
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);
		form.setAction(GWT.getModuleBaseURL() + "myFormHandler");
		// create a panel to hold the upload widget and submit button
		
		HTML label1 = new HTML("<b> Node Name </b><br />");
        label1.addStyleName("label-DialogBox");
        dialogHPanel[0].add(label1);
        HTML label2 = new HTML("<b> MIB File </b><br />");
        label2.addStyleName("label-DialogBox");
        dialogHPanel[1].add(label2);
        HTML label3 = new HTML("<b> Node Icon </b><br />");
        label3.addStyleName("label-DialogBox");
        dialogHPanel[2].add(label3);
        
		form.setWidget(panel);
		
		final Image[] images = new Image[4];
		images[0] = new Image("img/0.jpg");
		images[1] = new Image("img/1.jpg");
		images[2] = new Image("img/2.jpg");
		images[3] = new Image("img/4.jpg");
		
		final TextBox field = new TextBox();
		field.setName("deviceName");
		dialogHPanel[0].add(field);
		
		final FileUpload upload = new FileUpload();
		upload.setName("uploadFormElement"); 
		
		final ListBox listBox = new ListBox();
		listBox.setName("icon");
		listBox.addItem("Telephone", "0.jpg");
		listBox.addItem("Mobile Phone", "1.jpg");
		listBox.addItem("Laptop", "2.jpg");
		listBox.addItem("Scanner", "3.jpg");
		dialogHPanel[2].add(listBox);
		dialogHPanel[2].add(images[0]);
		listBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				// TODO Auto-generated method stub
				int i = listBox.getSelectedIndex();
				for(int j=0; j<listBox.getItemCount(); j++){
					dialogHPanel[2].remove(images[j]);
				}
				dialogHPanel[2].add(images[i]);
			}
		});
		form.addSubmitHandler(new SubmitHandler() {
			@Override
			public void onSubmit(SubmitEvent event) {
				if(upload.getFilename().isEmpty() && field.getValue().isEmpty()){
					Window.alert("Please enter a name and select a MIB file!");
					event.cancel();
				} else if(upload.getFilename().isEmpty()){
					Window.alert("Please select a MIB file!");
					event.cancel();
				} else if(field.getValue().isEmpty()){
					Window.alert("Please enter a name!");
					event.cancel();
				}
			}
		});
		
		form.addSubmitCompleteHandler(new SubmitCompleteHandler() {
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				// TODO Auto-generated method stub
				box.hide(true);
				Configuration.refreshTheNodeList();
			}
		});
		
		dialogHPanel[1].add(upload);
		Button button = new Button("Submit");
		button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				field.setName(field.getText());
				listBox.setName(listBox.getValue(listBox.getSelectedIndex()));
				form.submit();
			}
		});
		Button closeButton = new Button("Cancel");
		closeButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				box.hide(true);
			}
		});
		closeButton.addStyleName("left");
        button.addStyleName("left");
		buttonPanel.add(button);
		buttonPanel.add(closeButton);
		
		box.setWidget(vp);
		vp.add(form);
		vp.add(buttonPanel);
		box.center();
	}
}
