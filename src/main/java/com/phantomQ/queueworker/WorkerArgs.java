package com.phantomQ.queueworker;

import com.phantomQ.queuing.Queuable;

public class WorkerArgs<T extends Queuable> {

	Class<T> classtype;
	
	public Class<T> getClasstype() {
		return classtype;
	}
	public void setClasstype(Class<T> classtype) {
		this.classtype = classtype;
	}
	public Long getNumOfThreads() {
		return numOfThreads;
	}
	public void setNumOfThreads(Long numOfThreads) {
		this.numOfThreads = numOfThreads;
	}
	Long numOfThreads;
}
