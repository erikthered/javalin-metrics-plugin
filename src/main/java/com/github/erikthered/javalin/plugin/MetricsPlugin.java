package com.github.erikthered.javalin.plugin;

import io.javalin.Extension;
import io.javalin.Javalin;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetricsPlugin implements Extension {

  Logger logger = LoggerFactory.getLogger("metrics");

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

    app.get("/metrics", ctx -> {
      Collection<RequestStatistic> stats = MetricsRegistry.STATS;
      List<Long> avgTimes = stats.stream().map(RequestStatistic::getDuration)
          .collect(Collectors.toList());
      Optional<Long> avgTime = avgTimes.stream().reduce(Long::sum)
          .map(sum -> sum / avgTimes.size());
      List<Integer> avgSizes = stats.stream().map(RequestStatistic::getSizeInBytes)
          .collect(Collectors.toList());
      Optional<Integer> avgSize = avgSizes.stream().reduce(Integer::sum)
          .map(sum -> sum / avgSizes.size());
      ctx.result(String
          .format("Number of requests: %s, Avg time: %s ms, Avg response size: %s bytes", stats.size(),
              avgTime.orElse(0L), avgSize.orElse(0)));
    });
  }
}
