package com.cennetelmasi.hurma.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class NewDevice {
	public void onModuleLoad() {
		DialogBox box = new DialogBox();
		VerticalPanel vp = new VerticalPanel();
		
		final FormPanel form = new FormPanel();
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);
		form.setAction("upload");
		// create a panel to hold the upload widget and submit button
		VerticalPanel panel = new VerticalPanel();
		form.setWidget(panel);
		FileUpload upload = new FileUpload();
		panel.add(upload);
		Button button = new Button("Submit");
		button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				form.submit();
			}
		});
		panel.add(button); 
		box.setWidget(vp);
		vp.add(form);
		box.center();
	}
}
