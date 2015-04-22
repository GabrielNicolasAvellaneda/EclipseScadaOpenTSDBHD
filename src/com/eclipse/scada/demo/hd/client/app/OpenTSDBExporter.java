package com.eclipse.scada.demo.hd.client.app;

import java.util.Set;

import org.eclipse.scada.hd.ItemListListener;
import org.eclipse.scada.hd.data.HistoricalItemInformation;
import org.eclipse.scada.core.ConnectionInformation;
import org.eclipse.scada.hd.client.Connection;
import org.eclipse.scada.core.client.AutoReconnectController;
import org.eclipse.scada.core.client.ConnectionState;
import org.eclipse.scada.core.client.ConnectionStateListener;
import org.eclipse.scada.hd.client.ngp.ConnectionImpl;

public class OpenTSDBExporter {

	public class ItemListener implements ItemListListener {

		@Override
		public void listChanged(Set<HistoricalItemInformation> addedOrModified,
				Set<String> removed, boolean full) {

			System.out.println("Item listener: " + String.format("%s %s %s", addedOrModified.toString(), removed.toString(), full));
		}
	}
	
	private final String connectionString;
	private OpenTSDBClient client;
	
	private Connection connection;
	private AutoReconnectController controller;
	
	public OpenTSDBExporter (OpenTSDBClient client, String connectionString) {
		this.connectionString = connectionString;
		
		this.client = client;
	}
	
	public void run() throws Exception {
		createConnection(connectionString);
		
	}
	
	public void createConnection(final String connectionString) throws Exception {
		this.connection = new ConnectionImpl(ConnectionInformation.fromURI(connectionString));
		
		connection.addConnectionStateListener(new ConnectionStateListener() {
			
			public void stateChange(org.eclipse.scada.core.client.Connection arg0, ConnectionState state, Throwable e) {
				System.out.println("Connection state changed: " + state);
				if ( e != null )
					e.printStackTrace();
			};
			
			
		});
		
		this.controller = new AutoReconnectController(this.connection, 10 * 1000);
		this.controller.connect();
		this.connection.addListListener(new ItemListener());
	}
	
	public void disposeConnection() {
		if (this.connection == null) {
			return;
		}
		
		this.controller.dispose(true);
		this.controller = null;
		this.connection.dispose();
		this.connection = null;
	}
	
	@Override
	protected void finalize() throws Throwable {
		disposeConnection();
		super.finalize();
	}
	
}
