package com.eclipse.scada.demo.hd.client.app;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Metric {
	
	private final String metric;
	private final long timestamp;
	private final double value;
	private final Map<String, String> tags;
	
	public Metric(final String metric, final long timestamp, double value, Map<String, String> tags) {
		this.metric = metric;
		this.timestamp = timestamp;
		this.value = value;
		this.tags = tags;
	}
	
	public String getMetric() {
		return this.metric;
	}
	
	public long getTimestamp() {
		return this.timestamp;
	}
	
	public double getValue () {
		return this.value;
	}
	
	public Map<String, String> getTags() {
		return this.tags;
	}
	

	
	
	
}
