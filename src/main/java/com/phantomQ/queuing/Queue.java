package com.phantomQ.queuing;

import java.util.List;

public interface Queue<T extends Queuable> {
	public QueueItem<T> enqueue(T msg) ;
	public Boolean delete(QueueItem<? extends Queuable> msg);
	public List<QueueItem<T>> get();
}
