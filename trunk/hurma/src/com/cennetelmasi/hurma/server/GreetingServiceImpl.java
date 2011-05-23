package com.cennetelmasi.hurma.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

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
    	node.setId(id);
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
				System.out.println(n.getId());
				// Set values
				n.setNumberOfDevices(Integer.parseInt(values.get(1)));
				n.setProbability(Float.parseFloat(values.get(2)));
				n.setNodeName(values.get(3));
				System.out.println("server: " + values.get(3));
				// Rearrange alarm list
				ArrayList<Alarm> temp = new ArrayList<Alarm>();
				for(Alarm a : n.getAlarms())
					if(selectedAlarms.contains(a.getOid()))
						temp.add(a);
				n.setAlarms(temp);
				// Set MIBObject values
				int size = requiredFields.size();
				for(int i=0; i<size; i++) {
					System.out.println(requiredFields.get(i));
					MIBObject obj = n.getMibObjectByOid(requiredFields.get(i));
					if(obj==null) System.out.println("obje null");
					i++;
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
	
	/**
	 * Argument format:
	 * simulation name
	 * simulation type
	 * simulation time (hour:minute:second)
	 * 
	 * to create xml use nodes (list of NodeObj) in se (simulationEngine)
	 */
	@Override
	public void saveSimulation(ArrayList<String> values) {
		String simName = values.get(0);
		String simType = values.get(1);
		String simHour = values.get(2);
		String simMin  = values.get(3);
		String simSec  = values.get(4);
		File file = new File(simName+".xml");
		FileWriter writer = null;
		try {
			writer = new FileWriter(file);
			writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?><networkTopologies></networkTopologies>");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
				
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        Document doc = null;
        try {
			docBuilder = dbfac.newDocumentBuilder();
			doc = docBuilder.parse(file);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        Element	root = doc.getDocumentElement();
          
        
        Element duration = doc.createElement("duration");
        Element simulationType  = doc.createElement("simulationType");
        
        Text durationText = doc.createTextNode(simHour+":"+simMin+":"+simSec);
        Text simulationTypeText	 = doc.createTextNode(simType);
        
        duration.appendChild(durationText);
        simulationType.appendChild(simulationTypeText);
        
        root.appendChild(duration);
        root.appendChild(simulationType);
        
        Element topology = doc.createElement("networkTopology");
        for(int i = 0; i < se.getNodes().size(); i++){
			int number 	= se.getNodes().get(i).getNumberOfDevices();
			float error	= se.getNodes().get(i).getProbability();
			Element node = doc.createElement("node");
			Element nodeId = doc.createElement("id");
			Element nodeName = doc.createElement("name");
			Element nodeType = doc.createElement("nodeType");
			Element trapRate = doc.createElement("trapRate");
			Element numberOfDevices = doc.createElement("numberOfDevices");
			Element alarms = doc.createElement("alarms");
			Element fields = doc.createElement("fields");
			Element mib = doc.createElement("mib");
			
			mib.setTextContent(se.getNodes().get(i).getMIB());
			nodeId.setTextContent(Integer.toString(se.getNodes().get(i).getId()));
			nodeName.setTextContent(se.getNodes().get(i).getNodeName());
			nodeType.setTextContent(se.getNodes().get(i).getNodeTypeName());
			trapRate.setTextContent(Float.toString(error));
			numberOfDevices.setTextContent(Integer.toString(number));
			node.appendChild(nodeId);
			node.appendChild(nodeName);
			node.appendChild(mib);
			node.appendChild(nodeType);
			node.appendChild(trapRate);
			node.appendChild(numberOfDevices);
			node.appendChild(alarms);
			node.appendChild(fields);
			ArrayList<String> reqObjOids = new ArrayList<String>();
			for(int j = 0; j < se.getNodes().get(i).getAlarms().size(); j++){
				Element alarm = doc.createElement("alarm");
				alarm.setAttribute("oid", se.getNodes().get(i).getAlarms().get(j).getOid());
				alarm.setTextContent(se.getNodes().get(i).getAlarms().get(j).getName());
				alarms.appendChild(alarm);
				for(int k = 0; k < se.getNodes().get(i).getAlarms().get(j).getRequiredObjects().size(); k++){
					if(!reqObjOids.contains(se.getNodes().get(i).getAlarms().get(j).getRequiredObjects().get(k)))
						reqObjOids.add(se.getNodes().get(i).getAlarms().get(j).getRequiredObjects().get(k).toString());
				}
			}
			
			for(int j = 0; j < se.getNodes().get(i).getMibObjects().size(); j++){
				//if(reqObjOids.contains(se.getNodes().get(i).getMibObjects().get(j).getOid())){
					//˛u anda b¸t¸n deerleri al˝yor, yukar˝daki sat˝rdaki comment kald˝r˝l˝rsa
					//bu sefer de sadece seÁili alarmlara gereken objeleri alacak, bu halini seÁtim pikaÁu!..
					Element field = doc.createElement("field");
					field.setAttribute("oid", se.getNodes().get(i).getMibObjects().get(j).getOid());
					field.setAttribute("name",se.getNodes().get(i).getMibObjects().get(j).getName());
					field.setTextContent(se.getNodes().get(i).getMibObjects().get(j).getValue());
					fields.appendChild(field);
				//}
			}
			
			topology.appendChild(node);
        }
        
        root.appendChild(topology);
        
        TransformerFactory transfac = TransformerFactory.newInstance();
		Transformer trans = null;
		try {
			trans = transfac.newTransformer();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		}
		trans.setOutputProperty(OutputKeys.INDENT, "yes");

		StringWriter sw = new StringWriter();
	    StreamResult result = new StreamResult(sw);
	    DOMSource source = new DOMSource(doc);
	    try {
			trans.transform(source, result);
		} catch (TransformerException e2) {
			e2.printStackTrace();
		}
	    String xmlString = sw.toString();
	 
	    OutputStream f0 = null;
	    byte buf[] = xmlString.getBytes();
	    try {
			f0 = new FileOutputStream(file);
			for(int i=0;i<buf .length;i++) {
				f0.write(buf[i]); 
			}
			f0.close();
			buf = null;
	    } catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			   e.printStackTrace();
		   }
	      
	}
	/*	
	 * <duration></duration>
		<simulationType></simulationType>
		<networkTopology>
			<node>
				<id></id>
				<name></name>
				<nodeTypeId></nodeTypeId>
				<trapRate></trapRate>
				<alarm>
				<id></id>
				<name></name>
				<field></field>
				<value></value>
				</alarm>
				...
				<ip></ip>
				<numberOfDevices></numberOfDevices>
			</node>
		</networkTopology>
	 * 
	 * */
	/**
	 * Return format:
	 * name of saved simulation names
	 */

	@Override
	public ArrayList<String> getSavedSimulationName() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Return format:
	 * bunu beraber konu≈üup karar verelim...
	 * √ß√ºnk√º g√∂ndermesi geren √ßok ≈üey var..
	 */
	@Override
	public ArrayList<String> loadSimulation(String simulationName) {
		// TODO Auto-generated method stub
		File file = new File(simulationName);
		TopologyParser ntp = new TopologyParser();
		ntp.parseDocument(file);
		TopologyObject topology = TopologyParser.nodeTypes.get(0);
				
		return null;
	}

}
