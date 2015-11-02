package com.phantomQ.queuing;

import java.util.List;

public interface Broker {

	public QueueItem<Queuable> enqueue(Queuable msg);

	public  Boolean delete(QueueItem<? extends Queuable> msg);

	public <T extends Queuable> List<QueueItem<T>> get(Class<T> msgType);

}
