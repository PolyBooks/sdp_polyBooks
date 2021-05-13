package com.github.polybooks

import android.os.Bundle
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.polybooks.core.Sale
import com.github.polybooks.core.SaleState
import com.github.polybooks.core.database.SalesAdapter
import com.github.polybooks.core.database.implementation.FBBookDatabase
import com.github.polybooks.core.database.implementation.OLBookDatabase
import com.github.polybooks.core.database.implementation.SaleDatabase
import com.github.polybooks.core.database.interfaces.SaleQuery
import com.github.polybooks.core.database.interfaces.SaleSettings
import com.github.polybooks.utils.setupNavbar
import com.github.polybooks.utils.url2json
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Activity to list all active sales
 */

class ListSalesActivity: AppCompatActivity() {

    companion object {
        const val EXTRA_SALE_QUERY_SETTINGS: String = "saleQuerySettings"
        const val EXTRA_BOOKS_QUERY_SETTINGS: String = "bookQuerySettings"
    }

    private lateinit var mRecycler: RecyclerView
    private lateinit var mAdapter: SalesAdapter
    private val mLayout: RecyclerView.LayoutManager = LinearLayoutManager(this)
    private val initialBooks: List<Sale> = emptyList()

    private val firestore = FirebaseFirestore.getInstance()
    private val olBookDB = OLBookDatabase { string -> url2json(string) }
    private val bookDB = FBBookDatabase(firestore, olBookDB)
    private val salesDB = SaleDatabase(firestore, bookDB)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO: rename / change this
        setContentView(R.layout.activity_basic_database)

        mRecycler = findViewById(R.id.recyclerView)
        mRecycler.setHasFixedSize(true)
        // Links the database api to the recyclerView
        // TODO: check if completable future is used correctly
        mAdapter = SalesAdapter(initialBooks)
        mRecycler.layoutManager = mLayout
        mRecycler.adapter = mAdapter


        val saleQuery: SaleQuery = intent.getSerializableExtra(EXTRA_SALE_QUERY_SETTINGS)
                ?.let {
                    salesDB.querySales().fromSettings(intent.getSerializableExtra(EXTRA_SALE_QUERY_SETTINGS) as SaleSettings)
                }
                ?: salesDB.querySales().searchByState(setOf(SaleState.ACTIVE))

        saleQuery.getAll().thenAccept { list -> this.updateAdapter(list) }

        setupNavbar(findViewById(R.id.bottom_navigation), this)
    }


    private fun updateAdapter(sales: List<Sale>) {
        runOnUiThread {
            mAdapter = SalesAdapter(sales)
            mRecycler.adapter = mAdapter
        }
    }
}
