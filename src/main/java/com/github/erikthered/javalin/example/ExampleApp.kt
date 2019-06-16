package com.github.erikthered.javalin.example

import com.github.erikthered.javalin.plugin.MetricsPlugin
import io.javalin.Javalin

fun main() {
    Javalin.create()
            .register(MetricsPlugin.getInstance())
            .get("/hello") { ctx ->
                ctx.result("Hello world")
            }
            .get("/goodbye") { ctx ->
                ctx.result("Goodbye")
            }
            .start(7000)
}
