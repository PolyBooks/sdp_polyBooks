package com.github.polybooks

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.polybooks.core.*
import com.github.polybooks.core.database.SalesAdapter
import com.github.polybooks.core.database.implementation.FBBookDatabase
import com.github.polybooks.core.database.implementation.OLBookDatabase
import com.github.polybooks.core.database.implementation.SaleDatabase
import com.github.polybooks.core.database.interfaces.Query
import com.github.polybooks.core.database.interfaces.SaleQuery
import com.github.polybooks.core.database.interfaces.SaleSettings
import com.github.polybooks.utils.setupNavbar
import com.github.polybooks.utils.url2json
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore

class ListSalesActivity: ListActivity<Sale>() {
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