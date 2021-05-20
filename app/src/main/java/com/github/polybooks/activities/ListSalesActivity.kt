package com.github.polybooks.activities

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import com.github.polybooks.R
import com.github.polybooks.core.Sale
import com.github.polybooks.core.SaleState
import com.github.polybooks.database.FBSaleDatabase
import com.github.polybooks.database.Query
import com.github.polybooks.database.SaleSettings
import com.github.polybooks.adapter.database.SalesAdapter
import com.github.polybooks.utils.GlobalVariables.EXTRA_SALE_QUERY_SETTINGS
import com.github.polybooks.utils.setupNavbar
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * Activity to list all active sales
 */
class ListSalesActivity: ListActivity<Sale>() {

    val salesDB = FBSaleDatabase

    override fun adapter(list: List<Sale>): RecyclerView.Adapter<*> {
        return SalesAdapter(list)
    }

    override fun setNavBar() {
        val navBarListener : BottomNavigationView.OnNavigationItemSelectedListener =
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

    override fun getQuery(): Query<Sale> {
        return intent.getSerializableExtra(EXTRA_SALE_QUERY_SETTINGS)
            ?.let {
                salesDB.querySales()
                    .fromSettings(it as SaleSettings)
            }
            ?: salesDB.querySales().searchByState(setOf(SaleState.ACTIVE))
    }

    override fun onFilterButtonClick() {
        startActivity(Intent(this, FilteringSalesActivity::class.java))
    }

    override fun getTitleText(): String {
        return getString(R.string.sale)
    }
}