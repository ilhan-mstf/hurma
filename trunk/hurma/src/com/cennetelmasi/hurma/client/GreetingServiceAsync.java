package com.cennetelmasi.hurma.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GreetingServiceAsync {
    void greetServer(String input, String pass, AsyncCallback<String> callback);
    void getNodeTypes(AsyncCallback<ArrayList<String>> callback);
    void setNodeObjValues(ArrayList<String> values,
			ArrayList<String> selectedAlarms, ArrayList<String> requiredFields,
			AsyncCallback<Void> callback);
	void deleteNodeObj(String id, AsyncCallback<Void> callback);
	void startSimulation(int time, int cofactor, AsyncCallback<Void> callback);
	void getOutputs(AsyncCallback<String> callback);
	void pause(AsyncCallback<String> callback);
	void resume(AsyncCallback<Void> callback);
	void stop(AsyncCallback<String> callback);
	void destroySession(AsyncCallback<Void> callback);
	void sessionControl(AsyncCallback<Boolean> callback);
	void createSession(AsyncCallback<Void> callback);
	void saveSimulation(ArrayList<String> values, AsyncCallback<Void> callback);
	void getSavedSimulationName(AsyncCallback<ArrayList<String>> callback);
	void loadSimulation(String simulationName,
			AsyncCallback<ArrayList<String>> callback);
	void clear(AsyncCallback<Void> callback);
	void createNode(String mib, AsyncCallback<ArrayList<String>> callback);
	void getNodeObjValuesById(String id,
			AsyncCallback<ArrayList<String>> callback);
	void getSimulationState(AsyncCallback<String> callback);
	void getPassedTime(AsyncCallback<Integer> callback);
}
