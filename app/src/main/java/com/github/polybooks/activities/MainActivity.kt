package com.github.polybooks.activities

import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.github.polybooks.R
import com.github.polybooks.database.FirebaseProvider
import com.github.polybooks.utils.setupNavbar
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * The main homepage
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val login = FirebaseProvider.getAuth().currentUser

        val sellButton: Button = findViewById(R.id.sell_button)
        val buttonLogin: Button = findViewById(R.id.log_button)
        val buttonRegister: Button = findViewById(R.id.signup_button)
        login?.let {
            buttonLogin.visibility = GONE
            buttonRegister.visibility = GONE
            sellButton.setOnClickListener {
                startActivity(Intent(this, AddSaleActivity::class.java))
            }
        }?:let {
            buttonLogin.setOnClickListener {
                startActivity(Intent(this, LoginActivity::class.java))
            }
            buttonRegister.setOnClickListener {
                startActivity(Intent(this, RegisterActivity::class.java))
            }
            sellButton.visibility = GONE
        }

        findViewById<Button>(R.id.view_books_button).setOnClickListener {
            val i = Intent(this, ListBooksActivity::class.java)
            startActivity(i)
        }

        setNavBar()

    }

    private fun setNavBar(){
        val navBarListener : BottomNavigationView.OnNavigationItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener{ item ->
                when(item.itemId){
                    R.id.books ->{
                        startActivity(Intent(this, ListBooksActivity::class.java))
                        true
                    }
                    R.id.sales ->{
                        startActivity(Intent(this, ListSalesActivity::class.java))
                        true
                    }
                    R.id.user_profile ->{
                        startActivity(Intent(this, LoginActivity::class.java))
                        true
                    }
                    else -> true
                }
            }
        setupNavbar(findViewById(R.id.bottom_navigation), this, R.id.home, navBarListener)
    }


}
