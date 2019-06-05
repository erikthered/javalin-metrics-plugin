package com.github.erikthered.javalin

import io.javalin.Javalin

class App {
    val greeting: String
        get() {
            return "Hello world."
        }
}

fun main(args: Array<String>) {
    val app = Javalin.create().start(7000)
    app.get("/") { ctx -> ctx.result(App().greeting) }
}
