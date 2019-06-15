package com.github.erikthered.javalin.plugin;

import static io.javalin.rendering.template.TemplateUtil.model;

import io.javalin.Extension;
import io.javalin.Javalin;
import java.io.InputStream;
import java.util.Collection;
import java.util.IntSummaryStatistics;
import java.util.LongSummaryStatistics;
import java.util.UUID;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetricsPlugin implements Extension {

  private static final Logger logger = LoggerFactory.getLogger("metrics");

  @Override
  public void registerOnJavalin(Javalin app) {
    app.before(ctx -> {
      // Start timing the request
      long startTime = System.currentTimeMillis();
      ctx.attribute("request.start", startTime);
    }).after(ctx -> {
      // Calculate request body size
      InputStream responseBody = ctx.resultStream();
      responseBody.mark(0);
      int length = IOUtils.toByteArray(responseBody).length;
      responseBody.reset();

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
      MetricsRegistry.STATS.add(new RequestStatistic(requestId, durationMs, length));
    });

    // Register page for displaying metrics info
    app.get("/metrics", ctx -> {
      Collection<RequestStatistic> stats = MetricsRegistry.STATS;
      LongSummaryStatistics times = stats.stream()
          .mapToLong(RequestStatistic::getDuration).summaryStatistics();
      IntSummaryStatistics sizes = stats.stream().mapToInt(RequestStatistic::getSizeInBytes)
          .summaryStatistics();
      ctx.render("metrics.html",
          model("requestTimes", times,
              "responseSizes", sizes)
      );
    });
  }
}
