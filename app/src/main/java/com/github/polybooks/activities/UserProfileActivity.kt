package com.github.polybooks.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.polybooks.R
import com.github.polybooks.utils.GlobalVariables.EXTRA_USERNAME
import com.github.polybooks.utils.setupNavbar
import com.google.android.material.bottomnavigation.BottomNavigationView

class UserProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        val username = intent.getStringExtra(EXTRA_USERNAME)
        val welcomeText = "Hello $username !"

        val textMessageView = findViewById<TextView>(R.id.welcome_text)
        textMessageView.apply {text = welcomeText}

        val buttonSellBook: Button = findViewById(R.id.sell_book_button)
        buttonSellBook.setOnClickListener {
            val intent = Intent(this, AddSaleActivity::class.java)
            startActivity(intent)
        }

        val buttonDisconnect: Button = findViewById(R.id.button_disco)
        buttonDisconnect.setOnClickListener {
            LoginActivity().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.edit_user_interests).setOnClickListener {
            startActivity(Intent(this, EditUserInterestsActivity::class.java))
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
                    R.id.home ->{
                        startActivity(Intent(this, MainActivity::class.java))
                        true
                    }
                    R.id.user_profile ->{
                        startActivity(Intent(this, LoginActivity::class.java))
                        true
                    }
                    else -> true
                }
            }
        setupNavbar(findViewById(R.id.bottom_navigation), this, R.id.user_profile, navBarListener)
    }
}