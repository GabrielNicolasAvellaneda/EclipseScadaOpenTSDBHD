package com.eclipse.scada.demo.hd.client.app;

import java.util.Set;

import org.eclipse.scada.hd.client.*;
import org.eclipse.scada.hd.data.HistoricalItemInformation;
import org.eclipse.scada.hd.ItemListListener;

import java.io.*;

public class App {

	public static void main(String[] args) {
		
		OpenTSDBClient tsdbClient = new OpenTSDBClient("localhost", 4242);
		
		OpenTSDBExporter exporter = new OpenTSDBExporter(tsdbClient, "hd:ngp://admin:admin12@localhost:2302");
		try {
			exporter.run();
			
			System.out.println("Press a key to exit.");
			System.in.read();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	

}
