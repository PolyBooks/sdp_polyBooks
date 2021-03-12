package com.github.polybooks.core.database

/**
 * A Database aggregates the functionality of Book/Sale Databases in one place
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

}



