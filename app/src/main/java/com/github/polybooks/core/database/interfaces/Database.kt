package com.github.polybooks.core.database.interfaces

import com.github.polybooks.core.database.InterestDatabase

/**
 * A Database aggregates the functionality of Book/Sale/Interest Databases in one place
 * */
interface Database {

    /**
     * The instance of a Book Database associated with this Database
     * */
    val bookDatabase : BookDatabase

    /**
     * The instance of a Sale Database associated with this Database
     * */
    val saleDatabase : SaleDatabase

    /**
     * The instance of a Interest Database associated with this Database
     * */
    val interestDatabase : InterestDatabase

}



