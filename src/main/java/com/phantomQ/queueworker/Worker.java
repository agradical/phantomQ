package com.phantomQ.queueworker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.phantomQ.queuing.Broker;
import com.phantomQ.queuing.Queuable;

@Service
public class Worker<T extends Queuable>{

	Logger logger = LoggerFactory.getLogger(Worker.class);
	
	@Autowired
	Broker broker;
	
	static ApplicationContext ctx;
	
	public void start(WorkerArgs<T>[] values, ApplicationContext appctx){
		
		Worker.ctx = appctx;
		
		for (WorkerArgs<T> value : values){
			DefaultQueueWorker<T> queuable = new DefaultQueueWorker<T>(value.getClasstype(), broker, ctx);
			
			
			
			for(int i=0; i<value.getNumOfThreads(); i++){				
				final Thread t1 = new Thread(queuable);
				t1.start();
			}
			
		}
	}

//	@Override
//	public void setApplicationContext(ApplicationContext applicationContext)
//			throws BeansException {
//		// TODO Auto-generated method stub
//		this.ctx = applicationContext;
//	}
//	

}
