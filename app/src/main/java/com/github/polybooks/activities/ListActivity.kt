package com.github.polybooks.activities

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.polybooks.R
import com.github.polybooks.core.database.implementation.FBBookDatabase
import com.github.polybooks.core.database.implementation.OLBookDatabase
import com.github.polybooks.core.database.implementation.SaleDatabase
import com.github.polybooks.core.database.interfaces.Query
import com.github.polybooks.utils.url2json
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Activity to list all active sales
 * Pass boolean extra with id R.string.list_is_sale as false for books
 */

abstract class ListActivity<T>: AppCompatActivity() {

    companion object {
        const val EXTRA_SALE_QUERY_SETTINGS: String = "saleQuerySettings"
        const val EXTRA_BOOKS_QUERY_SETTINGS: String = "bookQuerySettings"
        private const val TAG = "ListActivity"
    }

    private lateinit var mRecycler: RecyclerView
    private val mLayout: RecyclerView.LayoutManager = LinearLayoutManager(this)

    private val firestore = FirebaseFirestore.getInstance()
    private val olBookDB = OLBookDatabase { string -> url2json(string) }
    open val bookDB = FBBookDatabase(firestore, olBookDB)
    open val salesDB = SaleDatabase(firestore, bookDB)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        findViewById<TextView>(R.id.sale_or_book).text = getTitleText()
        findViewById<Button>(R.id.filter_button).setOnClickListener { onFilterButtonClick() }
        val query: Query<T> = getQuery()

        mRecycler = findViewById(R.id.recyclerView)
        mRecycler.setHasFixedSize(true)

        // Links the database api to the recyclerView
        mRecycler.layoutManager = mLayout
        mRecycler.adapter = adapter(emptyList())

        query.getAll().thenAccept { list -> this.updateAdapter(list) }

        setNavBar()
    }

    private fun updateAdapter(list: List<*>) {
        runOnUiThread {
            mRecycler.adapter = adapter(list as List<T>)
        }
    }

    abstract fun onFilterButtonClick()
    abstract fun adapter(list : List<T>): RecyclerView.Adapter<*>
    abstract fun setNavBar(): Unit
    abstract fun getQuery(): Query<T>
    abstract fun getTitleText(): String
}