package com.cennetelmasi.hurma.server;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;

public class NodeTypeParser extends DefaultHandler{
	public static List<NodeTypeObject> nodeTypes;
	private String tempVal;
	public static NodeTypeObject tempNode;
	
	public NodeTypeParser(){
		nodeTypes = new ArrayList<NodeTypeObject>();
	}

	public void parseDocument(File file) {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
			SAXParser sp = spf.newSAXParser();
			sp.parse(file, this);			
		}catch(SAXException se) {
			se.printStackTrace();
		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch (IOException ie) {
			ie.printStackTrace();
		}}
	
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		tempVal = "";
		
		if(qName.equalsIgnoreCase("nodeType")) {
			tempNode = new NodeTypeObject();
		}
	}
	
	public void characters(char[] ch, int start, int length) throws SAXException {
		tempVal = new String(ch,start,length);
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {

		if(qName.equalsIgnoreCase("nodeType")) {
			nodeTypes.add(tempNode);	
		}else if (qName.equalsIgnoreCase("id")) {
			tempNode.setId(Integer.parseInt(tempVal));
		}else if (qName.equalsIgnoreCase("name")) {
			tempNode.setName(tempVal);
		}else if (qName.equalsIgnoreCase("MIBName")) {
			tempNode.setMIB(tempVal);
		}
	}

	public static List<NodeTypeObject> getNodeTypes() {
		return nodeTypes;
	}
}
