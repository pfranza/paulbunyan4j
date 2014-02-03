package com.peterfranza.paulbunyan4j.modules;

import java.util.concurrent.TimeUnit;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.google.inject.util.Modules;
import com.peterfranza.paulbunyan4j.LoggingClient;
import com.peterfranza.paulbunyan4j.modules.impls.DefaultLogStatisticsManager;
import com.peterfranza.paulbunyan4j.modules.impls.LocalLoggingSender;

public class LoggingCentralModule extends AbstractModule {

	private String applicationName;
	private String applicationInstanceId;
	private String applicationHostname;
	
	private int servicePort = 9876;
	
	public LoggingCentralModule(int servicePort, String applicationName, String applicationInstanceId,
			String applicationHostname) {
		super();
		this.servicePort = servicePort;
		this.applicationName = applicationName;
		this.applicationInstanceId = applicationInstanceId;
		this.applicationHostname = applicationHostname;
	}
	
	@Override
	protected void configure() {
		install(Modules.override(new LoggingClientModule("127.0.0.1", servicePort, applicationName, applicationInstanceId, applicationHostname)).with(new AbstractModule(){
			@Override
			protected void configure() {
				bind(LoggingClient.LoggingSender.class).to(LocalLoggingSender.class);
			}
		}));
		bind(TypeLiteral.get(Long.class)).annotatedWith(Names.named("message_retention_time")).toInstance(TimeUnit.MILLISECONDS.convert(11, TimeUnit.MINUTES));
		bind(LoggingClient.LoggingStatistics.class).to(DefaultLogStatisticsManager.class);
		
	}

}
