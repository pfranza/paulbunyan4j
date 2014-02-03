package com.peterfranza.paulbunyan4j;

import javax.inject.Provider;

import com.google.inject.ImplementedBy;
import com.peterfranza.paulbunyan4j.messages.Messages;
import com.peterfranza.paulbunyan4j.modules.impls.SystemSubjectProvider;

public interface LoggingClient {

	interface LoggingSender {
		void send(Messages.LoggingMessage message);

		long getMessageCount();
	}
	
	interface LoggingStatistics {		
		void getSamples(LoggingStatisticsSamplesVisitor visitor);	
		String getReport();
		int getSamples();
		double getAverage();
		long getLastValue();
	}
	
	interface LoggingStatisticsSamplesVisitor {
		void visit(LoggingStatisticsSample sample);
	}
	
	interface LoggingStatisticsSample {
		long getLastValue();
		long getMaxValue();
		long getMinValue();
		double getTotal();
		double getAverage();
		String getLabel();	
	}
	
	@ImplementedBy(SystemSubjectProvider.class)
	interface SubjectProvider extends Provider<String> {
		String get();
	}
}
