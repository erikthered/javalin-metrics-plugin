package com.github.erikthered.javalin.plugin;

import static io.javalin.rendering.template.TemplateUtil.model;

import io.javalin.Extension;
import io.javalin.Javalin;
import java.io.InputStream;
import java.util.IntSummaryStatistics;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetricsPlugin implements Extension {

  private static final Logger logger = LoggerFactory.getLogger("metrics");

  private final Map<String, RequestStatistic> stats;

  private static MetricsPlugin instance;

  private MetricsPlugin() {
    this.stats = new ConcurrentHashMap<>();
  }

  public static MetricsPlugin getInstance() {
    return (instance == null)
        ? instance = new MetricsPlugin()
        : instance;
  }

  protected static Map<String, RequestStatistic> stats() {
    return instance.stats;
  }

  @Override
  public void registerOnJavalin(Javalin app) {
    app.before(ctx -> {
      // Start timing the request
      long startTime = System.currentTimeMillis();
      ctx.attribute("request.start", startTime);
    }).after(ctx -> {
      // Calculate request body size
      InputStream responseBody = ctx.resultStream();
      int length = 0;
      if(responseBody != null) {
        responseBody.mark(0);
        length = IOUtils.toByteArray(responseBody).length;
        responseBody.reset();
      }

      // Add a unique id per request
      String requestId = UUID.randomUUID().toString();
      ctx.header("Request-Id", requestId);

      // Stop timing request and calc total time
      long endTime = System.currentTimeMillis();
      long durationMs = endTime - (long) ctx.attribute("request.start");
      logger
          .debug("Request {} to {} took {}ms and had response size {} bytes", requestId, ctx.path(),
              durationMs, length);

      // Record stat values
      stats.put(requestId, new RequestStatistic(durationMs, length));
    });

    // Register page for displaying metrics info
    app.get("/metrics", ctx -> {
      LongSummaryStatistics times = stats.values().stream()
          .mapToLong(RequestStatistic::getDuration).summaryStatistics();
      IntSummaryStatistics sizes = stats.values().stream()
          .mapToInt(RequestStatistic::getSizeInBytes)
          .summaryStatistics();
      ctx.render("public/metrics.html",
          model("requestTimes", times,
              "responseSizes", sizes)
      );
    });

    app.get("/metrics/:request-id", ctx -> {
      String requestId = ctx.pathParam("request-id");
      if (stats.containsKey(requestId)) {
        ctx.json(stats.get(requestId));
      } else {
        ctx.status(404);
      }
    });
  }
}
