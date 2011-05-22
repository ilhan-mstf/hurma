package com.cennetelmasi.hurma.server;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.percederberg.mibble.MibLoaderException;

import com.cennetelmasi.hurma.client.GreetingService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 * @param <NodeObject>
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl<NodeObject> extends RemoteServiceServlet implements
                GreetingService {
	
	private SimulationEngine se = new SimulationEngine();
	int id = 0;
	
	private HttpSession session;

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
    
    /**
     * Return format:
     * numberOfNodeTypes, (id, name, mib) ...
     */
    public ArrayList<String> getNodeTypes() {
		ArrayList<String> values = new ArrayList<String>();
    	File file = new File("nodeTypes.xml");
		NodeTypeParser ntp = new NodeTypeParser();
		ntp.parseDocument(file);
		int numberOfNodeTypes = NodeTypeParser.getNodeTypes().size();
		values.add(Integer.toString(numberOfNodeTypes));
		for(NodeTypeObject nodeType : NodeTypeParser.getNodeTypes()) {
			values.add(Integer.toString(nodeType.getId()));
			values.add(nodeType.getName());
			values.add(nodeType.getMIB());
			values.add(nodeType.getIcon());
		}
		System.out.println("server: xml is parsed.");
		return values;
    }
    
    
    /**
     * Return format:
     * id, numberofAlarms, (AlarmName, AlarmOID), ... (ObjectName, ObjectOID) ...
     */
    public ArrayList<String> getNodeObjValues(String mib) {
		ArrayList<String> values = new ArrayList<String>();
    	NodeObj node = null;
		try {
			node = new NodeObj(mib);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (MibLoaderException e) {
			e.printStackTrace();
		}
    	// Set id
    	values.add(Integer.toString(id));
    	// Add alarm size
    	values.add(Integer.toString(node.getAlarms().size()));
    	// Add alarms
    	for(Alarm alarm : node.getAlarms()) {
    		values.add(alarm.getName());
    		values.add(alarm.getOid());
    	}
    	// Add required objects
    	for(Alarm alarm : node.getAlarms()) {
    		for(Object requiredObject : alarm.getRequiredObjects()) {
    			MIBObject obj = new MIBObject();
				obj = node.getMibObjectByOid(requiredObject.toString());
				if(!values.contains(obj.getName())){
					values.add(obj.getName());
					values.add(obj.getOid().toString());
				}
    		}
    	}
    	se.getNodes().add(node);
    	System.out.println("server: node created, node id: " + id);
    	id++;
    	return values;
    }

    /**
     * Argument formats
     * values : id, numberOfDevices, prob
     * selectedAlarms : oid, ...
     * requiredFields : (oid, value) ... 
     */
	public void setNodeObjValues(ArrayList<String> values,
			ArrayList<String> selectedAlarms, ArrayList<String> requiredFields) {
		for(NodeObj n : se.getNodes()) {
			if(n.getId() == Integer.parseInt(values.get(0))) {
				// Set values
				n.setNumberOfDevices(Integer.parseInt(values.get(1)));
				n.setProbability(Float.parseFloat(values.get(2)));
				// Rearrange alarm list
				ArrayList<Alarm> temp = new ArrayList<Alarm>();
				for(Alarm a : n.getAlarms())
					if(selectedAlarms.contains(a.getOid()))
						temp.add(a);
				n.setAlarms(temp);
				// Set MIBObject values
				int size = requiredFields.size();
				for(int i=0; i<size; i++) {
					MIBObject obj = n.getMibObjectByOid(requiredFields.get(i++));
					obj.setValue(requiredFields.get(i));
				}
				System.out.println("server: SNMP Agent is created, node id: " + n.getId());
			}
		}
	}
	
	public void deleteNodeObj(String id) {
		int i;
		for(i=0; i<se.getNodes().size(); i++)
			if(se.getNodes().get(i).getId()==Integer.parseInt(id))
				break;
		se.getNodes().remove(--i);
		System.out.println("server: node deleted, node id: " + i);
	}

	public String getOutputs() {
		String str = se.getProtocol().getLog().toString();
		se.getProtocol().setLog(new StringBuffer());
		return str;
	}

	public void startSimulation(int time) {
		System.out.println("server: simulation started.");
		se.start(time);
	}

	public String pause() {
		se.pause();
		return getOutputs();
	}

	public void resume() {
		se.resume();
	}

	public String stop() {
		se.stop();
		return getOutputs();
	}

	@Override
	public boolean sessionControl() {
		HttpServletRequest request = this.getThreadLocalRequest();
    	session = request.getSession();
		
		if (session.getAttribute("id") == null) {
    		return false;
    	} else {
    		return true;
    	}
	}

	@Override
	public void destroySession() {
		session.removeAttribute("id");
	}

	@Override
	public void createSession() {
		if (session.getAttribute("id") == null) {
    		session.setAttribute("id", "user");
    	}
	}

}
