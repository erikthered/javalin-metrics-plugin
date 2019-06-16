package com.github.erikthered.javalin.plugin;

import static org.assertj.core.api.Assertions.assertThat;

import io.javalin.Javalin;
import org.eclipse.jetty.client.HttpClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MetricsPluginTest {

  private Javalin javalin;

  @Before
  public void setup() {
    javalin = Javalin.create()
        .register(new MetricsPlugin())
        .get("/test/1", ctx -> ctx.result("Test response 1"))
        .get("/test/2", ctx -> ctx.result("Test response 2"))
        .get("/test/3", ctx -> ctx.result("Test response 3"))
        .start(7777);
  }

  @After
  public void teardown() {
    javalin.stop();
  }

  @Test
  public void testRequestsAreCountedProperly() throws Exception {
    HttpClient client = new HttpClient();
    client.start();
    client.GET("http://localhost:7777/test/1").getStatus();
    client.GET("http://localhost:7777/test/2").getStatus();
    client.GET("http://localhost:7777/test/3").getStatus();
    assertThat(MetricsRegistry.STATS.size()).isEqualTo(3);
    client.stop();
  }

  @Test
  public void testMetricsRequestsAreNotCounted() {

  }
}
