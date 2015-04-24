package com.eclipse.scada.demo.hd.client.app;

import org.eclipse.scada.da.client.DataItem;
import org.eclipse.scada.da.client.DataItemValue;

public class DataItemWrapper {

	public DataItemWrapper(DataItem item, DataItemValue value) {
		this.item = item;
		this.value = value;
	}
	
	public DataItem item;
	public DataItemValue value;
	
	public DataItem getItem() {
		return item;
	}
	
	public DataItemValue getValue () {
		return value;
	}
	
}
