package com.peterfranza.paulbunyan4j;

import com.peterfranza.paulbunyan4j.messages.Messages;

public interface LoggingClient {

	interface LoggingSender {
		void send(Messages.LoggingMessage message);

		long getMessageCount();
	}
	
}
