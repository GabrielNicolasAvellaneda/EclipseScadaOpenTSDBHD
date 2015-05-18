package com.eclipse.scada.demo.hd.client.app;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public interface IOpenTSDBClient {

	public abstract void write(String metric, long timestamp, Double value,
			Map<String, String> tags) throws UnknownHostException, IOException;

	public abstract void write(final List<Metric> metrics) throws UnknownHostException, IOException;

	public abstract void close() throws IOException;

	public abstract void connect() throws UnknownHostException, IOException;
}
