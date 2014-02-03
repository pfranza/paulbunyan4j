package com.peterfranza.paulbunyan4j.modules.impls;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.peterfranza.paulbunyan4j.LoggingClient;
import com.peterfranza.paulbunyan4j.messages.Messages.LoggingMessage;

@Singleton
public class LocalLoggingSender implements LoggingClient.LoggingSender {

	private long messageCount = 0;
	@Inject DefaultLoggingClientReceiver recv;
	
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
		// TODO Auto-generated method stub
		return messageCount;
	}

}
