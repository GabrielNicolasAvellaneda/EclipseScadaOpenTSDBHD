package com.eclipse.scada.demo.hd.client.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class OpenTSDBScheduledWriter {
	
	private final Timer timer;
	private final IOpenTSDBClient client;
	private final List<Metric> metrics;
	private final int seconds;
	
	public OpenTSDBScheduledWriter(int seconds, IOpenTSDBClient client, List<Metric> metrics) {
		this.seconds = seconds;
		this.client = client;
		this.metrics = metrics;
		this.timer = new Timer();
	}
	
	public void run() {
		timer.scheduleAtFixedRate(new OpenTSDBWriter(), 1000, seconds * 1000);
	}
	
	class OpenTSDBWriter extends TimerTask {
		public void run() {
			List<Metric> list = new ArrayList<Metric>();
			synchronized (metrics) {
				Iterator<Metric> i = metrics.iterator(); // Must be in synchronized block
				while (i.hasNext()) {
					final Metric metric = i.next();
					list.add(metric);
					i.remove();
				}
			  }
			try {
				client.write(list);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
