package com.peterfranza.paulbunyan4j.annotations.handlers;

import javax.inject.Inject;
import javax.inject.Named;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.google.inject.Injector;
import com.peterfranza.paulbunyan4j.LoggingClient.LoggingSender;
import com.peterfranza.paulbunyan4j.LoggingClient.TimedWrapper;
import com.peterfranza.paulbunyan4j.annotations.LoggableEvent;

public class LoggableEventHandler implements MethodInterceptor {

	@Inject @Named("applicationName") String applicationName;
	@Inject @Named("applicationId") String applicationId;
	@Inject @Named("hostname") String hostname;
	
	@Inject Injector injector;
	@Inject LoggingSender sender;
	
	public Object invoke(final MethodInvocation invocation) throws Throwable {
		LoggableEvent event = invocation.getMethod().getAnnotation(LoggableEvent.class);
//		long startTime = System.currentTimeMillis();
//		Stopwatch watch = Stopwatch.createStarted();
//		Object val = invocation.proceed();
//		watch.stop();
//		
//		sender.send(LoggingMessage.newBuilder()
//				.setApplicationid(applicationName)
//				.setApplicationinstanceid(applicationId)
//				.setHostname(hostname)
//				.setTimestamp(startTime)
//				.setLabel(event.value().equals("") ? invocation.getMethod().getName() : event.value())
//				.setDuration(watch.elapsed(TimeUnit.MILLISECONDS))
//				.setUsername(injector.getInstance(event.subjectProvider()).get()).build());
//			
//		return val;

		return sender.createWrapper().setApplicationName(applicationName).setApplicationId(applicationId)
				.setApplicationHostname(hostname).setEventName(event.value().equals("") ? invocation.getMethod().getName() : event.value())
				.setUsername(injector.getInstance(event.subjectProvider()).get())
				.execute(new TimedWrapper<Object>() {
					@Override
					public Object execute() throws Throwable {
						return invocation.proceed();
					}
				});
	}


}
