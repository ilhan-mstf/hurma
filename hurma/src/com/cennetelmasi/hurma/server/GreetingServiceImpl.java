package com.cennetelmasi.hurma.server;

import java.io.File;

import com.cennetelmasi.hurma.client.GreetingService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements
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
	public String nodeTypeName(int index) {
		// TODO Auto-generated method stub
		File file = new File("nodeTypes.xml");
		NodeTypeParser spe = new NodeTypeParser();
		spe.parseDocument(file);
		return NodeTypeParser.nodeTypes.get(index).getName();
		
	}
    
    public String nodeTypeID(int index) {
		// TODO Auto-generated method stub
		File file = new File("nodeTypes.xml");
		NodeTypeParser spe = new NodeTypeParser();
		spe.parseDocument(file);
		return Integer.toString(NodeTypeParser.nodeTypes.get(index).getId());
	}
    
    public String nodeTypeMIB(int index) {
		// TODO Auto-generated method stub
		File file = new File("nodeTypes.xml");
		NodeTypeParser spe = new NodeTypeParser();
		spe.parseDocument(file);
		return NodeTypeParser.nodeTypes.get(index).getMIB();
	}

	@Override
	public String nodeTypeNumber() {
		// TODO Auto-generated method stub
		File file = new File("nodeTypes.xml");
		NodeTypeParser spe = new NodeTypeParser();
		spe.parseDocument(file);
		return Integer.toString(NodeTypeParser.nodeTypes.size());
	}
}
