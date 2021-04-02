package com.github.polybooks.core.database.implementation

import android.annotation.SuppressLint
import com.github.polybooks.core.Book
import com.github.polybooks.core.Interest
import com.github.polybooks.core.database.interfaces.BookDatabase
import com.github.polybooks.core.database.interfaces.BookOrdering
import com.github.polybooks.core.database.interfaces.BookOrdering.*
import com.github.polybooks.core.database.interfaces.BookQuery
import com.github.polybooks.utils.url2json
import com.google.gson.JsonElement
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.CompletableFuture


private const val ISBN13_FORMAT = """[0-9]{13}"""
private const val ISBN10_FORMAT = """[0-9]{9}[0-9X]"""
private const val ISBN_FORMAT = """($ISBN10_FORMAT)|($ISBN13_FORMAT)"""

private const val API_KEY = "<insert key here>"
private const val BASE_URL = """https://api-eu.hosted.exlibrisgroup.com/primo/v1/search"""
private const val SCOPE = "DiscoveryNetwork"
private const val VID = "41SLSP_EPF:prod"
private const val TAB = "41SLSP_EPF_DN_CI"
private const val TITLE_QUERY = "title,contains,"
private const val ISBN_QUERY = "any,contains,"
private const val QUERY_BASE = "$BASE_URL?format:json&scope=$SCOPE&vid=$VID&tab=$TAB&apikey=$API_KEY"

private const val ISBN_BOOK_SEARCH = 1
private const val TITLE_BOOK_SEARCH = 100


class PrimoBookDatabase(url2json : (String) -> CompletableFuture<JsonElement>) : BookDatabase {

    override fun queryBooks(): BookQuery = PrimoBookQuery()

    private inner class PrimoBookQuery : BookQuery {

        private var ordering = DEFAULT

        private var empty : Boolean = true
        private var title : String? = null
        private var isbn : String? = null

        override fun onlyIncludeInterests(interests: Collection<Interest>): BookQuery {
            System.err.println("Warning: onlyIncludeInterest not fully implemented for PrimoBookQuery")
            this.empty = true
            return this
        }

        override fun searchByTitle(title: String): BookQuery {
            this.empty = false
            this.isbn = null
            this.title = title
            return this
        }

        override fun searchByISBN13(isbn13: String): BookQuery {
            this.empty = false
            this.title = null
            this.isbn = isbn13
            return this
        }

        override fun withOrdering(ordering: BookOrdering): BookQuery {
            this.ordering = ordering
            return this
        }

        @SuppressLint("NewApi")
        override fun getAll(): CompletableFuture<List<Book>> {
            when {
                empty -> return CompletableFuture.completedFuture(Collections.emptyList())
                title != null -> {
                    val url = makeURLfromTitle(title!!, TITLE_BOOK_SEARCH, ordering = ordering)
                    return url2json(url)
                        .thenApply { listBooks(it).map {parseBook(it)} }
                }
                else -> { assert(isbn != null)
                    val cleanISBN = cleanISBN(isbn!!)
                    return if (cleanISBN != null) {
                        val url = makeURLfromISBN(cleanISBN)
                        url2json(url)
                            .thenApply {
                                val bookOption =
                                    findBookWithISBN(it, cleanISBN)
                                    .map{parseBook(it)}
                                opt2list(bookOption)
                            }
                    } else {
                        val future = CompletableFuture<List<Book>>()
                        future.completeExceptionally(Exception("ISBN is not valid"))
                        future
                    }
                }
            }
        }

        override fun getN(n: Int, page: Int): CompletableFuture<List<Book>> {
            TODO("Not yet implemented")
        }

        @SuppressLint("NewApi")
        override fun getCount(): CompletableFuture<Int> {
            when {
                empty -> return CompletableFuture.completedFuture(0)
                title != null -> {
                     return url2json(makeURLfromTitle(title!!, 0))
                         .thenApply { parseCount(it) }
                }
                else -> { assert(isbn != null)
                    val cleanISBN = cleanISBN(isbn!!)
                    return if (cleanISBN != null) {
                        val url = makeURLfromISBN(cleanISBN)
                        url2json(url)
                            .thenApply {
                                if (findBookWithISBN(it, cleanISBN).isPresent)
                                    1 else 0
                            }
                    } else {
                        val future = CompletableFuture<Int>()
                        future.completeExceptionally(Exception("ISBN is not valid"))
                        future
                    }
                }
            }
        }

        private fun cleanISBN(userISBN : String) : String? {
            val regularised = userISBN.replace("[- ]".toRegex(), "")
            return if (!regularised.matches(Regex(ISBN_FORMAT))) null
            else regularised
        }

        @SuppressLint("NewApi")
        private fun makeURLfromTitle(title : String, n : Int, page : Int = 0, ordering: BookOrdering = DEFAULT) : String {
            assert(page >= 0)
            assert(n > 0)
            val offsetString = if (page == 0) "" else "&offset=${n*page}"
            val limitString = "&limit=$n"
            val orderingString =
                if (ordering == TITLE_INC || ordering == TITLE_DEC)
                    "&sort=title"
                else ""
            val queryString = "&q=$TITLE_QUERY${urlEncode(title)}"
            return "$QUERY_BASE$queryString$offsetString$limitString$orderingString"
        }

        @SuppressLint("NewApi")
        private fun makeURLfromISBN(isbn : String) : String {
            val limitString = "&limit=$ISBN_BOOK_SEARCH"
            val queryString = "&q=$ISBN_QUERY$isbn"
            return "$QUERY_BASE$queryString$limitString"
        }

        @SuppressLint("NewApi")
        private fun urlEncode(s : String) = URLEncoder.encode(s, StandardCharsets.UTF_8.toString())

        @SuppressLint("NewApi")
        private fun findBookWithISBN(jsonBook : JsonElement, cleanISBN : String) : Optional<JsonElement> {
            //Since there is no guarantee that a pnx entry contains an ISBN, just return the first book
            return getJsonField(asJsonObject(jsonBook), "docs")
                .map {
                    Optional.ofNullable(asJsonArray(it).toList().firstOrNull())
                }.orElseThrow(cantParseException("docs"))
        }

        @SuppressLint("NewApi")
        private fun listBooks(json: JsonElement) : List<JsonElement> {
            return getJsonField(asJsonObject(json), "docs")
                .map {
                    asJsonArray(it)
                        .toList()
                        .map { bookJSON ->
                            getJsonField(asJsonObject(bookJSON), "pnx")
                                .orElseThrow(cantParseException("docs[n].pnx"))
                        }
                }.orElseThrow(cantParseException("docs"))
        }

        private fun parseBook(jsonBook : JsonElement) : Book {
            TODO("Not yet implemented")
        }

        @SuppressLint("NewApi")
        private fun parseCount(json : JsonElement) : Int {
            return getJsonField(asJsonObject(json), "info")
                .flatMap {
                    getJsonField(asJsonObject(it), "total")
                }.map {
                    asInt(it)
                }.orElseThrow(cantParseException("info.total"))
        }

        @SuppressLint("NewApi")
        private fun <T> opt2list(opt : Optional<T>) : List<T> {
            return if (opt.isPresent) {
                Collections.singletonList(opt.get())
            } else {
                Collections.emptyList<T>()
            }
        }

    }

}