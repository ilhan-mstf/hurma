package com.cennetelmasi.hurma.client;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.xml.transform.TransformerException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface GreetingService extends RemoteService {
        String greetServer(String name, String pass);
		ArrayList<String> getNodeObjValues(String mib);
		void setNodeObjValues(ArrayList<String> values,
				ArrayList<String> selectedAlarms,
				ArrayList<String> requiredFields);
		ArrayList<String> getNodeTypes();
		void deleteNodeObj(String id);
		void startSimulation(int time);
		String getOutputs();
		String pause();
		void resume();
		String stop();
		boolean sessionControl();
		void createSession();
		void destroySession();
		void saveSimulation(ArrayList<String> values);
		ArrayList<String> getSavedSimulationName();
		ArrayList<String> loadSimulation(String simulationName);
		
//        String[] getNodeTypeValues(int index);
//        String sendSimulationConfiguration(String[] values, String[] alarms, String[] fields);
//        String nodeTypeName(int index);
//        String nodeTypeID(int index);
//        String nodeTypeMIB(int index);
//        String nodeTypeNumber();
//        ArrayList<String> getAlarmListName(String mib);
//        ArrayList<String> getObjectList(String mib);

}
