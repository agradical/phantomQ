package com.phantomQ.Test;

import org.springframework.beans.factory.annotation.Autowired;

import com.phantomQ.queuing.Broker;

public class TestMain {

	@Autowired
	Broker broker;
	
	public void check()
	{
		Testclass test =  new Testclass();
		test.setName("ankur");
		test.setType("check");
		broker.enqueue(test);
	}
	
}
