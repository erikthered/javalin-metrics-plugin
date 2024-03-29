package com.github.erikthered.javalin.plugin;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.http.HttpFields;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MetricsPluginTest {

  private Javalin javalin;
  private HttpClient client;

  @Before
  public void setup() throws Exception {
    javalin = Javalin.create()
        .disableStartupBanner()
        .register(MetricsPlugin.getInstance())
        .get("/test/1", ctx -> {
          Thread.sleep(100);
          ctx.result("Test response 1");
        })
        .get("/test/2", ctx -> ctx.result("Test response 2"))
        .get("/test/3", ctx -> ctx.result("Test response 3"))
        .start(7777);
    client = new HttpClient();
    client.start();
  }

  @After
  public void teardown() throws Exception {
    MetricsPlugin.stats().clear();
    client.stop();
    javalin.stop();
  }

  @Test
  public void testRequestsAreCountedProperly() throws Exception {
    client.GET("http://localhost:7777/test/1").getStatus();
    client.GET("http://localhost:7777/test/2").getStatus();
    client.GET("http://localhost:7777/test/3").getStatus();
    assertThat(MetricsPlugin.stats().size()).isEqualTo(3);
  }

  @Test
  public void testRequestIdIsAssigned() throws Exception {
    HttpFields headers = client.GET("http://localhost:7777/test/1").getHeaders();
    assertThat(headers.containsKey("Request-Id")).isTrue();
    assertThat(headers.get("Request-Id")).isNotBlank();
  }

  @Test
  public void testSingleRequestMetricsAreAccessible() throws Exception {
    HttpFields headers = client.GET("http://localhost:7777/test/1").getHeaders();
    assertThat(headers.containsKey("Request-Id")).isTrue();
    String requestId = headers.get("Request-Id");
    assertThat(requestId).isNotBlank();
    byte[] rawResponse = client.GET("http://localhost:7777/metrics/" + requestId).getContent();
    JsonNode jsonResponse = new ObjectMapper().readTree(rawResponse);
    assertThat(jsonResponse.has("duration")).isTrue();
    assertThat(jsonResponse.has("sizeInBytes")).isTrue();
  }

  @Test
  public void accessingMetricsForUnknownRequestShould404() throws Exception {
    int status = client.GET("http://localhost:7777/metrics/foobar").getStatus();
    assertThat(status).isEqualTo(404);
  }

  @Test
  public void testRequestStatsAreNonZero() throws Exception {
    HttpFields headers = client.GET("http://localhost:7777/test/1").getHeaders();
    String requestId = headers.get("Request-Id");
    assertThat(MetricsPlugin.stats().get(requestId).getDuration()).isPositive();
    assertThat(MetricsPlugin.stats().get(requestId).getSizeInBytes()).isPositive();
  }
}
