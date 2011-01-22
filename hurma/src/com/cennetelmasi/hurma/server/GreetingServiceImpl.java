package com.cennetelmasi.hurma.server;

import com.cennetelmasi.hurma.client.GreetingService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements
                GreetingService {
        
        public String greetServer(String input, String pass) {
                if(input.equals("Hurma") && pass.equals("hurma"))
                        return "true";
                else
                        return "false";
        }
}
