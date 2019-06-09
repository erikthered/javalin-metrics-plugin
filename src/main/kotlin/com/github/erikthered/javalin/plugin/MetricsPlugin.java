package com.github.erikthered.javalin.plugin;

import io.javalin.Extension;
import io.javalin.Javalin;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetricsPlugin implements Extension {

  Logger logger = LoggerFactory.getLogger("metrics");

  @Override
  public void registerOnJavalin(Javalin app) {
    app.before(ctx -> { // Start timing the request
      long startTime = System.currentTimeMillis();
      logger.debug("Received call to {} at {}", ctx.path(), startTime);
      ctx.attribute("request.start", startTime);
    }).after(ctx -> { // Calculate request body size
      InputStream responseBody = ctx.resultStream();
      responseBody.mark(0);
      int length = IOUtils.toByteArray(responseBody).length;
      responseBody.reset();
      logger.debug("Response body size {} bytes", length);
    }).after(ctx -> { // Stop timing request and calc total time
      long endTime = System.currentTimeMillis();
      logger.debug("Responded to call to {} at {}, took {}ms", ctx.path(), endTime,
          endTime - (long) ctx.attribute("request.start"));
    });
  }
}
