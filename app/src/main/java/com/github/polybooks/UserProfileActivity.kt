package com.github.polybooks

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseUser

class UserProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        val username = intent.getStringExtra(EXTRA_MESSAGE);
        val welcomeText = "Hello $username !"

        val textMessageView = findViewById<TextView>(R.id.welcome_text);
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

    }
}