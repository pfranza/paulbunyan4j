package com.peterfranza.paulbunyan4j.modules.impls;

import javax.inject.Provider;

public class SystemSubjectProvider implements Provider<String>{

	public String get() {
		return "system";
	}

}
