package com.eclipse.scada.demo.hd.client.app;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import java.util.Set;
import java.util.function.Consumer;

import org.eclipse.scada.core.Variant;
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
		
		final List<Metric> metrics = Collections.synchronizedList(new ArrayList<Metric>());
		
		Properties properties = loadConfiguration("tsdbexporter.config");
    	if (properties == null) {
    		System.exit(1);
    	}
    	
    	String daUri = properties.getProperty("org.eclipse.scada.da.uri");
        String hdUri = properties.getProperty("org.eclipse.scada.hd.uri");
        String tsdbHost = properties.getProperty("tsdb.host");
        int flushInterval = 2; 
        flushInterval = Integer.parseInt(properties.getProperty("flushInterval"));
        int tsdbPort = Integer.parseInt(sanitize(properties.getProperty("tsdb.port")));
        
        final EclipseScadaDAClient daClient;
		try {
		
			daClient = new EclipseScadaDAClient(daUri);
			final IOpenTSDBClient tsdbClient = new OpenTSDBClient(tsdbHost, tsdbPort);
			daClient.addObserver(new Observer() {
				
				@Override
				public void update(Observable o, Object arg) {

					try {
						DataItemWrapper wrapper = (DataItemWrapper)arg;
						
						Variant value = wrapper.getValue().getValue();
						double theValue = -1;
						if (value != null) {
							try {
								if (value.isDouble() || value.isInteger()) {
									theValue = value.asDouble();
									synchronized(metrics) {
										metrics.add(new Metric(
												wrapper.getItem().getItemId(),
												wrapper.getValue().getTimestamp().getTimeInMillis()/1000,
												theValue,
												null
										));
									}	
								}
							}
							catch(Exception e) {
								 e.printStackTrace();
							}
						}
			}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			EclipseScadaHDClient hdClient = new EclipseScadaHDClient(hdUri);
			hdClient.addObserver(new Observer() {
				@SuppressWarnings("unchecked")
				@Override
				public void update(Observable o, Object arg) {
					Set<HistoricalItemInformation> addedOrModified = (Set<HistoricalItemInformation>)arg;
					for (HistoricalItemInformation i : addedOrModified) {
						daClient.registerItem(i.getItemId());
						//System.out.println("== Subscribed to " + i.getItemId());
					}
				}
			});
			
			final OpenTSDBScheduledWriter scheduledWriter = new OpenTSDBScheduledWriter(flushInterval, tsdbClient, metrics);
			scheduledWriter.run();
			
			//Exporter exporter = new Exporter(hdClient, daClient, tsdbClient);
	        //exporter.run();
			
			System.in.read();
		
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
