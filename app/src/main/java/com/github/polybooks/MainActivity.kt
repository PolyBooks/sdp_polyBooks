
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

        val dbButton: Button = findViewById(R.id.button_open_db_tests)
        dbButton.setOnClickListener {
            val i : Intent = Intent(this, ListSalesActivity::class.java)
            startActivity(i)
        }
    }

    fun login(view: View) {
        setContentView(R.layout.login)
    }

    fun signup(view: View) {
        setContentView(R.layout.signup)
    }

    fun backhome(view: View) {
        setContentView(R.layout.activity_main)
    }

    fun sellBook(view: View) {
        val intent = Intent(this, AddSaleActivity::class.java)
        startActivity(intent)
    }

}

