package com.github.polybooks

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()

        val button: Button = findViewById(R.id.button_open_db_tests)
        button.setOnClickListener {
            startActivity(Intent(this, FirebaseActivity::class.java))
        }

        val button2: Button = findViewById(R.id.log_button)
        button2.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    fun signup_fun(view: View) {
        setContentView(R.layout.signup)
    }

    fun backhome_fun(view: View) {
        setContentView(R.layout.activity_main)
    }
}