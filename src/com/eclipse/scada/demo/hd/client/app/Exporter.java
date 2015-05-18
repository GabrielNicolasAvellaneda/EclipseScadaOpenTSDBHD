package com.eclipse.scada.demo.hd.client.app;

import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import org.eclipse.scada.hd.ItemListListener;
import org.eclipse.scada.hd.data.HistoricalItemInformation;
import org.eclipse.scada.core.ConnectionInformation;
import org.eclipse.scada.hd.client.Connection;
import org.eclipse.scada.core.client.AutoReconnectController;
import org.eclipse.scada.core.client.ConnectionState;
import org.eclipse.scada.core.client.ConnectionStateListener;
import org.eclipse.scada.da.client.DataItemValue;
import org.eclipse.scada.hd.client.ngp.ConnectionImpl;
import org.eclipse.scada.sec.callback.Callback;
import org.eclipse.scada.sec.callback.CallbackHandler;
import org.eclipse.scada.utils.concurrent.NotifyFuture;

public class Exporter {

	private EclipseScadaHDClient hdClient;
	private EclipseScadaDAClient daClient;
	private IOpenTSDBClient tsdbClient;
	
	public Exporter (EclipseScadaHDClient hdClient, EclipseScadaDAClient daClient, IOpenTSDBClient tsdbClient) {

		this.hdClient = hdClient;
		this.daClient = daClient;
		this.tsdbClient = tsdbClient;
	}
	
	public void run() {
		
		// TODO: Implement this.
	}
	
	
}
