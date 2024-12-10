package com.glycin

import com.glycin.icon.IconDirectoryPath
import com.glycin.icon.IconSearchListBody
import com.glycin.icon.IconService
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val LOG = KotlinLogging.logger {}

fun Application.configureRouting(
    iconService: IconService
) {
    routing {
        route("/icon") {
            get("/{searchText}") {
                val searchText = call.parameters["searchText"]
                if(searchText.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }

                call.respond(iconService.searchImage(searchText))
            }

            post("/with_list") {
                try {
                    val texts = call.receive<IconSearchListBody>()
                    call.respond(iconService.searchImagesAsync(texts.searchTexts))
                } catch(e: JsonConvertException) {
                    LOG.info { "Couldn't parse IconSearchListBody: ${e.message}" }
                    call.respond(HttpStatusCode.BadRequest)
                }
            }

            post("/with_list/flow") {
                try {
                    val texts = call.receive<IconSearchListBody>()
                    call.respondTextWriter(ContentType.Application.Json) {
                        iconService.searchImagesFlow(texts.searchTexts).collect { icon ->
                            write(Json.encodeToString(icon))
                            write("\n")
                        }
                    }
                } catch(e: JsonConvertException) {
                    LOG.info { "Couldn't parse IconSearchListBody: ${e.message}" }
                    call.respond(HttpStatusCode.BadRequest)
                }
            }

            post("/ingest/directory") {
                try {
                    val path = call.receive<IconDirectoryPath>()
                    call.respond(iconService.loadImagesInDb(path.path))
                } catch (e: JsonConvertException) {
                    LOG.info { "Couldn't parse IconDirectoryPath: ${e.message}" }
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
        }
    }
}
