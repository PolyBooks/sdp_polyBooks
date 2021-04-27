package com.github.polybooks

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()

        val button2: Button = findViewById(R.id.log_button)
        button2.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        val dbButton: Button = findViewById(R.id.button_open_db_tests)
        dbButton.setOnClickListener {
//            val i : Intent = Intent(this, ListSalesActivity::class.java)
//            val i : Intent = Intent(this, FilteringBooksActivity::class.java)
            val i : Intent = Intent(this, FilteringSalesActivity::class.java)

            startActivity(i)
        }

        val navBar = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        navBar.selectedItemId = R.id.home
        navBar.setOnNavigationItemSelectedListener { item ->
            when(item.itemId){
                R.id.user_sales ->{
                    // TODO: user sales
                    true
                }
                R.id.sales ->{
                    startActivity(Intent(this, FilteringSalesActivity::class.java))
                    true
                }
                R.id.books ->{
                    startActivity(Intent(this, FilteringBooksActivity::class.java))
                    true
                }
                R.id.user_profile ->{
                    // TODO: user sales
                    true
                }
                else -> true
            }
        }

    }

    fun signup(view: View) {
        setContentView(R.layout.activity_signup)
    }

    fun backhome(view: View) {
        setContentView(R.layout.activity_main)
    }

    fun sellBook(view: View) {
        val intent = Intent(this, AddSaleActivity::class.java)
        startActivity(intent)
    }

}
