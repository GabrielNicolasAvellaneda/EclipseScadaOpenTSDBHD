package com.eclipse.scada.demo.hd.client.app;

import java.io.IOException;
import java.io.DataOutputStream;
import java.net.UnknownHostException;
import java.net.Socket;
import java.util.HashSet;
import java.util.Locale;

public class OpenTSDBClient {

	private final int port;
	private final String hostname;
	private Socket client; 
		
	public OpenTSDBClient (String hostname, int port) {
		
		this.port = port;
		this.hostname = hostname;
	}
	
	public void connect() throws UnknownHostException, IOException {
		if (client != null && client.isConnected()) {
			return;
		}
		
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
		
	public void write(String metric, long timestamp, Double value, HashSet<String> tags) throws UnknownHostException, IOException {
		connect();
		
		DataOutputStream output = new DataOutputStream(client.getOutputStream());
		String str = format(metric, timestamp, value);
		System.out.println(str);
		output.writeBytes(str);
	}
	
	private String format(String metric, long timestamp, Double value) {
		return String.format("put %s %s %s test=true\n", metric, timestamp, String.format(Locale.ENGLISH, "%.2f", value));
	}
	
	public void finalize() throws Throwable {
		try {
			close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			super.finalize();
		}
	}
}
