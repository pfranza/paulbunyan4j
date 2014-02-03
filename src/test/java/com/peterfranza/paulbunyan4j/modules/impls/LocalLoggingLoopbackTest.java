package com.peterfranza.paulbunyan4j.modules.impls;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.peterfranza.paulbunyan4j.LoggingClient;
import com.peterfranza.paulbunyan4j.messages.Messages.LoggingMessage;
import com.peterfranza.paulbunyan4j.modules.LoggingCentralModule;
import com.peterfranza.paulbunyan4j.modules.impls.DefaultLoggingClientSenderTest.DefaultLoggingClientSenderTestSample;

public class LocalLoggingLoopbackTest {

	@Test
	public void test() throws Exception {
		final Injector i = Guice.createInjector(new LoggingCentralModule(9876, "DefaultLoggingClientSenderTest", "1", "localhost"));
		
		DefaultLoggingClientReceiver cRcv = i.getInstance(DefaultLoggingClientReceiver.class);
		LoggingClient.LoggingSender m = i.getInstance(LoggingClient.LoggingSender.class);
		assertNotNull(m);
		assertNotNull(cRcv);
		Thread.sleep(50);
		
		assertEquals(m.getMessageCount(), cRcv.getMessageCount());
	
		i.getInstance(DefaultLoggingClientSenderTestSample.class).callable();
		
		Thread.sleep(500);
		assertEquals(1, cRcv.getMessageCount());
		assertEquals(m.getMessageCount(), cRcv.getMessageCount());
		assertEquals(0, cRcv.getRemoteMessageCount());
		
	}

}
