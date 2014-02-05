paulbunyan4j
============
__A logging and event timing framework for clustered or distributed systems__

PaulBunyan4J uses protobuf messages over a UDP socket to distribute messages from different applications on the same network so the information can be displayed and managed from a single application.  Making it possible to create a dashboard that shows the state of multiple applications or multiple clusters.

Simple Usage
---------------------
### Setup Central Logging Endpoint

1. Install the Guice Module

        install(new LoggingCentralModule(9876, "LoggingServer", "hostname"));

### Setup Remote Clients

1. Install the Guice Module

        install(new LoggingClientModule("10.10.1.1", 9876, "LoggingClient", "hostname"));

### Annotate Code

Code on both the central and remote clients can be annotated with @LoggableEvent annotations and the label for the event can be overridden in the annotation.

    @LoggableEvent
    public String performAction(? arg0, ? arg1) {
      ...
    }  

    @LoggableEvent("NewEventLabel")
    public String performOtherAction(? arg0, ? arg1) {
      ...
    } 

Advanced Usage
---------------------

If you are running a multiuser enviroment (i.e. hosting webservices)  and you want each specific username attached to the event you can either:

1.  Set a username provider in the annotation

        @LoggableEvent(subjectProvider=NewSubjectProvider.class)
        public String performAction(? arg0, ? arg1) {
           ...
        }  

1.  Change the default provider

		install(Modules.override(new LoggingCentralModule(...)).with(new AbstractModule() {
			@Override
			protected void configure() {
				bind(LoggingClient.SubjectProvider.class).to(NewSubjectProvider.class);
			}
		}));

If you want to override more specific attributes or don't want to use annotations

  2.  Get a reference to the log sender

        @Inject LoggingClient.LoggingSender logger;

  2.  Wrap your executing code in a wrapper

        public String performAction(? arg0, ? arg1) {
          return logger.createWrapper()
                      .setApplicationName("OverrideName")
                      .setEventName("EventName")
                      .setUsername("executor's username")
                      .execute(new TimedWrapper<String>() {
					@Override
					public String execute() throws Throwable {
					        //TODO Do some work in here a return it
						return "OK";
					}
				});
        }  


If you want full control over the message you can 

  3.  Get a reference to the log sender

        @Inject LoggingClient.LoggingSender logger;

  3.  Build a protobuf message

        LoggingMessage msg = LoggingMessage.newBuilder() ....  .build();

  3.  Send it

        logger.send(msg);