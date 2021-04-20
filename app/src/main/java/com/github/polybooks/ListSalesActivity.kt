package com.github.polybooks

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.polybooks.core.*
import com.github.polybooks.core.database.SalesAdapter
import com.github.polybooks.core.database.implementation.DummySalesQuery
import com.github.polybooks.core.database.implementation.SaleDatabase
import com.github.polybooks.core.database.implementation.format
import com.github.polybooks.core.database.interfaces.SaleQuery
import com.github.polybooks.utils.anonymousBook
import com.google.firebase.Timestamp


/**
 * Activity to list all active sales
 * @property saleQuery the query listing what is to be shown
 */
class ListSalesActivity(private val saleQuery: SaleQuery = SaleDatabase().querySales()) : AppCompatActivity() {
    companion object {
        val EXTRA_SALE_QUERY :String = "saleQuery"
        val EXTRA_BOOKS_QUERY : String = "bookQuery"
    }
    private val TAG: String = "ListSaleActivity"

    private lateinit var mRecycler : RecyclerView
    private lateinit var mAdapter : SalesAdapter
    private val mLayout : RecyclerView.LayoutManager = LinearLayoutManager(this)
    private val initialBooks : List<Sale> = emptyList()

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

        val saleQuery: SaleQuery = intent.getSerializableExtra(EXTRA_SALE_QUERY)
                ?.let{ intent.getSerializableExtra(EXTRA_SALE_QUERY) as SaleQuery }
                ?: saleQuery.searchByState(setOf(SaleState.ACTIVE))
        saleQuery.getAll().thenAccept { list -> this.updateAdapter(list) }
    }

    private fun updateAdapter(sales : List<Sale>){
        runOnUiThread {
            mAdapter = SalesAdapter(sales)
            mRecycler.adapter= mAdapter
        }
    }
}
