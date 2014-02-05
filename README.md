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


