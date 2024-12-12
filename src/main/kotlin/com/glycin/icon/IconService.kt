package com.glycin.icon

import com.glycin.util.asyncFlatMap
import com.glycin.weaviate.WeaviateIcon
import com.glycin.weaviate.WeaviateRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.File

private val LOG = KotlinLogging.logger {}

class IconService(
    private val weaviateRepository: WeaviateRepository,
) {
    fun loadImagesInDb(imageDirectory: String): Boolean {
        LOG.info { "Loading in images from $imageDirectory" }
        val directory = File(imageDirectory)
        val uris = directory
            .walk()
            .filter { it.isFile && it.extension == "png" }
            .map { it.toURI() }
            .toList()
        LOG.info { "Finished in images from $imageDirectory" }
        return weaviateRepository.batchAddImages(uris)
    }

    fun searchImage(text: String): List<Icon> {
        return weaviateRepository.searchImageNearText(text, 10).map {
            it.toIcon()
        }
    }

    suspend fun searchImagesAsync(texts: List<String>): List<Icon> = withContext (Dispatchers.IO + SupervisorJob()) {
        texts.asyncFlatMap { text ->
            weaviateRepository.searchImageNearText(text, 10).map {
                it.toIcon()
            }
        }
    }

    fun searchImagesFlow(texts: List<String>): Flow<Icon> = channelFlow {
        texts.asyncFlatMap { text ->
            weaviateRepository.searchImageNearText(text, 10).map {
                send(it.toIcon())
            }
        }
    }.flowOn(Dispatchers.IO)

    private fun WeaviateIcon.toIcon(): Icon = Icon(
        text = text,
        image = image,
    )
}