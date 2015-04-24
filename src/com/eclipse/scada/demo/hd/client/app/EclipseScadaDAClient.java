package com.eclipse.scada.demo.hd.client.app;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.scada.core.ConnectionInformation;
import org.eclipse.scada.core.client.AutoReconnectController;
import org.eclipse.scada.core.client.ConnectionFactory;
import org.eclipse.scada.core.client.ConnectionState;
import org.eclipse.scada.core.client.ConnectionStateListener;
import org.eclipse.scada.da.client.Connection;
import org.eclipse.scada.da.client.DataItem;
import org.eclipse.scada.da.client.DataItemValue;
import org.eclipse.scada.da.client.ItemManagerImpl;
import org.eclipse.scada.da.client.ngp.ConnectionImpl;

public class EclipseScadaDAClient extends Observable
{

	private String uri;
	
	private final ItemManagerImpl itemManager;
	
	private final HashMap<String, DataItem> items;
	
	private AutoReconnectController controller;
	private Connection connection;
	
	public EclipseScadaDAClient(final String uri) throws Exception {
		this.uri = uri;
		this.items = new HashMap<>();
		
        connection = new ConnectionImpl ( ConnectionInformation.fromURI ( uri ) );
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
                System.out.println ( "DA Connection state is now: " + state );
            }
        } );

        controller = new AutoReconnectController (
                connection );
        controller.connect ();

        itemManager = new ItemManagerImpl ( connection );
	}
	
	public void registerItem(final String itemId) {
	
		DataItem item = items.get(itemId);
		if (item != null) {
			return;
		}
		
		final DataItem dataItem = new DataItem (itemId, itemManager);
        dataItem.addObserver ( new Observer () {
            @Override
            public void update ( final Observable observable, final Object update )
            {
                final DataItem item = (DataItem)observable;
            	final DataItemValue div = (DataItemValue)update;
            	
            	div.getValue();
            	setChanged();
            	notifyObservers(new DataItemWrapper(item, div));
            }
        } );
	}
	
	public void unregisterItem(final String itemId) {
		DataItem item = items.remove(itemId);
		if (item == null) { 
			return;
		}
		
		item.unregister();
	}
    
}

