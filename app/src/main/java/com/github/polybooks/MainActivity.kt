package com.github.polybooks

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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