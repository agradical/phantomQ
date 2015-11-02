package com.phantomQ.handlers;

import com.phantomQ.Test.Testclass;
import com.phantomQ.queuing.QueueHandle;

@QueueHandle(type=Testclass.class)
public class TestHandler<T> implements QueueHandler<T>{

	T test;

	public HandlerResponse handle(T t) {
		// TODO Auto-generated method stub
		HandlerResponse response = new HandlerResponse();
		response.setIsDBConfigAllowed(false);
		response.setToDeleteFromQueue(true);
		
		return response;
	}
}
