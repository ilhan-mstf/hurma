package com.cennetelmasi.hurma.server;

import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import java.io.*;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;

import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MyFormHandler extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String deviceName = null;
		String MIBName = null;
		String image = null;
		try {
			int state = 0;
			ServletFileUpload upload = new ServletFileUpload();
	    	res.setContentType("text/plain");
	      
	    	FileItemIterator iterator = upload.getItemIterator(req);
	    	while (iterator.hasNext()) {
	    		FileItemStream item = iterator.next();
	    		InputStream stream = item.openStream();
	    		BufferedWriter dos;
	    		
	    		if (item.isFormField()) {
	    			if(state++ == 0){
	    				deviceName = item.getFieldName();
	    			} else {
	    				image = item.getFieldName();
	    			}
	    		} else {
	    			MIBName = item.getName();
	    			File outFile = new File("MIBs\\"+MIBName);
	    			dos = new BufferedWriter(new FileWriter(outFile));
	    			int c;
	    			while((c = stream.read()) != -1){
	    				dos.write(c);
	    			}
	    			dos.close();
	    		}
	    	}
	    	
	    	System.out.println(deviceName);
	    	System.out.println(image);
	    } catch (Exception ex) {
	    	throw new ServletException(ex);
	    }
	    if(deviceName != null && MIBName != null){
	    	try {
				saveToXml(deviceName, MIBName, image);
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (TransformerException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			}
	    }
	   
	}
	
	public static void saveToXml(String device, String mib, String img) throws ParserConfigurationException, TransformerException, SAXException, IOException{
		File file = new File("nodeTypes.xml");
		NodeTypeParser spe = new NodeTypeParser();
		spe.parseDocument(file);
		int i = NodeTypeParser.nodeTypes.size();
		int lastId = 0;
		for(int j = 0; j<i; j++){
			if(lastId < NodeTypeParser.nodeTypes.get(j).getId())
				lastId = NodeTypeParser.nodeTypes.get(j).getId();
		}
		
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
        Document doc = docBuilder.parse("nodeTypes.xml");
        Element	root = doc.getDocumentElement();
          
        Element nodeType = doc.createElement("nodeType");
        Element id		 = doc.createElement("id");
        Element mibName  = doc.createElement("MIBName");
        Element name 	 = doc.createElement("name");
        Element image	 = doc.createElement("icon");
        
        Text mibNameText = doc.createTextNode("MIBs\\"+mib);
        Text nameText	 = doc.createTextNode(device);
        Text imageText	 = doc.createTextNode(img);
        Text idText		 = doc.createTextNode(Integer.toString(++lastId));
        
        id.appendChild(idText);
        mibName.appendChild(mibNameText);
        name.appendChild(nameText);
        image.appendChild(imageText);
        
        nodeType.appendChild(id);
        nodeType.appendChild(mibName);
        nodeType.appendChild(name);
        nodeType.appendChild(image);
        
        root.appendChild(nodeType);
        
        TransformerFactory transfac = TransformerFactory.newInstance();
		Transformer trans = transfac.newTransformer();
		trans.setOutputProperty(OutputKeys.INDENT, "yes");

		StringWriter sw = new StringWriter();
	    StreamResult result = new StreamResult(sw);
	    DOMSource source = new DOMSource(doc);
	    trans.transform(source, result);
	    String xmlString = sw.toString();
	 
	    OutputStream f0;
	    byte buf[] = xmlString.getBytes();
	    f0 = new FileOutputStream("nodeTypes.xml");
	    for(i=0;i<buf .length;i++) {
		   f0.write(buf[i]);
		}
		f0.close();
		buf = null;      
	}
}
