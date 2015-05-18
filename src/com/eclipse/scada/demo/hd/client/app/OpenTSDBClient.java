package com.eclipse.scada.demo.hd.client.app;

import java.io.IOException;
import java.io.DataOutputStream;
import java.net.UnknownHostException;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OpenTSDBClient implements IOpenTSDBClient {

	private final int port;
	private final String hostname;
	private Socket client; 
		
	public OpenTSDBClient (String hostname, int port) {
		
		this.port = port;
		this.hostname = hostname;
	}
	
	@Override
	public void connect() throws UnknownHostException, IOException {
		if (client != null && client.isConnected()) {
			return;
		}
		
		client = new Socket(hostname, port);
	}
	
	@Override
	public void close() throws IOException {
		if (client.isConnected()) {
			client.close();	
		}
	}
	
	private int getCurrentTimestamp() {
		return (int) (System.currentTimeMillis()/1000);
	}
	
	@Override
	public void write(final List<Metric> metrics) throws UnknownHostException, IOException {
		
		StringBuffer buffer = new StringBuffer();
		for (Metric metric : metrics) {
			buffer.append(format(metric.getMetric(), metric.getTimestamp(), metric.getValue(), metric.getTags()));	
			
		}
		rawWrite(buffer.toString());
	}
	
	private void rawWrite(String str) throws UnknownHostException, IOException {
		connect();
		
		DataOutputStream output = new DataOutputStream(client.getOutputStream());
		output.writeBytes(str);
	}
		
	@Override
	public void write(String metric, long timestamp, Double value, Map<String, String> tags) throws UnknownHostException, IOException {
		connect();
		
		String str = format(metric, timestamp, value, tags);
		rawWrite(str);
	}
	
	private String format(String metric, long timestamp, Double value, Map<String, String> tags) {
		// TODO: Format tags
		final String tagList = "null=false";
				
		return String.format("put %s %s %s %s\n", metric, timestamp, String.format(Locale.ENGLISH, "%.2f", value), tagList);
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
