package com.peterfranza.paulbunyan4j.annotations.handlers;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.google.common.base.Stopwatch;
import com.google.inject.Injector;
import com.peterfranza.paulbunyan4j.LoggingClient.LoggingSender;
import com.peterfranza.paulbunyan4j.annotations.LoggableEvent;
import com.peterfranza.paulbunyan4j.messages.Messages.LoggingMessage;

public class LoggableEventHandler implements MethodInterceptor {

	@Inject @Named("applicationName") String applicationName;
	@Inject @Named("applicationId") String applicationId;
	@Inject @Named("hostname") String hostname;
	
	@Inject Injector injector;
	@Inject LoggingSender sender;
	
	public Object invoke(MethodInvocation invocation) throws Throwable {
		LoggableEvent event = invocation.getMethod().getAnnotation(LoggableEvent.class);
		long startTime = System.currentTimeMillis();
		Stopwatch watch = Stopwatch.createStarted();
		Object val = invocation.proceed();
		watch.stop();
		
		sender.send(LoggingMessage.newBuilder()
				.setApplicationid(applicationName)
				.setApplicationinstanceid(applicationId)
				.setHostname(hostname)
				.setTimestamp(startTime)
				.setLabel(event.value().equals("") ? invocation.getMethod().getName() : event.value())
				.setDuration(watch.elapsed(TimeUnit.MILLISECONDS))
				.setUsername(injector.getInstance(event.subjectProvider()).get()).build());
			
		return val;
	}


}
