package com.phantomQ.queueworker;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.phantomQ.handlers.HandlerResponse;
import com.phantomQ.handlers.QueueHandler;
import com.phantomQ.queuing.Broker;
import com.phantomQ.queuing.Queuable;
import com.phantomQ.queuing.QueueHandle;
import com.phantomQ.queuing.QueueItem;

@Service
public class DefaultQueueWorker<T extends Queuable> implements QueueWorker<T>{


	protected boolean stop = false;

	protected boolean started = false;
	protected boolean done = false;

	Broker broker;
	
	static ApplicationContext applicationContext;
	
	Logger logger = LoggerFactory.getLogger(QueueWorker.class);


	protected Class<T> type;
	
	public DefaultQueueWorker(){
		super();
	}
	
	public DefaultQueueWorker(Class<T> type, Broker broker, ApplicationContext ctx){
		
		super();
		this.type = type;
		this.broker = broker;
		
		DefaultQueueWorker.applicationContext = ctx;
		
		
	}
	
	
		
	static Map<String, String> availableHandles = Maps.newHashMap();
	
	public synchronized <I extends QueueHandler<?>> QueueHandler<T> getHandle(QueueItem<T> item) 
								
										throws ClassNotFoundException, 
														InstantiationException, 
																	IllegalAccessException
	{		
		String itemTypeId = item.getItemTypeIdentifier();
		String handleName  = availableHandles.get(itemTypeId);
		
		@SuppressWarnings("unchecked")
		Class<? extends QueueHandler<T>> handle = (Class<? extends QueueHandler<T>>)Class.forName(handleName);
		try {
			
			return handle.getConstructor(ApplicationContext.class).newInstance(applicationContext);
		
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	
	@SuppressWarnings("unchecked")
	public <I extends QueueHandler<T>, J extends Queuable> 
					void prepareHandle(Class<I> msgType){
		QueueHandle queueHandle;
		if (!(availableHandles.containsKey(msgType.getName()))) {
			queueHandle = msgType.getAnnotation(QueueHandle.class);
			availableHandles.put(((Class<? extends QueueHandler<T>>)queueHandle.type()).getName(), msgType.getName());		
		}
		
	}
	
	@PostConstruct
	public void init() {
		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(
				false);
		scanner.addIncludeFilter(new AnnotationTypeFilter(QueueHandle.class));

		for (BeanDefinition bd : scanner
				.findCandidateComponents("<your-package-to-scan>")) {
			try {
				if (!QueueHandler.class.isAssignableFrom(Class.forName(bd
						.getBeanClassName()))) {
					continue;
				}
				@SuppressWarnings("unchecked")
				Class<? extends QueueHandler<T>> msgType = (Class<? extends QueueHandler<T>>) Class
						.forName(bd.getBeanClassName());
				
				prepareHandle(msgType);
				
				
			
			} catch (Exception e) {
				logger.error(
						"Could not initialize bean for "
								+ bd.getBeanClassName(), e);
			}
		}

	}
	
	public synchronized void stop() {
		if (!started)
			return;
		this.stop = true;
		logger.warn("Stoping DataQueueConsumer.....");

	}
	
	public void run() {
		// TODO Auto-generated method stub
		started = true;
		while (!stop) {
			if (stop) {
				logger.warn("Stopped DataQueueConsumer.....");
				return;
			}
			
		try{
			
		
			List<QueueItem<T>> itemRecieved = broker.get(type);
			
			for(QueueItem<T> item: itemRecieved )
			{
				QueueHandler<T> handler = null;
				try {
					handler = getHandle(item);
				} catch (ClassNotFoundException | InstantiationException
						| IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if(handler != null)
				{
					HandlerResponse response = callHandle(handler, item);
					//handler.handle(item.getItem());	
					
					if(response !=null && response.getToDeleteFromQueue()){
						broker.delete(item);
					}
				}
			}
			
			}catch(Exception e){
				e.printStackTrace();
				logger.error("Error in QueueWorker");
			}
		}
	}
	
	public HandlerResponse callHandle(QueueHandler<T> handler, QueueItem<T> item){
		
		HandlerResponse response;
		try{
			response = new HandlerCallable<T>(handler, item).call();
		}catch(Exception e){
			return null;
		}
		return response;
	}
	
	private static class HandlerCallable<T extends Queuable> implements Callable<HandlerResponse>{

		QueueHandler<T> handler;

		QueueItem<T> item;
		

		public void setHandler(QueueHandler<T> handler) {
			this.handler = handler;
		}



		public void setItem(QueueItem<T> item) {
			this.item = item;
		}

		
		public HandlerCallable(QueueHandler<T> handler, QueueItem<T> item){		
			this.setHandler(handler);
			this.setItem(item);
		}
		
		@Override
		public HandlerResponse call() throws Exception {
			// TODO Auto-generated method stub
			HandlerResponse response = new HandlerResponse();
			response = handler.handle(item.getItem());
			return response;
		}
		
	}

//	@Override
//	public void setApplicationContext(ApplicationContext applicationContext)
//			throws BeansException {
//		
//		if(DefaultQueueWorker.applicationContext != null)
//			DefaultQueueWorker.applicationContext = applicationContext;
//		// TODO Auto-generated method stub
//		
//	} 

	
}
