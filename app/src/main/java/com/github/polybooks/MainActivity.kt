
package com.github.polybooks

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.github.polybooks.core.DummySalesQuery


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dbButton: Button = findViewById(R.id.button_open_db_tests)
        /*
        dbButton.setOnClickListener {
            startActivity(Intent(this, DummyDatabaseActivity::class.java))
        }
        */
        dbButton.setOnClickListener {
            val i : Intent = Intent(this, ListSalesActivity::class.java)
            startActivity(i)
        }

        val sellButton: Button = findViewById(R.id.sell_button)
        sellButton.setOnClickListener {
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