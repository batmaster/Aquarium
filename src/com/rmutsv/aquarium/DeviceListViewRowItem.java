package com.rmutsv.aquarium;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class DeviceListViewRowItem {
	
	private String bid;
	private String ip;
	private String port;
	private String lastTime;
	
	public DeviceListViewRowItem(String bid, String ip, String port, String lastTime) {
		this.bid = bid;
		this.ip = ip;
		this.port = port;
		this.lastTime = lastTime;
	}

	public String getBid() {
		return bid;
	}

	public void setBid(String bid) {
		this.bid = bid;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getLastTime() {
		return lastTime;
	}

	public void setLastTime(String lastTime) {
		this.lastTime = lastTime;
	}
	
}