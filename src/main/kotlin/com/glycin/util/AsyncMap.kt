package com.glycin.util

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

suspend inline fun <T, R> Iterable<T>.asyncMap(context: CoroutineContext = EmptyCoroutineContext, crossinline transform: suspend (T) -> R): List<R> =
    withContext(context) {
        map { async { transform(it) } }.awaitAll()
    }

suspend inline fun <T, R> Iterable<T>.asyncFlatMap(context: CoroutineContext = EmptyCoroutineContext, crossinline transform: suspend (T) -> List<R>): List<R> =
    withContext(context) {
        map { async { transform(it) } }.awaitAll().flatten()
    }