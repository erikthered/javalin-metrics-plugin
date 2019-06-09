package com.github.erikthered.javalin.plugin

data class RequestStatistic(
        val id: String,
        val duration: Long,
        val sizeInBytes: Int
)