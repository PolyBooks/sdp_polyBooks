package com.github.polybooks.activities

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import com.github.polybooks.R
import com.github.polybooks.core.Book
import com.github.polybooks.adapter.database.BooksAdapter
import com.github.polybooks.database.BookSettings
import com.github.polybooks.database.FBBookDatabase
import com.github.polybooks.database.FBSaleDatabase
import com.github.polybooks.database.Query
import com.github.polybooks.utils.GlobalVariables.EXTRA_BOOKS_QUERY_SETTINGS
import com.github.polybooks.utils.setupNavbar
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * Activity to list Books
 */
class ListBooksActivity: ListActivity<Book>() {

    val bookDB = FBBookDatabase.getInstance()

    override fun adapter(list: List<Book>): RecyclerView.Adapter<*> {
        return BooksAdapter(list)
    }

    override fun setNavBar() {
        val navBarListener : BottomNavigationView.OnNavigationItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener{ item ->
                when(item.itemId){
                    R.id.home ->{
                        startActivity(Intent(this, MainActivity::class.java))
                        true
                    }
                    R.id.sales ->{
                        startActivity(Intent(this, ListSalesActivity::class.java))
                        true
                    }
                    R.id.user_profile ->{
                        startActivity(Intent(this, LoginActivity::class.java))
                        true
                    }
                    else -> true
                }
            }
        setupNavbar(findViewById(R.id.bottom_navigation), this, R.id.books, navBarListener)
    }

    override fun getQuery(): Query<Book> {
        return intent.getSerializableExtra(EXTRA_BOOKS_QUERY_SETTINGS)
            ?.let {
                bookDB.queryBooks()
                    .fromSettings(it as BookSettings)
            }
            ?: bookDB.queryBooks()
    }

    override fun onFilterButtonClick() {
        startActivity(Intent(this, FilteringBooksActivity::class.java))
    }

    override fun getTitleText(): String {
        return getString(R.string.books)
    }
}