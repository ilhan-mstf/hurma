package com.cennetelmasi.hurma.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GreetingServiceAsync {
    void greetServer(String input, String pass, AsyncCallback<String> callback);
    //void nodeTypeName(int index, AsyncCallback<String> asyncCallback);
    //void nodeTypeID(int index, AsyncCallback<String> asyncCallback);
    //void nodeTypeMIB(int index, AsyncCallback<String> asyncCallback);
    void nodeTypeNumber(AsyncCallback<String> asyncCallback);
    void getNodeTypeValues(int index, AsyncCallback<String[]> callback);
	void getAlarmListName(String mib, AsyncCallback<ArrayList<String>> callback);
	void getObjectList(String mib, AsyncCallback<ArrayList<String>> callback);
}
