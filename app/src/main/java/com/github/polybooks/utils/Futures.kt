package com.github.polybooks.utils

import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionException

fun <T> listOfFuture2FutureOfList(listOfFuture: List<CompletableFuture<T>>): CompletableFuture<List<T>> {
    val futureOfEmptyList = CompletableFuture.completedFuture<List<T>>(Collections.emptyList())
    return listOfFuture.fold(futureOfEmptyList) { acc, curr ->
        acc.thenCombine(curr) { listAcc, currElem ->
            listAcc.plus(currElem)
        }
    }
}

fun unwrapException(futureException: Throwable): Throwable {
    return if (futureException is CompletionException && futureException.cause != null) futureException.cause!!
    else futureException
}