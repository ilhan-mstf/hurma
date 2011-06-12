package com.cennetelmasi.hurma.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;

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
import org.xml.sax.SAXException;

public class SimulationEngine {
	private ArrayList<SNMPagent> agents = new ArrayList<SNMPagent>(); 
	private ArrayList<NodeObj> nodes = new ArrayList<NodeObj>();
	private Protocol protocol;
	private SNMPagent timer;
	private String simulationState = "ready";

	public SimulationEngine() {
		setProtocol(new Protocol());
	}
	
	public void start(int time, int cofactor) {
		setSimulationState("running");
		for(NodeObj node : nodes) {
			SNMPagent agent = new SNMPagent("NODE", getProtocol(), node);
			agents.add(agent);
			agent.start();
		}
		timer = new SNMPagent("TIMER", getProtocol(), time, cofactor);
		timer.start();
		System.out.println("SERVER: threads are created.");
	}
	
	public void pause() {
		setSimulationState("paused");
		timer.pauseScheduler();
	}
	
	public void resume() {
		setSimulationState("running");
		timer.resumeScheduler();
	}
	
	public void stop() {
		setSimulationState("ready");
		timer.pauseScheduler();
		timer.setStop(true);
		for(SNMPagent agent : agents)
			agent.setStop(true);
	}
	
	public void clear() {
		agents.clear();
		nodes.clear();
	}
	
	/**
	 *  <duration></duration>
		<simulationType></simulationType>
		<networkTopology>
			<node>
				<id></id>
				<mib></mib>
				<name></name>
				<numberOfDevices></numberOfDevices>
				<ip></ip>
				<alarms>
					<alarm oid="" probability="" frequency=""></alarm>
				</alarms>
				<fields>
					<field oid="" name=""></field>
				</fields>
				...
			</node>
		</networkTopology>
	 **/
	public void save(ArrayList<String> values) {
		String simName = values.get(0);
		String simType = values.get(1);
		String simHour = values.get(2);
		String simMin  = values.get(3);
		String simSec  = values.get(4);
		File file = new File("saved/" + simName + ".xml");
		FileWriter writer = null;
		try {
			writer = new FileWriter(file);
			writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?><simulation></simulation>");
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
        
        Element simulationName = doc.createElement("simulationName");
        Element duration = doc.createElement("duration");
        Element simulationType  = doc.createElement("simulationType");
        
        simulationName.setTextContent(simName);
        duration.setTextContent(simHour+":"+simMin+":"+simSec);
        simulationType.setTextContent(simType);
        
        root.appendChild(simulationName);
        root.appendChild(duration);
        root.appendChild(simulationType);
        
        Element topology = doc.createElement("networkTopology");
        for(NodeObj n : getNodes()) {
			Element node = doc.createElement("node");
			Element nodeId = doc.createElement("id");
			Element nodeName = doc.createElement("name");
			Element numberOfDevices = doc.createElement("numberOfDevices");
			Element ip = doc.createElement("ip");
			Element image = doc.createElement("image");
			Element alarms = doc.createElement("alarms");
			Element fields = doc.createElement("fields");
			Element mib = doc.createElement("mib");
			
			mib.setTextContent(n.getMIB());
			nodeId.setTextContent(Integer.toString(n.getId()));
			nodeName.setTextContent(n.getNodeName());
			numberOfDevices.setTextContent(n.getNumberOfDevices()+"");
			ip.setTextContent(n.getIp());
			image.setTextContent(n.getImage());
			
			node.appendChild(nodeId);
			node.appendChild(mib);
			node.appendChild(nodeName);
			node.appendChild(numberOfDevices);
			node.appendChild(ip);
			node.appendChild(image);
			node.appendChild(alarms);
			node.appendChild(fields);
			
			ArrayList<String> reqObjOids = new ArrayList<String>();
			for(Alarm a : n.getAlarms()) {
				if(a.isSelected()) {
					Element alarm = doc.createElement("alarm");
					alarm.setAttribute("oid", a.getOid());
					alarm.setAttribute("probability", a.getProb()+"");
					alarm.setAttribute("frequency", a.getFreq()+"");
					alarm.setTextContent(a.getName());
					alarms.appendChild(alarm);
					for(Object obj : a.getRequiredObjects()){
						if(!reqObjOids.contains(obj))
							reqObjOids.add(obj.toString());
					}
				}
			}
			
			for(MIBObject obj : n.getMibObjects()){
				//if(reqObjOids.contains(se.getNodes().get(i).getMibObjects().get(j).getOid())){
					//Şu anda bütün değerleri alıyor, yukarıdaki satırdaki comment kaldırılırsa
					//bu sefer de sadece seçili alarmlara gereken objeleri alacak, bu halini seçtim pikaçu!..
					Element field = doc.createElement("field");
					field.setAttribute("oid", obj.getOid());
					field.setAttribute("name", obj.getName());
					field.setTextContent(obj.getValue());
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
			for(int i=0; i<buf .length; i++) {
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

	public void setNodes(ArrayList<NodeObj> nodes) {
		this.nodes = nodes;
	}

	public ArrayList<NodeObj> getNodes() {
		return nodes;
	}

	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}
	
	public Protocol getProtocol() {
		return protocol;
	}

	public void setSimulationState(String simulationState) {
		this.simulationState = simulationState;
	}

	public String getSimulationState() {
		if(timer.getScheduler().isShutdown())
			simulationState = "finished";
		return simulationState;
	}
	
}
