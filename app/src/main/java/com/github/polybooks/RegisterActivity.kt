package com.github.polybooks

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.polybooks.utils.setupNavbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase


class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth = Firebase.auth

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
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser != null){
            updateUI(currentUser)
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
                                        updateUI(user)
                                    }
                                }
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                        updateUI(null)
                    }
                }
    }

    private fun updateUI(user: FirebaseUser?) {
        if(user != null) {
            val intent = Intent(this, UserProfileActivity::class.java).apply {
                putExtra(EXTRA_MESSAGE, user.displayName)
            }
            startActivity(intent)
        }
    }

    companion object {
        private const val TAG = "EmailPassword"
    }
}