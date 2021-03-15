package com.github.polybooks

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.polybooks.core.SaleState
import com.github.polybooks.core.database.SaleDatabase
import com.github.polybooks.core.database.SalesAdapter

/**
 * Activity to list all active sales
 * @property database the link to the sale database
 */
class ListSalesActivity(private val database: SaleDatabase) : AppCompatActivity() {
    private lateinit var mRecycler : RecyclerView
    private lateinit var mAdapter : RecyclerView.Adapter<SalesAdapter.SalesViewHolder>
    private val mLayout : RecyclerView.LayoutManager = LinearLayoutManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //TODO: rename / change this
        setContentView(R.layout.activity_dummy_database)

        mRecycler = findViewById(R.id.recyclerView)
        mRecycler.setHasFixedSize(true)
        //Links the database api to the recyclerView
        //TODO: check if completeable future is used correctly
        mAdapter = SalesAdapter(
                database.querySales().searchByState(setOf(SaleState.ACTIVE)).getAll().get())
        mRecycler.layoutManager = mLayout
        mRecycler.adapter = mAdapter


    }
}