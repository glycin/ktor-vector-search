package com.glycin.weaviate

import io.github.oshai.kotlinlogging.KotlinLogging
import io.weaviate.client.Config
import io.weaviate.client.WeaviateClient
import io.weaviate.client.v1.data.model.WeaviateObject
import io.weaviate.client.v1.graphql.query.argument.NearTextArgument
import io.weaviate.client.v1.graphql.query.fields.Field
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.net.URI
import java.util.*

private val LOG = KotlinLogging.logger {}

class WeaviateRepository {
    private val client = WeaviateClient(Config("http", "localhost:8080"))

    fun batchAddImages(imagePaths: List<URI>): Boolean {
        if(!client.hasSchemaWithName(WeaviateClassNames.ICON_CLASS)) {
            LOG.info { "Schema ${WeaviateClassNames.ICON_CLASS} doesn't exist!" }
            return false
        }

        val batcher = client.batch().objectsBatcher()
        LOG.info { "Importing bob ross paintings" }

        imagePaths.forEachIndexed { i, uri ->
            val icon = File(uri)
            batcher.withObject(
                WeaviateObject.builder()
                    .className(WeaviateClassNames.ICON_CLASS)
                    .properties(
                        mapOf(
                            "image" to Base64.getEncoder().encodeToString(icon.readBytes()),
                        )
                    )
                    .build()
            )

            if(i % 50 == 0){
                LOG.info { "Flushing batcher at $i" }
                batcher.flush()
            }
        }
        val result = batcher.run()
        LOG.info { "DONE!" }
        return !result.hasErrors()
    }

    fun searchImageNearText(text: String, limit: Int): List<WeaviateIcon> {
        val result = client.graphQL()
            .get()
            .withClassName(WeaviateClassNames.ICON_CLASS)
            .withNearText(
                NearTextArgument.builder()
                    .concepts(arrayOf(text))
                    .distance(0.9f)
                    .build()
            )
            .withLimit(limit)
            .withFields(Field.builder().name("image").build())
            .run()

        if(result.hasErrors()) {
            LOG.error{ "Could not search for ${WeaviateClassNames.ICON_CLASS} and text: $text" }
            return emptyList()
        }

        return Json.encodeToString(result.result.data).let { json ->
            Json.decodeFromString<Data>(json).get.icon ?: emptyList()
        }
    }

    private fun WeaviateClient.hasSchemaWithName(name: String): Boolean = schema().exists().withClassName(name).run().result
}