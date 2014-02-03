package com.peterfranza.paulbunyan4j.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Provider;

import com.peterfranza.paulbunyan4j.LoggingClient;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})  
public @interface LoggableEvent {
	String value() default ""; 
	Class<? extends Provider<String>> subjectProvider() default LoggingClient.SubjectProvider.class;
}
