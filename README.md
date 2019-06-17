# Javalin Simple Metrics Plugin

[![Build Status](https://travis-ci.com/erikthered/javalin-metrics-plugin.svg?branch=master)](https://travis-ci.com/erikthered/javalin-metrics-plugin)

This is a plugin for [Javalin](https://github.com/tipsy/javalin) to track some basic HTTP 
request/response metrics. It is written using a mix of Java and Kotlin.

Currently the following metrics are being recorded:
- Request time
- Response size

A unique request ID is also generated for each request, which is set in the `Request-Id` header of
the HTTP response.

The plugin also registers several routes related to metrics:
- `/metrics` displays a simple Thymeleaf page for viewing aggregated metrics (min, max and avg 
values for time and size)
- `/metrics/:request-id` will return the duration and sizeInBytes for an individual request in json
format

## Usage

The plugin can be built as a jar using `./gradlew assemble` and added to your project.

Simply register the plugin during creation of your Javalin application:

```java
public class App {
    public static void main(String[] args) {
        Javalin app = Javalin.create()
          .register(new MetricsPlugin())
          .start(7000);
        app.get("/", ctx -> ctx.result("Hello World"));
    }
}
```

Check out [ExampleApp.kt](src/main/java/com/github/erikthered/javalin/example/ExampleApp.kt) for an
example application.

## Shortcomings/Areas for Improvement

- Path matching in the used version of Javalin (2.8.0) doesn't seem to work well with more complex
regular expressions, so I was unable to exclude calls to the `/metrics` endpoint itself from metrics
collection.
- There is no bounding on the map holding the recorded statistics so memory consumption could
be an issue for long running applications. It would be advisable to evaluate using a ring buffer for
storage in order to automatically age out the oldest entries.
- The metrics page is extremely basic and could use some polish.
- The plugin isn't currently being published to a Maven repo so bringing this into your own project 
isn't the smoothest experience.