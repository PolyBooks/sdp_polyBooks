package com.github.polybooks.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.polybooks.R
import com.github.polybooks.utils.GlobalVariables.EXTRA_USERNAME
import com.github.polybooks.utils.setupNavbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class UserProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        val username = intent.getStringExtra(EXTRA_USERNAME)
        val uid = intent.getStringExtra(EXTRA_MESSAGE2)

        val welcomeText = "Hello $username ! \n Your UID: $uid"

        findViewById<Button>(R.id.switch_loca).setOnClickListener {
            val intent = Intent(this, GPSActivity::class.java).apply {
                putExtra(EXTRA_MESSAGE2, uid) }
            startActivity(intent) }

        findViewById<TextView>(R.id.welcome_text).apply {text = welcomeText}

        findViewById<Button>(R.id.sell_book_button).setOnClickListener {
            startActivity(Intent(this, AddSaleActivity::class.java)) }

        findViewById<Button>(R.id.button_disco).setOnClickListener { LoginActivity().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent) }

        findViewById<Button>(R.id.edit_user_interests).setOnClickListener {
            startActivity(Intent(this, EditUserInterestsActivity::class.java)) }

        setNavBar()
    }

    private fun setNavBar(){
        val navBarListener : BottomNavigationView.OnNavigationItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener{ item ->
                when(item.itemId){
                    R.id.books -> {
                        startActivity(Intent(this, ListBooksActivity::class.java))
                        true
                    }
                    R.id.sales -> {
                        startActivity(Intent(this, ListSalesActivity::class.java))
                        true
                    }
                    R.id.home -> {
                        startActivity(Intent(this, MainActivity::class.java))
                        true
                    }
                    R.id.user_profile -> {
                        startActivity(Intent(this, LoginActivity::class.java))
                        true
                    }
                    else -> true
                }
            }

        setupNavbar(findViewById(R.id.bottom_navigation), this, R.id.user_profile, navBarListener)
    }
}