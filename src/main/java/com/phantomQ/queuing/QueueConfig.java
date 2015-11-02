package com.phantomQ.queuing;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({ TYPE })
@Retention(RUNTIME)
public @interface QueueConfig {

	String queueName() default "";

	QueueType type() default QueueType.AMAZON_SQS;

	boolean allowDuplicates() default true;

	boolean persist() default false;
}
