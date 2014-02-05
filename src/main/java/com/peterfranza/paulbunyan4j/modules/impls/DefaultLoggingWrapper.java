package com.peterfranza.paulbunyan4j.modules.impls;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.common.base.Stopwatch;
import com.peterfranza.paulbunyan4j.LoggingClient;
import com.peterfranza.paulbunyan4j.LoggingClient.LoggingSender;
import com.peterfranza.paulbunyan4j.LoggingClient.LoggingWrapper;
import com.peterfranza.paulbunyan4j.LoggingClient.TimedWrapper;
import com.peterfranza.paulbunyan4j.messages.Messages.LoggingMessage;

public class DefaultLoggingWrapper implements LoggingClient.LoggingWrapper {

	@Inject @Named("applicationName")
	private String applicationName = "<UnknownApplication>";
	
	@Inject @Named("applicationId")
	private String applicationId = "<UnknownID>";
	
	@Inject @Named("hostname")
	private String hostname = "<UnknownHost>";

	
	private String eventName = "<UnknownEvent>";
	private String username = "<UnknownUser>";
	
	@Inject LoggingSender sender;

	@Override
	public <T> T execute(TimedWrapper<T> runnable) {
		try {
			long startTime = System.currentTimeMillis();
			Stopwatch watch = Stopwatch.createStarted();
			T val = runnable.execute();
			watch.stop();

			sender.send(LoggingMessage.newBuilder()
					.setApplicationid(applicationName)
					.setApplicationinstanceid(applicationId)
					.setHostname(hostname)
					.setTimestamp(startTime)
					.setLabel(eventName)
					.setDuration(watch.elapsed(TimeUnit.MILLISECONDS))
					.setUsername(username).build());

			return val;
		} catch(Throwable e) {
			throw new RuntimeException(e);
		}
	}


	@Override
	public LoggingWrapper setApplicationName(String v) {
		this.applicationName = v;
		return this;
	}


	@Override
	public LoggingWrapper setEventName(String v) {
		this.eventName = v;
		return this;
	}


	@Override
	public LoggingWrapper setUsername(String v) {
		username = v;
		return this;
	}


	@Override
	public LoggingWrapper setApplicationId(String v) {
		applicationId = v;
		return this;
	}


	@Override
	public LoggingWrapper setApplicationHostname(String v) {
		hostname = v;
		return this;
	}
	
}
