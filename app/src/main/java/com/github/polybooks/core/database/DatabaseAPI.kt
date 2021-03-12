package com.github.polybooks.core.database

import com.github.polybooks.core.*
import java.util.concurrent.CompletableFuture

/**
 * Provides the API for a database. Allows to retrieve information in the database
 * about Books, Sales, and Interests.
 *
 * Books and Sales can be queried with dedicated methods, but more generally,
 * one can use the queryBooks()/querySales() methods to build a more fine tuned query and execute them.
 *
 * The list of what interests exist can be accessed with the dedicated methods. As for the interests
 * of the user, there is a distinction between the local and remote users. The interest of the user
 * should be stored in the local Interests, but an authenticated user can save their interests on the
 * cloud. The database provides methods for saving/restoring local interests to/from the cloud.
 * */
interface Database {

    /**
     * Creates a new query for Books. It originally matches all books.
     * */
    fun queryBooks() : BookQuery

    /**
     * Create a new query for Sales. It originally matches all sales.
     * */
    fun querySales() : SaleQuery

    /**
     * List all the Fields in the database.
     * */
    fun listAllFields() : CompletableFuture<List<Field>>

    /**
     * List all the Semesters in the database.
     * */
    fun listAllSemesters() : CompletableFuture<List<Semester>>

    /**
     * List all the Courses in the database.
     * */
    fun listAllCourses() : CompletableFuture<List<Field>>

    /**
     * List all the interests in the database.
     * */
    fun listAllInterests() : CompletableFuture<Triple<List<Field>, List<Semester>, List<Course>>>
        = TODO("It can be implemented from the previous functions")

    /**
     * Get the interests of the local user.
     * */
    fun getLocalInterests() : CompletableFuture<Triple<List<Field>, List<Semester>, List<Course>>>

    /**
     * Sets the interests of the local user.
     * @return A Future to receive confirmation of success/failure asynchronously
     * */
    fun setLocalInterests(interests : List<Interest>) : CompletableFuture<Nothing>

    /**
     * Get the interests of the given User as stored on the cloud.
     * TODO: Might need to add an authentication token to restrict authenticated users to only access their interests.
     * */
    fun getInterestsFromCloud(user : User) : CompletableFuture<Triple<List<Field>, List<Semester>, List<Course>>>

    /**
     * Save the interests of the local user to the cloud to the given user's account.
     * @return A Future to receive confirmation of success/failure asynchronously
     * TODO: Might need to add an authentication token to restrict authenticated users to only modify their interests.
     * */
    fun updateCloudInterestsFromLocal(user : User) : CompletableFuture<Nothing>

    /**
     * Restore the interests of the local user from the cloud given the user's account:
     * @return A Future to receive confirmation of success/failure asynchronously
     * TODO: Might need to add an authentication token to restrict authenticated users to only access their interests.
     * */
    fun updateLocalInterestsFromCloud(user : User) : CompletableFuture<Nothing>

    /**
     * Get all the books in the database
     * */
    fun listAllBooks() : CompletableFuture<List<Book>> = queryBooks().getAll()

    /**
     * Get all the sales in the database
     * */
    fun listAllSales() : CompletableFuture<List<Sale>> = querySales().getAll()

    /**
     * Get data about a Book from the database given it's ISBN13
     * */
    fun getBook(isbn13 : String) : CompletableFuture<Book>
        = TODO("It can be implemented from the previous functions")

    /**
     * A method for getting books by batches of at most N books. The batches are indexed by ordered pages.
     * @param numberOfBooks The maximum number of books per page
     * @param page The index of the page
     * @param ordering The ordering for the pages and books within the pages (see {@link BookOrdering})
     * */
    fun getNBooks(numberOfBooks : Int, page : Int, ordering : BookOrdering) : CompletableFuture<List<Book>>
        = queryBooks().withOrdering(ordering).getN(numberOfBooks, page)

    /**
     * A method for getting sales by batches of at most N sales. The batches are indexed by ordered pages.
     * @param numberOfSales The maximum number of sales per page
     * @param page The index of the page
     * @param ordering The ordering for the pages and sales within the pages (see {@link SaleOrdering})
     * */
    fun getNSales(numberOfSales : Int, page : Int, ordering : SaleOrdering) : CompletableFuture<List<Sale>>
            = querySales().withOrdering(ordering).getN(numberOfSales, page)
    

}

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


/**
 * A BookQuery is a builder for a query to the database that will yield Books.
 * Most methods return themselves for function chaining
 * */
interface BookQuery : Query<Book> {

    /**
     * Set this query to only include books that satisfy the given interests.
     * */
    fun onlyIncludeInterests(interests: Collection<Interest>) : BookQuery

    /**
     * Set this query to only search for books with title that are like the given one.
     * (ignoring other filters)
     * */
    fun searchByTitle(title : String) : BookQuery

    /**
     * Set this query to get the book associated with the given isbn13, if it exists.
     * (ignoring other filters)
     * */
    fun searchByISBN13(isbn13: String) : BookQuery

    /**
     * Set this query to order books with the given ordering.
     * (see {@link BookOrdering})
     * */
    fun withOrdering(ordering : BookOrdering) : BookQuery

}

/**
 * A SaleQuery is a builder for a query to the database that will yield Sales.
 * Most methods return themselves for function chaining.
 * */
interface SaleQuery : Query<Sale> {

    /**
     * Set this query to only include sales that satisfy the given interests.
     * */
    fun onlyIncludeInterests(interests: Collection<Interest>) : SaleQuery

    /**
     * Set this query to only search for sales with book's title that are like the given one.
     * (ignoring other filters)
     * */
    fun searchByTitle(title : String) : SaleQuery

    /**
     *  Set this query to only search for sales in the given states.
     *  (see {@link SaleState})
     * */
    fun searchByState(state : Collection<SaleState>) : SaleQuery

    /**
     * Set this query to only search for sales of books in the given condition.
     * (see {@link BookCondition})
     * */
    fun searchByCondition(condition : Collection<BookCondition>) : SaleQuery

    /**
     * Set this query to only search for sales within the given price range.
     * */
    fun searchByPrice(min : Float, max : Float) : SaleQuery

    /**
     * Set this query to order books with the given ordering.
     * (see {@link BookOrdering})
     * */
    fun withOrdering(ordering : SaleOrdering) : SaleQuery

    /**
     * Set this query to get sales of books associated with the given isbn13.
     * (ignoring other filters)
     * */
    fun searchByISBN13(isbn13: String) : SaleQuery

}

/**
 * Defines an ordering for books. DEFAULT is implementation defined.
 * */
enum class BookOrdering {
    DEFAULT, TITLE_INC, TITLE_DEC,
}

/**
 * Defines an ordering for books. DEFAULT is implementation defined.
 * */
enum class SaleOrdering {
    DEFAULT, TITLE_INC, TITLE_DEC, PRICE_INC, PRICE_DEC, PUBLISH_DATE_INC, PUBLISH_DATE_DEC,
}