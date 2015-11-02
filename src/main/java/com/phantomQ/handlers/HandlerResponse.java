package com.phantomQ.handlers;

public class HandlerResponse {
	
	Boolean isDBConfigAllowed;
	Boolean toDeleteFromQueue;
	
	public Boolean getIsDBConfigAllowed() {
		return isDBConfigAllowed;
	}
	public void setIsDBConfigAllowed(Boolean isDBConfigAllowed) {
		this.isDBConfigAllowed = isDBConfigAllowed;
	}
	public Boolean getToDeleteFromQueue() {
		return toDeleteFromQueue;
	}
	public void setToDeleteFromQueue(Boolean toDeleteFromQueue) {
		this.toDeleteFromQueue = toDeleteFromQueue;
	}
	
}
