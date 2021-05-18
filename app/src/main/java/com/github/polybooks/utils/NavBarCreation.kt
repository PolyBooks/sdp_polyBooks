package com.github.polybooks.utils

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import com.github.polybooks.R
import com.github.polybooks.activities.ListBooksActivity
import com.github.polybooks.activities.ListSalesActivity
import com.github.polybooks.activities.LoginActivity
import com.github.polybooks.activities.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

fun setupNavbar(
    navBar: BottomNavigationView,
    origin: Context,
    selectedItem: Int = R.id.default_selected,
    selectedListener: BottomNavigationView.OnNavigationItemSelectedListener? = null
) {
    navBar.selectedItemId = selectedItem

    selectedListener?.run {
        navBar.setOnNavigationItemSelectedListener(selectedListener)
    } ?: run {
        navBar.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    startActivity(origin, Intent(origin, MainActivity::class.java), null)
                    true
                }
                R.id.books -> {
                    startActivity(origin, Intent(origin, ListBooksActivity::class.java), null)
                    true
                }
                R.id.sales -> {
                    startActivity(origin, Intent(origin, ListSalesActivity::class.java), null)
                    true
                }
                R.id.user_profile -> {
                    startActivity(origin, Intent(origin, LoginActivity::class.java), null)
                    true
                }
                else -> true
            }
        }
    }
}