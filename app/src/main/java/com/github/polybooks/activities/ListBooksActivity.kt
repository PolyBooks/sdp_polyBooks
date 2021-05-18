package com.github.polybooks.activities

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import com.github.polybooks.R
import com.github.polybooks.core.Book
import com.github.polybooks.core.database.BooksAdapter
import com.github.polybooks.database.BookQuery
import com.github.polybooks.database.FBBookDatabase
import com.github.polybooks.database.OLBookDatabase
import com.github.polybooks.utils.setupNavbar
import com.github.polybooks.utils.url2json
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.CompletableFuture

/**
 * Activity to list Books
 */
class ListBooksActivity: ListActivity<Book>() {

    private val firebase = FirebaseFirestore.getInstance()
    private val olBookDB = OLBookDatabase{ url2json(it) }
    private val bookDB = FBBookDatabase(firebase, olBookDB)

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

    override fun getElements(): CompletableFuture<List<Book>> {
        return intent.getSerializableExtra(EXTRA_BOOKS_QUERY)
            ?.let { query ->
                bookDB.execute(query as BookQuery)
            }
            ?: bookDB.execute(BookQuery())
    }

    override fun onFilterButtonClick() {
        startActivity(Intent(this, FilteringBooksActivity::class.java))
    }

    override fun getTitleText(): String {
        return getString(R.string.books)
    }
}