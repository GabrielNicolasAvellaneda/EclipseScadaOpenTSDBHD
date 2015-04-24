package com.eclipse.scada.demo.hd.client.app;

import java.util.Calendar;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import java.util.Set;

import org.eclipse.scada.hd.client.*;
import org.eclipse.scada.hd.data.HistoricalItemInformation;
import org.eclipse.scada.hd.ItemListListener;

import java.io.*;

public class App {

	private static Boolean validateProperties(Properties properties) {
		
		String hdUri = properties.getProperty("org.eclipse.scada.hd.uri");
		if (hdUri == null || hdUri.isEmpty()) {
			System.out.println("You need to specify org.eclipse.scada.hd.uri");
			return false;
		}
		
		String daUri = properties.getProperty("org.eclipse.scada.da.uri");
		if (daUri == null || daUri.isEmpty())
		{
			System.out.println("You need to specify org.eclipse.scada.da.uri");
			return false;
		}
		
		String tsdbHost = properties.getProperty("tsdb.host");
		if (tsdbHost == null || tsdbHost.isEmpty()) {
			System.out.println("You need to specify tsdb.host");
			return false;
		}
		
		String tsdbPort = properties.getProperty("tsdb.port");
		if (tsdbPort == null || tsdbPort.isEmpty()) {
			System.out.println("You need to specify tsdb.port");
			return false;
		}
				
		return true;
	}
	
	private static Properties loadConfiguration(final String configFile) {
		try {
    		
    		FileInputStream fs = new FileInputStream(configFile);
    		
    		// TODO: Add log services.
    		Properties properties = new Properties();
    		try {
    		
    			properties.load(fs);
    			validateProperties(properties);

    			return properties;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	catch (FileNotFoundException e) {
    		System.out.println(String.format("Could not find configuration file: %s", System.getProperty("user.dir") + "\\" + configFile));
    		
    		e.printStackTrace();
    	}
		
		return null;
	}
	
	private static String sanitize(final String string) {
		if (string == null) {
			return string;
		}
		
		return string.trim();
	}

	public static void main(String[] args) {
		
		Properties properties = loadConfiguration("tsdbexporter.config");
    	if (properties == null) {
    		System.exit(1);
    	}
    	
    	String daUri = properties.getProperty("org.eclipse.scada.da.uri");
        String hdUri = properties.getProperty("org.eclipse.scada.hd.uri");
        String tsdbHost = properties.getProperty("tsdb.host");
        int tsdbPort = Integer.parseInt(sanitize(properties.getProperty("tsdb.port")));
        
        EclipseScadaDAClient daClient;
		try {
		
			// Create a HD Client, every time the list of item changes, it will inform the DA Client to subscribe/unsubscribe to value changes.
			// Exporter should listen for DA Client value changes and sent to OpenTSDB.
			daClient = new EclipseScadaDAClient(daUri);
			final OpenTSDBClient tsdbClient = new OpenTSDBClient(tsdbHost, tsdbPort);
			daClient.addObserver(new Observer() {
				
				@Override
				public void update(Observable o, Object arg) {

					try {
						DataItemWrapper wrapper = (DataItemWrapper)arg;
						
						double value = wrapper.getValue().getValue().asDouble();
						long timestamp = wrapper.getValue().getTimestamp().getTimeInMillis()/1000;
											
						tsdbClient.write(wrapper.getItem().getItemId(), timestamp, value, null);
						
						System.out.println(wrapper.item);
						System.out.println(value);
						System.out.println(timestamp);
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					
				}
			});
			
			
			daClient.registerItem("REGION1.SITE1.heat.V");
			//
			//EclipseScadaHDClient hdClient = new EclipseScadaHDClient(hdUri);
	        
		
	        //Exporter exporter = new Exporter(hdClient, daClient, tsdbClient);
	        //exporter.run();
		
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
		
		 
		
		
		
		
	}
	
	

}
