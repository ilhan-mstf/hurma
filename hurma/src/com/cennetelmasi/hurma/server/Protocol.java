package com.cennetelmasi.hurma.server;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class Protocol {
	private String 	ipAddress 	= "127.0.0.1";
	private int 	port		= 163;
	
	private int passedTime = 0;
		
	// cofactor is used for reduced time
	private int cofactor;
	final Random generator = new Random();
	
	// shared values used by timer and node
	private Queue<Trap> alarmQueue = new LinkedList<Trap>();
	private int count = 0;
	private StringBuffer log = new StringBuffer();

	public Protocol() {
	}
	
	// NODE will execute this function during the simulation
	public synchronized void run(NodeObj node) {
		while (true) {
			try {
				System.out.println("node: device " + node.getId() + "- wait");
				wait();
			} catch (InterruptedException e) {
				System.err.println("node: ERROR! when waiting the thread: "
						+ node.getId());
				e.printStackTrace();
			}
			int iterator = 0;
			for (Alarm alarm : node.getAlarms()) {
				if(!alarm.isSelected())	continue;
				float rate = node.getNumberOfDevices()*errorRateCalculation(alarm.getProb(),alarm.getFreq());
				if(rate > 1) rate = 1;
				
				Random rand = new Random();
				float val = rand.nextFloat();

				if(rate>=val){
					System.out.println("node: device " + node.getId() + "- trap " + alarm.getName());
					alarmQueue.add(new Trap(node, iterator));
				}
				iterator++;
			}
		}
	}

	public synchronized void wakeUp() {
		notifyAll();
		System.out.println("timer: notify all");
	}

	public synchronized void checkQueue() throws IOException {
		System.out.println("timer: checkqueue");
		while (!alarmQueue.isEmpty()) {
			Trap trap = alarmQueue.poll();
			sendSnmpV2Trap(trap);
		}
	}

	public boolean probCal(float probability) {
		return generator.nextBoolean();
	}

	public String getIpAt(String base, int node) {
		String[] ips = base.split("\\.");
		int[] ipsConverted = {0,0,0,0};
		for(int i = 3; i >= 0; i--){
			ipsConverted[i] = Integer.parseInt(ips[i]) + node;
			if(ipsConverted[i] > 255){
				node = ipsConverted[i] / 255;
				ipsConverted[i] %= 255;
			} else {
				node = 0;
			}
			
		}
		return ipsConverted[0]+"."+ipsConverted[1]+"."+ipsConverted[2]+"."+ipsConverted[3]+"";
	}
	
	public String generateMacAddress() {
		String[] macs = {"","","","","",""};
		for(int i = 0; i < 6; i++){
			Random rand = new Random();
			int a = rand.nextInt(255);
			macs[i] = Integer.toHexString(a);
		}
		return macs[0]+":"+macs[1]+":"+macs[2]+":"+macs[3]+":"+macs[4]+":"+macs[5];
	}
	
	public void sendSnmpV2Trap(Trap trap) throws IOException {
		NodeObj node = trap.getNode();
		Alarm alarm = node.getAlarms().get(trap.getAlarmId());
		int numberOfDevices = node.getNumberOfDevices();
		
		String trapOID = alarm.getOid().toString();
		String ip = getIpAt(node.getIp(), generator.nextInt(numberOfDevices));
		String mac = generateMacAddress();
		
		TransportMapping transport;
		try {
			transport = new DefaultUdpTransportMapping();
		    Snmp snmp = new Snmp(transport);
		    snmp.listen();
	
		    CommunityTarget target = new CommunityTarget();
		    target.setCommunity(new OctetString("public"));
		    target.setAddress(new UdpAddress("127.0.0.1" + "/" + port));
		    target.setVersion(SnmpConstants.version2c);
	
		    PDU pdu = new PDU();
		    pdu.setType(PDU.TRAP);
		    // system time
			pdu.add(new VariableBinding(new OID("1.3.6.1.2.1.1.3.0"), new Integer32(0)));
			// oid
			pdu.add(new VariableBinding(new OID("1.3.6.1.6.3.1.1.4.1.0"), new OID(trapOID)));
			
			// TODO when getIpObject and getMacObject are null, add condition???
			pdu.add(new VariableBinding(new OID(node.getIpObject().getOid()), new OctetString(ip)));
			pdu.add(new VariableBinding(new OID(node.getMacObject().getOid()), new OctetString(mac)));
			
			// add the required objects...
			for (Object obj : alarm.getRequiredObjects()) {
				MIBObject temp = node.getMibObjectByOid(obj.toString());
				if(temp.isSendable())
					pdu.add(new VariableBinding(new OID(temp.getOid().toString()),
							new OctetString(temp.getValue())));
			}
	
		    ResponseEvent responseEvent = snmp.send(pdu, target);
		    snmp.close();
		    count++;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int hour = passedTime / 3600;
		int minute = (passedTime - hour*3600) / 60;
		int second = passedTime - minute*60 - hour*3600;

		String str = timeFormat(hour) + ":" + timeFormat(minute) + ":" + timeFormat(second) + " " 
					+ node.getNodeName() + " - " + node.getId() + " : "
					+ trap.getNode().getAlarms().get(trap.getAlarmId()).getName() + 
					" for the device " + ip + "\n";
		
		log.append(str);
		System.out.println("server: " + count + " " + alarm.getName() + " -- device " + node.getId()
							+ " - Sending V2 Trap to " + ipAddress + " on Port " + port);
	}
	
	public String timeFormat(int val) {
		if(val < 10)
			return "0" + val;
		else
			return Integer.toString(val);
	}
	
	public float errorRateCalculation(float prob, int freq) {
		int totalSeconds = 1;
		switch (freq) {
		//seconds
		case 0:
			totalSeconds = 1;
			break;
		//minute
		case 1:
			totalSeconds = 60;
			break;
		//hour
		case 2:
			totalSeconds = 60*60;
			break;
		//day
		case 3:
			totalSeconds = 60*60*24;
			break;
		//week
		case 4:
			totalSeconds = 60*60*24*7;
			break;
		//month
		case 5:
			totalSeconds = 60*60*24*30;
			break;
		//year
		case 6:
			totalSeconds = 60*60*24*365;
			break;
		}
		return prob/totalSeconds;
	}

	public int getCofactor() {
		return cofactor;
	}

	public void setCofactor(int cofactor) {
		this.cofactor = cofactor;
	}

	public void setLog(StringBuffer log) {
		this.log = log;
	}

	public StringBuffer getLog() {
		return log;
	}

	public void setPassedTime(int passedTime) {
		this.passedTime = passedTime;
	}

	public int getPassedTime() {
		return passedTime;
	}

}