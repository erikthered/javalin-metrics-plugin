package com.github.erikthered.javalin

import com.github.erikthered.javalin.plugin.MetricsPlugin
import io.javalin.Javalin

class App {
    val greeting: String
        get() {
            return "Hello world."
        }
}

fun main() {
    Javalin.create()
            .register(MetricsPlugin())
            .get("/") { ctx -> ctx.result(App().greeting) }
            .start(7000)
}
