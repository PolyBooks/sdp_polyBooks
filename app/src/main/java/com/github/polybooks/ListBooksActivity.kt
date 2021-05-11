package com.github.polybooks

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import com.github.polybooks.core.Book
import com.github.polybooks.core.database.BooksAdapter
import com.github.polybooks.core.database.interfaces.BookSettings
import com.github.polybooks.core.database.interfaces.Query
import com.github.polybooks.utils.setupNavbar
import com.google.android.material.bottomnavigation.BottomNavigationView

class ListBooksActivity: ListActivity<Book>() {

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
                        startActivity(Intent(this, ListSaleActivity::class.java))
                        true
                    }
                    R.id.user_profile ->{
                        // TODO: user sales
                        false
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
                    .fromSettings(intent.getSerializableExtra(EXTRA_BOOKS_QUERY_SETTINGS) as BookSettings)
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