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
import com.github.polybooks.core.database.SaleQuery
import com.github.polybooks.core.database.SalesAdapter

/**
 * Activity to list all active sales
 * @property database the link to the sale database
 */
class ListSalesActivity(private val saleQuery: SaleQuery = DummySalesQuery()) : AppCompatActivity() {
    private lateinit var mRecycler : RecyclerView
    private lateinit var mAdapter : SalesAdapter
    private val mLayout : RecyclerView.LayoutManager = LinearLayoutManager(this)
    private lateinit var l : List<Sale>

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //TODO: rename / change this
        setContentView(R.layout.activity_dummy_database)

        mRecycler = findViewById(R.id.recyclerView)
        mRecycler.setHasFixedSize(true)
        //Links the database api to the recyclerView
        //TODO: check if completeable future is used correctly
        l = listOf(Sale("Book1", 1, 23.00f, BookCondition.GOOD, format.parse("2016-05-05")!!, SaleState.ACTIVE))
        mAdapter = SalesAdapter(l)
        mRecycler.layoutManager = mLayout
        mRecycler.adapter = mAdapter
        Toast.makeText(this@ListSalesActivity, "[SET] success", Toast.LENGTH_SHORT).show()
        saleQuery.searchByState(setOf(SaleState.ACTIVE)).getAll().thenAccept{ list ->
            Log.d("LIST ", list.toString())
            this.updateAdapter(list)

            //mRecycler.adapter= SalesAdapter(list)

        }

    }

    private fun updateAdapter(sales : List<Sale>){
        Log.d("LIST ","HOPEFUL")
        //l = sales
        //mAdapter.update(sales)
        mAdapter = SalesAdapter(sales)
        mRecycler.adapter= mAdapter
        Log.d("LIST ", "HERE?")
    }
}