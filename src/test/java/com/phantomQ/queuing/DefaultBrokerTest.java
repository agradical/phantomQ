package com.phantomQ.queuing;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.phantomQ.Test.Testclass;


public class DefaultBrokerTest{

	static ApplicationContext context;

	static DefaultBroker broker; 
	
	static Testclass test;
	
	
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		context = new ClassPathXmlApplicationContext(new String[] {
				"classpath*:applicationContext-test.xml" });
		broker = context.getBean(DefaultBroker.class);
		//test =  context.getBean(Testclass.class);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	
	@Test
	public void testGet() {
		
		List<QueueItem<Testclass>> items = broker.get(Testclass.class);
		
		QueueItem<Testclass> item = items.get(0);
		
		System.out.print(item.getItem().getName());
		//fail("Not yet implemented");
	}

}
