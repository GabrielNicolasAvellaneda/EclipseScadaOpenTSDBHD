package com.eclipse.scada.demo.hd.client.app;

import java.io.*;
import java.net.*;
import java.util.HashSet;

public class OpenTSDBClient {

	private final int port;
	private final String hostname;
	private Socket client; 
		
	public OpenTSDBClient (String hostname, int port) {
		
		this.port = port;
		this.hostname = hostname;
	}
	
	
	public void connect() throws UnknownHostException, IOException {

		client = new Socket(hostname, port);
	}
	
	public void close() throws IOException {
		if (client.isConnected()) {
			client.close();	
		}
	}
	
	private int getCurrentTimestamp() {
		return (int) (System.currentTimeMillis()/1000);
	}
		
	public void write(String metric, Double value, HashSet<String> tags) throws UnknownHostException, IOException {
		if (!client.isConnected()) {
			connect();
		}
		
		DataOutputStream output = new DataOutputStream(client.getOutputStream());
		output.writeBytes(format(metric, value));
	}
	
	private String format(String metric, Double value) {
		return String.format("put %s %s %f test=true", metric, getCurrentTimestamp(), value);
	}
	
	public void finalize() throws Throwable {
		try {
		
		client.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			super.finalize();
		}

	}
}
