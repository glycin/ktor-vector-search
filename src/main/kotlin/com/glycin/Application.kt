package com.glycin

import com.glycin.icon.IconService
import com.glycin.weaviate.WeaviateRepository
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 1337, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {

    val repository = WeaviateRepository()
    val iconService = IconService(repository)

    configureRouting(iconService)
}
