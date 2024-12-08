package com.glycin.icon

import com.glycin.weaviate.WeaviateIcon
import com.glycin.weaviate.WeaviateRepository
import io.github.oshai.kotlinlogging.KotlinLogging
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
        return weaviateRepository.searchImageNearText(text, 2).map {
            it.toIcon()
        }
    }

    private fun WeaviateIcon.toIcon(): Icon = Icon(
        text = text,
        image = image,
    )
}