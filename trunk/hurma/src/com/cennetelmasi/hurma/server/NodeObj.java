package com.cennetelmasi.hurma.server;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import net.percederberg.mibble.Mib;
import net.percederberg.mibble.MibLoader;
import net.percederberg.mibble.MibLoaderException;
import net.percederberg.mibble.MibSymbol;
import net.percederberg.mibble.MibType;
import net.percederberg.mibble.MibValue;
import net.percederberg.mibble.MibValueSymbol;
import net.percederberg.mibble.snmp.SnmpNotificationType;
import net.percederberg.mibble.snmp.SnmpObjectType;
import net.percederberg.mibble.value.ObjectIdentifierValue;

public class NodeObj {
	private int id;
	private int numberOfDevices;
	private String ip;
	private String mac;
	private MIBObject ipObject;
	private MIBObject macObject;
	private String nodeName;
	private String image;
	
	private String MIB;
	private ArrayList<MIBObject> mibObjects;
	private ArrayList<Alarm> alarms;
	
	public NodeObj() {
		MIB = new String();
		mibObjects = new ArrayList<MIBObject>();
		alarms = new ArrayList<Alarm>();
	}
	
	public NodeObj(String MIB) throws IOException, MibLoaderException {
		this.MIB = new String(MIB);
		this.ipObject = new MIBObject();
		this.macObject = new MIBObject();
		this.mibObjects = new ArrayList<MIBObject>();
		this.alarms = new ArrayList<Alarm>();
		parseMIB();
	}
	
	public Alarm getAlarmByOid(String oid){
		for(int i = 0; i<alarms.size(); i++){
			if(alarms.get(i).getOid().equals(oid))
				return alarms.get(i);
		}
		return null;
	}
	
	public void parseMIB() throws IOException, MibLoaderException {
		MibLoader  	loader = new MibLoader();
	    Mib     	mib = null;
	    String 		mibFile = new String(this.MIB);
	    File        file = new File(mibFile);

        if (file.exists()) {
            if (loader.getMib(file) != null) {
                return;
            }
            if (!loader.hasDir(file.getParentFile())) {
                loader.removeAllDirs();
                loader.addDir(file.getParentFile());
            }
            mib = loader.load(file);
        } else {
            mib = loader.load(mibFile);
        }
        
        Iterator<?> iter = mib.getAllSymbols().iterator();
        MibSymbol symbol;
        Alarm alarm = null;
        while (iter.hasNext()) {
        	ObjectIdentifierValue  	oid = null;
        	MibValue               	value;
        	MibType					type;
        	
            symbol = (MibSymbol) iter.next();
            if (symbol instanceof MibValueSymbol) {
                value = ((MibValueSymbol) symbol).getValue();
                type = ((MibValueSymbol) symbol).getType();
               
                if(value instanceof ObjectIdentifierValue && type.getName().equals(new String("NOTIFICATION-TYPE"))) {
                	//alarms will be kept in alarms list
                	alarm = new Alarm();
                	alarm.setOid(value.toString());
                	alarm.setName(value.getName());
                	oid = (ObjectIdentifierValue) value;
                    MibValueSymbol  symbol2 = oid.getSymbol();
                    SnmpNotificationType snmpNot = (SnmpNotificationType) symbol2.getType();
                	alarm.setDescription(snmpNot.getDescription());
                	for(int i = 0; i < snmpNot.getObjects().size(); i++) {
                		alarm.addToRequiredObjects(snmpNot.getObjects().get(i));
                	}
                	//alarm.setRequiredObjects(snmpNot.getObjects());
                	alarms.add(alarm);
                } else if (value instanceof ObjectIdentifierValue && type.getName().equals(new String("OBJECT-TYPE"))) {
                	//other objects will be kept in mibObject list
                	MIBObject obj = new MIBObject();
              
                	obj.setOid(value.toString()); //value -> 1.3.6.1.4.1.4329.2.38.3.2.1.2.1
                	obj.setName(value.getName()); //value.getName() -> phoneNumber
                	oid = (ObjectIdentifierValue) value;
                    MibValueSymbol  symbol2 = oid.getSymbol();
                    
                    SnmpObjectType snmpObj = (SnmpObjectType) symbol2.getType();
                	
                    obj.setDescription(snmpObj.getDescription());
                	obj.setValue(null);
                	
                	if(snmpObj.getSyntax().toString().equals(new String("[APPLICATION 0] OCTET STRING (SIZE (4))"))){
                    	obj.setSendable(false);
                		ipObject = obj;
                    } else if(snmpObj.getSyntax().toString().equals(new String("[UNIVERSAL 4] OCTET STRING (SIZE (0..6))"))){
                    	obj.setSendable(false);
                    	macObject = obj;
                    }
                    mibObjects.add(obj);
                    
                }
            }
        }
	}
	
	public void setMibObjectByOid(String Oid, String value) {
		for(int i = 0; i<mibObjects.size(); i++) {
			if(mibObjects.get(i).getOid().equals(Oid)) {
				mibObjects.get(i).setValue(value);
				break;
			}
		}
	}
	
	public MIBObject getMibObjectByOid(String Oid) {
		for(int i = 0; i<mibObjects.size(); i++) {
			if(mibObjects.get(i).getOid().equals(Oid)) {
				return mibObjects.get(i);
			}
		}
		return null;
	}
	
	public String getMibObjectNameByOid(String Oid) {
		for(int i = 0; i<mibObjects.size(); i++) {
			if(mibObjects.get(i).getOid().equals(Oid)) {
				return mibObjects.get(i).getName();
			}
		}
		return null;
	}
	
	public String getMIB() {
		return MIB;
	}

	public void setMIB(String mIB) {
		MIB = mIB;
	}

	public ArrayList<MIBObject> getMibObjects() {
		return mibObjects;
	}

	public void setMibObjects(ArrayList<MIBObject> mibObjects) {
		this.mibObjects = mibObjects;
	}

	public ArrayList<Alarm> getAlarms() {
		return alarms;
	}

	public void setAlarms(ArrayList<Alarm> alarms) {
		this.alarms = alarms;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setNumberOfDevices(int numberOfDevices) {
		this.numberOfDevices = numberOfDevices;
	}

	public int getNumberOfDevices() {
		return numberOfDevices;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setIp(String ip) {
		this.ip = ip;
		this.ipObject.setValue(ip);
	}

	public String getIp() {
		return ip;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
		this.macObject.setValue(mac);
	}
	
	public void setImage(String image) {
		this.image = image;
	}

	public String getImage() {
		return image;
	}

}
