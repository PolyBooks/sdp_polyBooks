package com.github.polybooks.activities

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import com.github.polybooks.R
import com.github.polybooks.core.Sale
import com.github.polybooks.core.SaleState
import com.github.polybooks.database.*
import com.github.polybooks.utils.setupNavbar
import com.github.polybooks.utils.url2json
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.CompletableFuture

/**
 * Activity to list Sales
 */
class ListSalesActivity: ListActivity<Sale>() {

    private val firestore = FirebaseFirestore.getInstance()
    private val olBookDB = OLBookDatabase { string -> url2json(string) }
    private val bookDB = FBBookDatabase(firestore, olBookDB)
    private val saleDB = FBSaleDatabase(firestore, bookDB)

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

    override fun getElements(): CompletableFuture<List<Sale>> {
        return intent.getSerializableExtra(EXTRA_SALE_QUERY)
            ?.let { query ->
                saleDB.execute(query as SaleQuery)
            }
            ?: saleDB.execute(SaleQuery(states = setOf(SaleState.ACTIVE)))
    }

    override fun onFilterButtonClick() {
        startActivity(Intent(this, FilteringSalesActivity::class.java))
    }

    override fun getTitleText(): String {
        return getString(R.string.sale)
    }
}