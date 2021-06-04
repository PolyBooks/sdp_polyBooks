package com.github.polybooks.activities

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.github.polybooks.R
import com.github.polybooks.adapter.database.SalesAdapter

import com.github.polybooks.core.Sale
import com.github.polybooks.core.SaleState
import com.github.polybooks.database.Database
import com.github.polybooks.database.SaleDatabase
import com.github.polybooks.database.SaleQuery
import com.github.polybooks.utils.GlobalVariables.EXTRA_SALE_QUERY
import com.github.polybooks.utils.setupNavbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.concurrent.CompletableFuture

/**
 * Activity to list all active sales
 */
class ListSalesActivity: ListActivity<Sale>() {

    private lateinit var salesDB: SaleDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        salesDB = Database.saleDatabase(applicationContext)
        super.onCreate(savedInstanceState)
    }

    override fun adapter(list: List<Sale>): RecyclerView.Adapter<*> {
        return SalesAdapter(list)
    }

    override fun setNavBar() {
        val navBarListener: BottomNavigationView.OnNavigationItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.home -> {
                        startActivity(Intent(this, MainActivity::class.java))
                        true
                    }
                    R.id.books -> {
                        startActivity(Intent(this, ListBooksActivity::class.java))
                        true
                    }
                    R.id.user_profile -> {
                        startActivity(Intent(this, LoginActivity::class.java))
                        true
                    }
                    else -> true
                }
            }
        setupNavbar(findViewById(R.id.bottom_navigation), this, R.id.sales, navBarListener)
    }

    override fun getElements(): CompletableFuture<List<Sale>> {
        return intent.getSerializableExtra(EXTRA_SALE_QUERY)
            ?.let {
                salesDB.execute(it as SaleQuery)
            }
            ?: salesDB.execute(SaleQuery(states = listOf(SaleState.ACTIVE)))
    }

    override fun onFilterButtonClick() {
        startActivity(Intent(this, FilteringSalesActivity::class.java))
    }

    override fun getTitleText(): String {
        return getString(R.string.sale)
    }
}