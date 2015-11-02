package com.phantomQ.handlers;

public interface QueueHandler<T> {

	public HandlerResponse handle(T t);
}
