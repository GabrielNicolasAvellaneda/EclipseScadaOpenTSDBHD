package com.eclipse.scada.demo.hd.client.app;

import java.util.Observable;
import java.util.Set;

import org.eclipse.scada.core.ConnectionInformation;
import org.eclipse.scada.core.client.AutoReconnectController;
import org.eclipse.scada.core.client.ConnectionFactory;
import org.eclipse.scada.core.client.ConnectionState;
import org.eclipse.scada.core.client.ConnectionStateListener;
import org.eclipse.scada.hd.ItemListListener;
import org.eclipse.scada.hd.client.Connection;
import org.eclipse.scada.hd.data.HistoricalItemInformation;
import org.eclipse.scada.hd.client.ngp.ConnectionImpl;

public class EclipseScadaHDClient extends Observable {

	private final String uri;
	private AutoReconnectController controller;
	private Connection connection;
	
	public EclipseScadaHDClient (String uri) throws Exception {
		
		this.uri = uri;
		
		try
        {
            Class.forName ( "org.eclipse.scada.hd.client.ngp.ConnectionImpl" );
        }
        catch ( final ClassNotFoundException e )
        {
        	throw new Exception("Unable to find implementation for hd:ngp protocol", e);
        }

        connection = new ConnectionImpl(ConnectionInformation.fromURI( uri));
        if ( connection == null )
        {
            throw new Exception("Unable to find a connection driver for specified URI");
        }

        connection.addConnectionStateListener ( new ConnectionStateListener () {
            @Override
            public void stateChange (
                    final org.eclipse.scada.core.client.Connection connection,
                    final ConnectionState state, final Throwable error )
            {
                // TODO: Log?
            }
        } );

        controller = new AutoReconnectController (
                connection );
        controller.connect ();
        
        connection.addListListener(new ItemListListener() {
			
			@Override
			public void listChanged(Set<HistoricalItemInformation> addedOrModified,
					Set<String> removed, boolean full) {
				EclipseScadaHDClient.this.setChanged();
				EclipseScadaHDClient.this.notifyObservers(addedOrModified);
			}
		} );
	}
	
	public String getUri() {
		return uri;
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
