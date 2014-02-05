package com.peterfranza.paulbunyan4j.modules.impls;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.peterfranza.paulbunyan4j.LoggingClient;
import com.peterfranza.paulbunyan4j.LoggingClient.LoggingWrapper;
import com.peterfranza.paulbunyan4j.messages.Messages.LoggingMessage;

@Singleton
public class LocalLoggingSender implements LoggingClient.LoggingSender {

	private long messageCount = 0;
	@Inject DefaultLoggingClientReceiver recv;
	@Inject Provider<LoggingWrapper> wrapperProvider;

	{
		System.out.println("Using LocalLoggingSender");
	}
	
	@Override
	public void send(LoggingMessage message) {
		recv.processMessage(message);
		messageCount += 1;
	}

	@Override
	public long getMessageCount() {
		return messageCount;
	}

	@Override
	public LoggingWrapper createWrapper() {
		return wrapperProvider.get();
	}

}
