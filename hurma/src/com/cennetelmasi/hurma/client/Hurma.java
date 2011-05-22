package com.cennetelmasi.hurma.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Hurma implements EntryPoint {
	
    private static final String SERVER_ERROR = "An error occurred while "
                    + "attempting to contact the server. Please check your network "
                    + "connection and try again.";

    private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);
    
    private Simulation simulation;
    
    public Hurma() {
    	simulation = new Simulation();
    }

    public void onModuleLoad(){
    	
    	greetingService.sessionControl(new AsyncCallback<Boolean>() {
			
			@Override
			public void onSuccess(Boolean result) {
				// TODO Auto-generated method stub
				if(result){
					//session already defined
					//RootPanel.get("loginPage").setVisible(false);
					Configuration config = new Configuration(simulation);
                    config.onModuleLoad();
				} else {
					final Button sendButton = new Button("Login");
			        final TextBox nameField = new TextBox();
			        final PasswordTextBox passField = new PasswordTextBox();
			        nameField.setText("Hurma");
			        passField.setText("hurma");

			        sendButton.addStyleName("sendButton");
			        
			        RootPanel.get("nameFieldContainer").add(nameField);
			        RootPanel.get("passFieldContainer").add(passField);
			        RootPanel.get("sendButtonContainer").add(sendButton);
			        
			        nameField.setFocus(true);
			        nameField.selectAll();

			        final DialogBox dialogBox = new DialogBox();
			        dialogBox.setText("Remote Procedure Call");
			        dialogBox.setAnimationEnabled(true);
			        final Button closeButton = new Button("Close");
			        closeButton.getElement().setId("closeButton");
			        final Label textToServerLabel = new Label();
			        final HTML serverResponseLabel = new HTML();
			        
			        closeButton.addClickHandler(new ClickHandler() {
			            public void onClick(ClickEvent event) {
			                dialogBox.hide();
			                sendButton.setEnabled(true);
			                sendButton.setFocus(true);
			            }
			        });

			        // Create a handler for the sendButton and nameField
			        class MyHandler implements ClickHandler, KeyUpHandler {
			            public void onClick(ClickEvent event) {
			            	sendNameToServer();
			            }

			            public void onKeyUp(KeyUpEvent event) {
				    		System.out.println("key pressed");
				            if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
				            	sendNameToServer();
				            }
			            }

			            private void sendNameToServer(){
			                sendButton.setEnabled(false);
			                String textToServer = nameField.getText();
			                String passToServer = passField.getText();
			                textToServerLabel.setText(textToServer);
			                serverResponseLabel.setText("");
			                greetingService.greetServer(textToServer, passToServer,
			                    new AsyncCallback<String>() {
			                        public void onFailure(Throwable caught) {
			                            // Show the RPC error message to the user
			                            dialogBox.setText("Remote Procedure Call - Failure");
			                            serverResponseLabel.addStyleName("serverResponseLabelError");
			                            serverResponseLabel.setHTML(SERVER_ERROR);
			                            dialogBox.center();
			                            closeButton.setFocus(true);
			                        }

			                        public void onSuccess(String result) {
			                            if(result.equals("true")) {
			                            	greetingService.createSession(new AsyncCallback<Void>() {
												
												@Override
												public void onSuccess(Void result) {
													// TODO Auto-generated method stub
					                            	RootPanel.get("loginPage").setVisible(false);
					                            	sendButton.setEnabled(true);
					                                Configuration config = new Configuration(simulation);
					                                config.onModuleLoad();
													
												}
												
												@Override
												public void onFailure(Throwable caught) {
													// TODO Auto-generated method stub
													
												}
											});
			                            } else {
			                                dialogBox.setText("Login Failure");
			                                serverResponseLabel.removeStyleName("serverResponseLabelError");
			                                serverResponseLabel.setHTML(result);
			                                
			                                final VerticalPanel dialogVPanel = new VerticalPanel();
			                                dialogVPanel.add(new HTML("<b>Incorrect username and password!</b>"));
			                                dialogVPanel.addStyleName("dialogVPanel");
			                                
			                                dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
			                                dialogVPanel.add(closeButton);
			                                dialogBox.setWidget(dialogVPanel);
			                                dialogBox.center();
			                            }
			                            closeButton.setFocus(true);
			                        }
			                	});
			            }
			        }
			        MyHandler handler = new MyHandler();
			        sendButton.addClickHandler(handler);
			        nameField.addKeyUpHandler(handler);
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}
		});
    }
}