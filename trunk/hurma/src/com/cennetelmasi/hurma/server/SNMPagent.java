package com.cennetelmasi.hurma.server;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SNMPagent extends Thread {
	private String type;
	private Protocol protocol;
	private NodeObj node;
	private int time;
	private ScheduledFuture<?> beeperHandle = null;
	private Runnable beeper = null;
	private boolean stop = false;

	private final static ScheduledExecutorService scheduler = Executors
			.newScheduledThreadPool(2);

	public SNMPagent (String type, Protocol protocol, int time, int cofactor) {
		this.setType(type);
		this.setProtocol(protocol);
		this.getProtocol().setCofactor(cofactor);
		this.time = time;
	}

	public SNMPagent (String type, Protocol protocol, NodeObj node) {
		this.setType(type);
		this.setProtocol(protocol);
		this.setNode(node);
	}

	/**
	 * Two different SNMP Agents are exist.
	 * One type for nodes, the other for 
	 * timer agent which calculates time
	 * and sends traps.
	 */
	
	public void run() {
		if (getType().equals("TIMER")) {
			beeper = new Runnable() {
				public void run() {
					try {
						protocol.setPassedTime(protocol.getPassedTime()+5);
						getProtocol().wakeUp();
						// wait until deciding to sending traps
						sleep(100/getProtocol().getCofactor());
						getProtocol().checkQueue();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
			beeperHandle = scheduler.scheduleAtFixedRate(beeper, 0, 5000/getProtocol().getCofactor(), TimeUnit.MILLISECONDS);
			scheduler.schedule(new Runnable() {
				public void run() {
					beeperHandle.cancel(true);
					System.out.println("\nSERVER: simulation successfully ended.");
				}
			}, 1000*time/getProtocol().getCofactor(), TimeUnit.MILLISECONDS);
		} else {
			getProtocol().run(getNode());
		}
		
		if(isStop()) return;
	}
	
	public void pauseScheduler() {
		beeperHandle.cancel(true);
	}
	
	public void resumeScheduler() {
		beeperHandle = scheduler
				.scheduleAtFixedRate(beeper, 0, 5000/getProtocol().getCofactor(), TimeUnit.MILLISECONDS);
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}

	public Protocol getProtocol() {
		return protocol;
	}

	public void setNode(NodeObj node) {
		this.node = node;
	}

	public NodeObj getNode() {
		return node;
	}
	
	public void setStop(boolean stop) {
		this.stop = stop;
	}

	public boolean isStop() {
		return stop;
	}

	public static ScheduledExecutorService getScheduler() {
		return scheduler;
	}

}
