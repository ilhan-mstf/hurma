package com.cennetelmasi.hurma.server;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class Protocol {
	// SNMP variables
	TransportMapping transport;
	CommunityTarget comtarget;
	Snmp snmp;

	// initial values
	private String UdpAddress = "127.0.0.1/0";
	private String community = "public";
	private String ipAddress = "127.0.0.1";
	private int port = 8001;

	// shared values used by timer and node
	final Random generator = new Random();
	private Queue<Trap> alarmQueue = new LinkedList<Trap>();
	private int count = 0;
	private StringBuffer log = new StringBuffer();

	public Protocol() {
	}

	// NODE will execute this function during the simulation
	public synchronized void run(NodeObj node) {
		while (true) {
			try {
				System.out.println("server: device " + node.getId() + "- wait");
				wait();
			} catch (InterruptedException e) {
				System.err.println("server: ERROR- when waiting the thread: "
						+ node.getId());
				e.printStackTrace();
			}
			// decide to send trap or not
			// TODO probability
			//if (probCal(node.getProbability())) {
				System.out.println("server: device " + node.getId() + "- trap");
				// select alarm
				// add to queue
				alarmQueue.add(new Trap(node, 0));
			//}
		}
	}

	public synchronized void wakeUp() {
		notifyAll();
		System.out.println("server: timer- notify all");
	}

	public synchronized void checkQueue() throws IOException {
		System.out.println("timer: checkqueue");
		while (!alarmQueue.isEmpty()) {
			Trap trap = alarmQueue.poll();
			sendSnmpV2Trap(trap);
		}
	}

	public boolean probCal(float probability) {
		return true; // generator.nextBoolean();
	}

	public void initSNMP() throws IOException {
		// Create Transport Mapping
		transport = new DefaultUdpTransportMapping(new UdpAddress(UdpAddress));
		transport.listen();

		// Create Target
		comtarget = new CommunityTarget();
		comtarget.setCommunity(new OctetString(community));
		comtarget.setVersion(SnmpConstants.version2c);
		comtarget.setAddress(new UdpAddress(ipAddress + "/" + port));
		comtarget.setRetries(2);
		comtarget.setTimeout(3000);

		// Create SNMP
		snmp = new Snmp(transport);
	}

	public void closeSNMP() throws IOException {
		snmp.close();
		transport.close();
	}

	public void sendSnmpV2Trap(Trap trap) throws IOException {
		NodeObj node = trap.getNode();
		Alarm alarm = node.getAlarms().get(0);

		String trapOID = alarm.getOid().toString();
		String description = alarm.getDescription();

		// Create PDU for V2
		PDU pdu = new PDU();
		// need to specify the system up time
		pdu.add(new VariableBinding(SnmpConstants.sysUpTime, new OctetString(
				new Date().toString())));
		pdu.add(new VariableBinding(SnmpConstants.snmpTrapOID, new OID(trapOID)));
//		pdu.add(new VariableBinding(SnmpConstants.snmpTrapAddress,
//				new IpAddress(ipAddress)));

		// add the required objects...
		for (Object obj : alarm.getRequiredObjects()) {
			MIBObject temp = node.getMibObjectByOid(obj.toString());
			pdu.add(new VariableBinding(new OID(temp.getOid().toString()),
					new OctetString(temp.getDescription())));
		}

		// variable binding for Enterprise Specific objects, Severity
		// (should be defined in MIB file)
		pdu.add(new VariableBinding(new OID(trapOID), new OctetString(
				description)));
		pdu.setType(PDU.NOTIFICATION);

		// Send the PDU
		snmp.send(pdu, comtarget);
		count++;
		
		String str = node.getNodeName() + " - " + node.getId() + " : " + trap.getNode().getAlarms().get(0).getName() + "\n";
		log.append(str);
		System.out.println("server: " + count + " -- device " + node.getId() +
				 			" - Sending V2 Trap to " + ipAddress + " on Port " + port);
		// ResponseEvent respEv = snmp.send(pdu, comtarget);
		// PDU response = respEv.getResponse();
		snmp.close();
	}

	public void setLog(StringBuffer log) {
		this.log = log;
	}

	public StringBuffer getLog() {
		return log;
	}

}
