# Java Actor System

This is a simple actor system implemented for the purpose of learning. It is not intended to be used in production.

Java 19 is required to build and run the project. Preview features are used.

## Usage

### Example Echo Actor

```java
public class MyActor extends AbstractActor {
    @Override
    public void process(Message<?> msg) {
        ActorRef from = msg.getSender();
        if (from != null) {
            msg.setSender(this);
            msg.setSenderName(name);
            from.send(msg);
        }
    }
}

public class Main {
    public static void main(String[] args) {
        ActorProxy proxy = new ActorContext.spawnActor("my-actor", new MyActor());
        proxy.send(new Message<>("Hello World!"));
        System.out.println(proxy.<Message<String>>receive().getBody());
    }
}
```

## Building

To build the project, run `./gradlew build`. This will build the project and run the tests.

## Tests

The tests are written using JUnit 5. To run the tests, run `./gradlew test`. 
This will run the tests and generate a test report in `build/reports/tests/test/index.html`.

To run the tests with code coverage, run `./gradlew jacocoTestReport`.
This will generate a report in `build/reports/jacoco/test/html/index.html`.

## Documentation

The documentation is written using Javadoc. To generate the documentation, run `./gradlew javadoc`.
This will generate the documentation in `build/docs/javadoc/index.html`.

## Some benchmarks

A simple benchmark is included in the project. The benchmark is run using JMH.
To run the benchmark, run `./gradlew jmh`. This will run the benchmark and generate a result file in
`build/results/jmh/results.txt`.

The benchmark measures the time it takes to process 100 entire rounds in a ring of 100 `RingActor` actors.

## RPC

In the `rpc` package, there is an RPC api to use the actor system remotely. The RPC api is implemented using
XMLRPC and JSONRPC. The RPC api is not intended to be used in production, as underlying libraries
([Apache XMLRPC](https://ws.apache.org/xmlrpc/) and [JSONRPC](https://github.com/briandilley/jsonrpc4j))
are outdated and have known vulnerabilities (see [CVE report for Apache XMLRPC](https://www.cvedetails.com/vulnerability-list/vendor_id-45/product_id-41217/Apache-Xml-rpc.html),
[CVE report for JSONRPC](https://mvnrepository.com/artifact/com.github.briandilley.jsonrpc4j/jsonrpc4j/1.6)).

### Running the RPC server

To start the RPC server, run `./gradlew appRun`. This will start the XMLRPC and JSONRPC servlets (using tomcat 9)
on port 8080, accessible at `http://localhost:8080/xmlrpc` and `http://localhost:8080/jsonrpc`, respectively.

### Trying out the RPC server

To try out the RPC server, you can use the [Actor System UI](https://github.com/bystepii/actor-system-ui).
