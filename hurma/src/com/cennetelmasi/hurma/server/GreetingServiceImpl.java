package com.cennetelmasi.hurma.server;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import net.percederberg.mibble.MibLoaderException;
import net.percederberg.mibble.MibValue;

import com.cennetelmasi.hurma.client.GreetingService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 * @param <NodeObject>
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl<NodeObject> extends RemoteServiceServlet implements
                GreetingService {

    public String greetServer(String input, String pass) {
    	input = escapeHtml(input);
        if(input.equals("Hurma") && pass.equals("hurma"))
        	return "true";
        else
        	return "false";
    }
    
    private String escapeHtml(String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;");
	}

	@Override
	public String nodeTypeNumber() {
		File file = new File("nodeTypes.xml");
		NodeTypeParser spe = new NodeTypeParser();
		spe.parseDocument(file);
		return Integer.toString(NodeTypeParser.nodeTypes.size());
	}
	
	public String[] getNodeTypeValues(int index) {
		File file = new File("nodeTypes.xml");
		NodeTypeParser ntp = new NodeTypeParser();
		ntp.parseDocument(file);
		String[] values = new String[3];
		int i=0;
		values[i++] = Integer.toString(NodeTypeParser.nodeTypes.get(index).getId());
		values[i++] = NodeTypeParser.nodeTypes.get(index).getName();
		values[i++] = NodeTypeParser.nodeTypes.get(index).getMIB();
		return values;
	}

	public ArrayList<Alarm> getAlarmList(String mib) {
		NodeObj node = null;
		try {
			node = new NodeObj(mib);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (MibLoaderException e) {
			e.printStackTrace();
		}
		return node.getAlarms();
	}

	@Override
	public ArrayList<String> getAlarmListName(String mib) {
		ArrayList<String> list = new ArrayList<String>();
		ArrayList<Alarm> alarms = new ArrayList<Alarm>();
		NodeObj node = null;
		try {
			node = new NodeObj(mib);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (MibLoaderException e) {
			e.printStackTrace();
		}
		alarms = node.getAlarms();
		
		for(int i = 0; i < alarms.size(); i++){
			list.add(alarms.get(i).getName());
			list.add(alarms.get(i).getOid().toString());
		}
		return list;
	}

	@Override
	public ArrayList<String> getObjectList(String mib) {
		ArrayList<String> list = new ArrayList<String>();
		
		ArrayList<Alarm> alarms = new ArrayList<Alarm>();
		NodeObj node = null;
		try {
			node = new NodeObj(mib);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (MibLoaderException e) {
			e.printStackTrace();
		}
		alarms = node.getAlarms();
		for(int i=0; i < alarms.size(); i++){
			Alarm alarm = new Alarm();
			alarm = alarms.get(i);
			ArrayList<Object> requiredObjects = new ArrayList<Object>();
			requiredObjects = alarm.getRequiredObjects();
			for(int j = 0; j < requiredObjects.size(); j++){
				MIBObject obj = new MIBObject();
				obj = node.getMibObjectByOid((MibValue) requiredObjects.get(j));
				if(!list.contains(obj.getName())){
					list.add(obj.getName());
				}
			}
		}
		
		return list;
	}
}
