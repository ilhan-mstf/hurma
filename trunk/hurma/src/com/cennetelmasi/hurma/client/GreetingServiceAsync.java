package com.cennetelmasi.hurma.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GreetingServiceAsync {
    void greetServer(String input, String pass, AsyncCallback<String> callback);
    void getNodeTypes(AsyncCallback<ArrayList<String>> callback);
    void getNodeObjValues(String mib, AsyncCallback<ArrayList<String>> callback);
    void setNodeObjValues(ArrayList<String> values,
			ArrayList<String> selectedAlarms, ArrayList<String> requiredFields,
			AsyncCallback<Void> callback);
	void deleteNodeObj(String id, AsyncCallback<Void> callback);
	void startSimulation(int time, AsyncCallback<Void> callback);
	void getOutputs(AsyncCallback<String> callback);
	void pause(AsyncCallback<String> callback);
	void resume(AsyncCallback<Void> callback);
	void stop(AsyncCallback<String> callback);
    
}