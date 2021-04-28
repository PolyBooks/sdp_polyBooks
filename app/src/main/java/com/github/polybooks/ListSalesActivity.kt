package com.github.polybooks

import android.content.Intent
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
import com.github.polybooks.core.database.interfaces.SaleSettings
import com.github.polybooks.utils.anonymousBook
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Timestamp

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
                    SaleDatabase().querySales().fromSettings(intent.getSerializableExtra(EXTRA_SALE_QUERY_SETTINGS) as SaleSettings)
                }
                ?: SaleDatabase().querySales().searchByState(setOf(SaleState.ACTIVE))
        saleQuery.getAll().thenAccept { list -> this.updateAdapter(list) }

        setupNavbar(findViewById(R.id.bottom_navigation))
    }

    private fun setupNavbar(navBar: BottomNavigationView){
        navBar.selectedItemId = R.id.default_selected
        navBar.setOnNavigationItemSelectedListener { item ->
            when(item.itemId){
                R.id.home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.books ->{
                    startActivity(Intent(this, FilteringBooksActivity::class.java))
                    true
                }
                R.id.sales ->{
                    startActivity(Intent(this, FilteringSalesActivity::class.java))
                    true
                }
                R.id.user_profile ->{
                    // TODO: user sales
                    false
                }
                else -> true
            }
        }

    }

    private fun updateAdapter(sales : List<Sale>){
        runOnUiThread {
            mAdapter = SalesAdapter(sales)
            mRecycler.adapter= mAdapter
        }
    }
}
