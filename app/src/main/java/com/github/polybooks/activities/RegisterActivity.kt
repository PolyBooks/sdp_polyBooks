package com.github.polybooks.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.polybooks.R
import com.github.polybooks.database.FirebaseProvider
import com.github.polybooks.utils.failedUser
import com.github.polybooks.utils.setupNavbar
import com.github.polybooks.utils.updateUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.userProfileChangeRequest


class RegisterActivity : AppCompatActivity() {

    private var auth = FirebaseProvider.getAuth()

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val registerButton = findViewById<Button>(R.id.button_reg)
        registerButton.setOnClickListener{

            val usernameField = findViewById<EditText>(R.id.username_field)
            val username = usernameField.text.toString()

            val emailField = findViewById<EditText>(R.id.email_field)
            val email = emailField.text.toString()

            val password1Field = findViewById<EditText>(R.id.password1_field)
            val password1 = password1Field.text.toString()

            val password2Field = findViewById<EditText>(R.id.password2_field)
            val password2 = password2Field.text.toString()

            if(password1 == password2)
                createAccount(email, password1, username)
            else
                Toast.makeText(baseContext, "Passwords don't match.",
                        Toast.LENGTH_LONG).show()

        }

        val navBarListener : BottomNavigationView.OnNavigationItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener{ item ->
                when(item.itemId){
                    R.id.books ->{
                        startActivity(Intent(this, FilteringBooksActivity::class.java))
                        true
                    }
                    R.id.sales ->{
                        startActivity(Intent(this, FilteringSalesActivity::class.java))
                        true
                    }
                    R.id.user_profile ->{
                        startActivity(Intent(this, LoginActivity::class.java))
                        true
                    }
                    else -> {
                        startActivity(Intent(this, MainActivity::class.java))
                        true
                    }
                }
            }
        setupNavbar(findViewById(R.id.bottom_navigation), this, R.id.user_profile, navBarListener)
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser != null){
            updateUI(currentUser, this)
        }
    }

    private fun createAccount(email: String, password: String, username: String) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "createUserWithEmail:success")
                        val user = auth.currentUser

                        user!!.updateProfile(userProfileChangeRequest { displayName = username })
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d(TAG, "User profile updated.")
                                    updateUI(user, this)
                                }
                            }
                    } else {
                        Log.d(TAG, "Cancelled ${task.isCanceled} Complete ${task.isComplete} Message: ${task.exception?.message}===================")
                        failedUser(auth.currentUser, this)
                    }
                }
    }

    companion object {
        const val TAG = "EmailPassword"
    }
}