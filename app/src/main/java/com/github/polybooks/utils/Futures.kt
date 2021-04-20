package com.github.polybooks.utils

import java.util.*
import java.util.concurrent.CompletableFuture

fun <T> listOfFuture2FutureOfList(listOfFuture : List<CompletableFuture<T>>) : CompletableFuture<List<T>> {
    val futureOfEmptyList = CompletableFuture.completedFuture<List<T>>(Collections.emptyList())
    return listOfFuture.fold(futureOfEmptyList) { acc, curr ->
            acc.thenCombine(curr) { listAcc, currElem ->
                listAcc.plus(currElem)
            }
        }
}