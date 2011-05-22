package com.cennetelmasi.hurma.server;

public class Trap {
	private NodeObj node;
	private int alarmId;

	public Trap(NodeObj node, int id) {
		this.setNode(node);
		this.setAlarmId(id);
	}

	public void setNode(NodeObj node) {
		this.node = node;
	}

	public NodeObj getNode() {
		return node;
	}

	public void setAlarmId(int alarmId) {
		this.alarmId = alarmId;
	}

	public int getAlarmId() {
		return alarmId;
	}

}
