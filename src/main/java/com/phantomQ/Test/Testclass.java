package com.phantomQ.Test;

import com.phantomQ.queuing.Queuable;
import com.phantomQ.queuing.QueueConfig;

@QueueConfig(queueName="testQ")
public class Testclass implements Queuable{

	String name;
	String type;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
}
