package com.github.polybooks.core


import java.io.Serializable
import java.sql.Timestamp

import java.util.*

/**
 * The Sale class contains all the information about a book on sale.
 * @property book The isbn13 of the book that is being sold
 * @property seller The ID of the seller
 * @property price The price in CHF at which the book is being sold
 * @property condition The condition of the book (see {@link BookCondition})
 * @property publicationDate The date on which the Sale has been issued/published
 * @property state The state of the Sale (see {@link SaleState})
 * */

data class Sale(
        val title : String,
        val seller : Int,
        val price : Float,
        val condition : BookCondition,
        val date : Timestamp,
        val state : SaleState) : Serializable


/**
 * The condition of a book (as in "in great condition").
 * */
enum class BookCondition {
    NEW, GOOD, WORN
}

/**
 * The State of a Sale.
 * Active means that the offer to buy the book is on the table.
 * Retracted means that the book is no longer on sale because the seller retracted the offer.
 * Concluded means that the book has been sold and is therefore no longer on sale.
 * */
enum class SaleState {
    ACTIVE, RETRACTED, CONCLUDED
}