package com.peterfranza.paulbunyan4j.modules.impls;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.peterfranza.paulbunyan4j.LoggingClient;

import com.peterfranza.paulbunyan4j.annotations.LoggableEvent;
import com.peterfranza.paulbunyan4j.modules.LoggingClientModule;

public class DefaultLoggingClientSenderTest {

	@Test
	public void test() {
		
		Injector i = Guice.createInjector(new LoggingClientModule("127.0.0.1", 9546, "DefaultLoggingClientSenderTest", "1", "localhost"));
		
		LoggingClient.LoggingSender m = i.getInstance(DefaultLoggingClientSender.class);
		assertNotNull(m);
		assertEquals(0, m.getMessageCount());
		i.getInstance(DefaultLoggingClientSenderTestSample.class).callable();
		assertEquals(1, m.getMessageCount());
		
	}
	
	public static class DefaultLoggingClientSenderTestSample {
		
		@LoggableEvent()
		void callable() {}
		
	}

}
