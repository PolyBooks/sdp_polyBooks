package com.github.polybooks

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.polybooks.core.Sale
import com.github.polybooks.core.SaleState
import com.github.polybooks.core.database.SalesAdapter
import com.github.polybooks.core.database.implementation.OLBookDatabase
import com.github.polybooks.core.database.implementation.SaleDatabase
import com.github.polybooks.core.database.interfaces.SaleQuery
import com.github.polybooks.core.database.interfaces.SaleSettings
import com.github.polybooks.utils.url2json
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Activity to list all active sales
 */
class ListSalesActivity : AppCompatActivity() {
    companion object {
        val EXTRA_SALE_QUERY_SETTINGS: String = "saleQuerySettings"
        val EXTRA_BOOKS_QUERY_SETTINGS: String = "bookQuerySettings"
    }

    private lateinit var mRecycler: RecyclerView
    private lateinit var mAdapter: SalesAdapter
    private val mLayout: RecyclerView.LayoutManager = LinearLayoutManager(this)
    private val initialBooks: List<Sale> = emptyList()

    private val firestore = FirebaseFirestore.getInstance()
    private val bookDB = OLBookDatabase { string -> url2json(string) }
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
    }

    private fun updateAdapter(sales : List<Sale>){
        runOnUiThread {
            mAdapter = SalesAdapter(sales)
            mRecycler.adapter= mAdapter
        }
    }
}
