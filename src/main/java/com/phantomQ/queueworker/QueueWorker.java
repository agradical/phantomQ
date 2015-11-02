package com.phantomQ.queueworker;

import com.phantomQ.queuing.Queuable;

public interface QueueWorker<T extends Queuable> extends Runnable{

	
}
