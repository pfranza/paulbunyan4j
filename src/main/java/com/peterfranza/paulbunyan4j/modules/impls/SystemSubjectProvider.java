package com.peterfranza.paulbunyan4j.modules.impls;

import javax.inject.Singleton;

import com.peterfranza.paulbunyan4j.LoggingClient;

@Singleton
public class SystemSubjectProvider implements LoggingClient.SubjectProvider {

	public String get() {
		return "system";
	}

}
