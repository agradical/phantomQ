package com.phantomQ.queuing;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Service;

import scala.NotImplementedError;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;

@Service
public class DefaultBroker implements Broker {

	Logger logger = LoggerFactory.getLogger(DefaultBroker.class);

	Table<QueueType, String, Queue<? extends Queuable>> queuesCache = HashBasedTable.create();

	Map<String, QueueConfig> queueItemConfigCache = Maps.newHashMap();

	protected <T extends Queuable> Queue<T> getQueue(Class<T> msgType) {
		QueueConfig queueConfig = null;
		if (!(queueItemConfigCache.containsKey(msgType.getName()))) {
			queueConfig = msgType.getAnnotation(QueueConfig.class);
			queueItemConfigCache.put(msgType.getName(), queueConfig);
		}
		queueConfig = queueItemConfigCache.get(msgType.getName());
		String queueName = queueConfig.queueName();
		QueueType type = queueConfig.type();
		Queue<T> queue = null;
		if (!queuesCache.contains(type, queueName)) {
			queue = createQueue(msgType, type, queueName);
		}
		return queue;
	}

	protected <T extends Queuable> Queue<T> createQueue(Class<T> msgType,
			QueueType type, String name) {
		switch (type) {
		case AMAZON_SQS:
			return new SQSQueue<T>(name);
		default:
			throw new NotImplementedError(
					"Sorry no Implementation for queue type: " + type);
		}
	}

	@PostConstruct
	public void init() {
		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(
				false);
		scanner.addIncludeFilter(new AnnotationTypeFilter(QueueConfig.class));

		for (BeanDefinition bd : scanner
				.findCandidateComponents("<your-package-to-scan>")) {
			try {
				if (!Queuable.class.isAssignableFrom(Class.forName(bd
						.getBeanClassName()))) {
					continue;
				}
				@SuppressWarnings("unchecked")
				Class<? extends Queuable> msgType = (Class<? extends Queuable>) Class
						.forName(bd.getBeanClassName());
				getQueue(msgType);
			} catch (Exception e) {
				logger.error(
						"Could not initialize bean for "
								+ bd.getBeanClassName(), e);
			}
		}

	}

	@Override
	public QueueItem<Queuable> enqueue(Queuable msg) {
		@SuppressWarnings("unchecked")
		Queue<Queuable> queue = (Queue<Queuable>) getQueue(msg.getClass());
		return queue.enqueue(msg);
	}

	@Override
	public Boolean delete(QueueItem<? extends Queuable> msg) {
		Queue<? extends Queuable> queue = getQueue(msg.getItem().getClass());
		return queue.delete(msg);
	}

	@Override
	public <T extends Queuable> List<QueueItem<T>> get(Class<T> msgType) {
		Queue<T> queue = (Queue<T>) getQueue(msgType);
		return queue.get();
	}

}
