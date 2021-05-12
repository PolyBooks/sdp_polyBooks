package com.github.polybooks.database

import java.util.concurrent.CompletableFuture

/**
 * Queries are Object that allow to make queries to a database.
 * */
interface Query<T> {

    /**
     * Execute the query and return all the results in a Future.
     * */
    fun getAll() : CompletableFuture<List<T>>

    /**
     * Get the results in batches of at most n books.
     * */
    fun getN(n : Int, page : Int) : CompletableFuture<List<T>>

    /**
     * Get how many entries match this query
     * */
    fun getCount() : CompletableFuture<Int>
}
