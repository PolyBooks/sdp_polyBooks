package com.github.polybooks

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button: Button = findViewById(R.id.button_open_db_tests)
        button.setOnClickListener {
            startActivity(Intent(this, FirebaseActivity::class.java))
        }
    }

    fun login_fun(view: View) {
        setContentView(R.layout.login)
    }

    fun signup_fun(view: View) {
        setContentView(R.layout.signup)
    }

    fun backhome_fun(view: View) {
        setContentView(R.layout.activity_main)
    }
}