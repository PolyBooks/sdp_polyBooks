package com.github.polybooks

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.polybooks.core.*
import com.github.polybooks.core.database.DummySalesQuery
import com.github.polybooks.core.database.SaleQuery
import com.github.polybooks.core.database.SalesAdapter
import com.github.polybooks.core.database.format

/**
 * Activity to list all active sales
 * @property saleQuery the query listing what is to be shown
 */
class ListSalesActivity(private val saleQuery: SaleQuery = DummySalesQuery()) : AppCompatActivity() {
    private lateinit var mRecycler : RecyclerView
    private lateinit var mAdapter : SalesAdapter
    private val mLayout : RecyclerView.LayoutManager = LinearLayoutManager(this)
    private val initalBooks : List<Sale> = listOf(Sale("Book1", 1, 23.00f, BookCondition.GOOD, format.parse("2016-05-05")!!, SaleState.ACTIVE))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //TODO: rename / change this
        setContentView(R.layout.activity_dummy_database)

        mRecycler = findViewById(R.id.recyclerView)
        mRecycler.setHasFixedSize(true)
        //Links the database api to the recyclerView
        //TODO: check if completeable future is used correctly
        mAdapter = SalesAdapter(initalBooks)
        mRecycler.layoutManager = mLayout
        mRecycler.adapter = mAdapter
        saleQuery.searchByState(setOf(SaleState.ACTIVE)).getAll().thenAccept{ list ->
            this.updateAdapter(list)
        }

    }

    private fun updateAdapter(sales : List<Sale>){
        runOnUiThread {
            mAdapter = SalesAdapter(sales)
            mRecycler.adapter= mAdapter
        }
    }
}
