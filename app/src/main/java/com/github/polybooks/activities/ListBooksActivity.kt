package com.github.polybooks.activities

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import com.github.polybooks.R
import com.github.polybooks.adapter.database.BooksAdapter
import com.github.polybooks.core.Book
import com.github.polybooks.database.BookQuery
import com.github.polybooks.database.Database
import com.github.polybooks.utils.GlobalVariables.EXTRA_BOOKS_QUERY
import com.github.polybooks.utils.setupNavbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.concurrent.CompletableFuture

/**
 * Activity to list Books
 */
class ListBooksActivity: ListActivity<Book>() {

    private val bookDB = Database.bookDatabase

    override fun adapter(list: List<Book>): RecyclerView.Adapter<*> {
        return BooksAdapter(list)
    }

    override fun setNavBar() {
        val navBarListener: BottomNavigationView.OnNavigationItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.home -> {
                        startActivity(Intent(this, MainActivity::class.java))
                        true
                    }
                    R.id.sales -> {
                        startActivity(Intent(this, ListSalesActivity::class.java))
                        true
                    }
                    R.id.user_profile -> {
                        startActivity(Intent(this, LoginActivity::class.java))
                        true
                    }
                    else -> true
                }
            }
        setupNavbar(findViewById(R.id.bottom_navigation), this, R.id.books, navBarListener)
    }

    override fun getElements(): CompletableFuture<List<Book>> {
        return intent.getSerializableExtra(EXTRA_BOOKS_QUERY)
            ?.let {
                bookDB.execute(it as BookQuery)
            }
            ?: bookDB.listAllBooks()
    }

    override fun onFilterButtonClick() {
        startActivity(Intent(this, FilteringBooksActivity::class.java))
    }

    override fun getTitleText(): String {
        return getString(R.string.books)
    }
}