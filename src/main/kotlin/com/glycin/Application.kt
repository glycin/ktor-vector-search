package com.glycin

import com.glycin.icon.IconService
import com.glycin.weaviate.WeaviateRepository
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json
import org.slf4j.event.Level

fun main() {
    embeddedServer(Netty, port = 1337, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {

    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = false
            ignoreUnknownKeys = true
        })
    }

    install(CallLogging) {
        level = Level.WARN
    }

    val repository = WeaviateRepository()
    val iconService = IconService(repository)

    configureRouting(iconService)
}
