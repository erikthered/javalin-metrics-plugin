package com.github.erikthered.javalin.plugin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MetricsRegistry {

  public static final Map<String, RequestStatistic> STATS = new ConcurrentHashMap<>();

}
