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

        val textView = findViewById<TextView>(R.id.welcome_text).apply {
            text = welcomeText
        }

        val backhome : TextView = findViewById(R.id.logo)
        backhome.setOnClickListener {
            val i : Intent = Intent(this, MainActivity::class.java)
            startActivity(i)
        }

        val buttonDisconnect: Button = findViewById(R.id.button_disco)
        buttonDisconnect.setOnClickListener {
            LoginActivity().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }
}