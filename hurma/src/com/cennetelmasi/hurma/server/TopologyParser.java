package com.cennetelmasi.hurma.server;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import net.percederberg.mibble.MibLoaderException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;

public class TopologyParser extends DefaultHandler{
	static List<TopologyObject> nodeTypes;
	private String tempVal;
	private static TopologyObject tempNode;
	private static NodeObj node;
	private static boolean isField = false;
	private static boolean isAlarm = false;
	private static String oid = "";
	public TopologyParser() {
		nodeTypes = new ArrayList<TopologyObject>();
	}

	public void parseDocument(File file) {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
			SAXParser sp = spf.newSAXParser();
			sp.parse(file, this);			
		} catch(SAXException se) {
			se.printStackTrace();
		} catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}
	
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		tempVal = "";
		
		if(qName.equalsIgnoreCase("networkTopologies")) {
			tempNode = new TopologyObject();
			node = new NodeObj();
			tempNode.addToNode(node);
		} else if(qName.equalsIgnoreCase("field")) {
			isField = true;
			oid = attributes.getValue("oid");
		} else if(qName.equalsIgnoreCase("alarm")) {
			isAlarm = true;
			oid = attributes.getValue("oid");
		}
	}
	
	public void characters(char[] ch, int start, int length) throws SAXException {
		tempVal = new String(ch,start,length);
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {

		if(isField){
			node.setMibObjectByOid(oid, tempVal);
			oid = "";
			isField = false;
		} else if(isAlarm){
			node.getAlarmByOid(oid).setSelectStatus(true);
			oid = "";
			isAlarm = false;
		} else if(qName.equalsIgnoreCase("networkTopologies")) {
			nodeTypes.add(tempNode);	
		}else if (qName.equalsIgnoreCase("duration")) {
			tempNode.setDuration(tempVal);
		}else if (qName.equalsIgnoreCase("simulationType")) {
			tempNode.setName(tempVal);
		}else if (qName.equalsIgnoreCase("node")) {
			node = new NodeObj();
			tempNode.addToNode(node);
		}else if (qName.equalsIgnoreCase("id")) {
			node.setId(Integer.parseInt(tempVal));
		}else if (qName.equalsIgnoreCase("name")) {
			node.setNodeName(tempVal);
		}else if (qName.equalsIgnoreCase("mib")) {
			node.setMIB(tempVal);
			try {
				node.parseMIB();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (MibLoaderException e) {
				e.printStackTrace();
			}
		}else if (qName.equalsIgnoreCase("trapRate")) {
			node.setProbability(Float.parseFloat(tempVal));
		}else if (qName.equalsIgnoreCase("numberOfDevices")) {
			node.setNumberOfDevices(Integer.parseInt(tempVal));
		}else if (qName.equalsIgnoreCase("nodeType")) {
			node.setNodeTypeName(tempVal);
		}
	}

	public static List<TopologyObject> getNodeTypes() {
		return nodeTypes;
	}

}
