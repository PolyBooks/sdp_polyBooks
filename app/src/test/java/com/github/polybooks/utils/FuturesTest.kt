package com.github.polybooks.utils

import junit.framework.AssertionFailedError
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionException

class FuturesTest {

    @Test
    fun emptyList2EmptyList() {
        val future = listOfFuture2FutureOfList(listOf<CompletableFuture<String>>())
        assertEquals(listOf<String>(), future.get())
    }

    @Test
    fun noFail2NoFail() {
        val list = listOf(1, 2, 3, 4).map { CompletableFuture.completedFuture(it) }
        val future = listOfFuture2FutureOfList(list)
        assertEquals(listOf(1, 2, 3, 4), future.get())
    }

    @Test
    fun oneFail2Fail() {
        val failed = CompletableFuture<Int>()
        failed.completeExceptionally(Exception())
        val list = listOf(1, 2, 3, 4).map {
            if (it == 3) failed
            else CompletableFuture.completedFuture(it)
        }
        val future = listOfFuture2FutureOfList(list)
        try {
            future.join()
        } catch (e: CompletionException) {
            return
        }
        throw AssertionFailedError("Expected future to be completed exceptionally")
    }

}