package com.peterfranza.paulbunyan4j.modules;

import static com.google.inject.matcher.Matchers.annotatedWith;
import static com.google.inject.matcher.Matchers.any;

import java.net.InetAddress;

import com.google.common.net.InetAddresses;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.peterfranza.paulbunyan4j.LoggingClient;

import com.peterfranza.paulbunyan4j.annotations.LoggableEvent;
import com.peterfranza.paulbunyan4j.annotations.handlers.LoggableEventHandler;
import com.peterfranza.paulbunyan4j.modules.impls.DefaultLoggingClientSender;

public class LoggingClientModule extends AbstractModule {

	private String serviceAddress;
	private int servicePort = 9876;
	
	private String applicationName;
	private String applicationInstanceId;
	private String applicationHostname;
	
//	InetAddress.getLocalHost().getHostName()
	
	public LoggingClientModule(String serviceAddress, int servicePort,
			String applicationName, String applicationInstanceId,
			String applicationHostname) {
		super();
		this.serviceAddress = serviceAddress;
		this.servicePort = servicePort;
		this.applicationName = applicationName;
		this.applicationInstanceId = applicationInstanceId;
		this.applicationHostname = applicationHostname;
	}
	
	@Override
	protected void configure() {
		bind(TypeLiteral.get(String.class)).annotatedWith(Names.named("hostname")).toInstance(applicationHostname);
		bind(TypeLiteral.get(String.class)).annotatedWith(Names.named("applicationName")).toInstance(applicationName);
		bind(TypeLiteral.get(String.class)).annotatedWith(Names.named("applicationId")).toInstance(applicationInstanceId);
		bind(TypeLiteral.get(Integer.class)).annotatedWith(Names.named("servicePort")).toInstance(servicePort);
		bind(TypeLiteral.get(String.class)).annotatedWith(Names.named("serviceAddress")).toInstance(serviceAddress);
		bind(TypeLiteral.get(InetAddress.class)).annotatedWith(Names.named("LoggingEndpoint")).toInstance(InetAddresses.forString(serviceAddress));
		
		
		
		bindInterceptor(any(), annotatedWith(LoggableEvent.class), inject(new LoggableEventHandler()));
		
		bind(LoggingClient.LoggingSender.class).to(DefaultLoggingClientSender.class);
	}

	

	private <T> T inject(T obj) {
		requestInjection(obj);
		return obj;
	}
}
