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
import com.github.polybooks.utils.successUser
import com.github.polybooks.utils.updateUI
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider

const val EXTRA_MESSAGE2 = "com.github.polybooks.UID"

/**
 * Demonstrate Firebase Authentication using a Google ID Token.
 */
class LoginActivity : AppCompatActivity() {

    private var auth = FirebaseProvider.getAuth()

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val signInGoogleButton = findViewById<SignInButton>(R.id.sign_in_button)
        signInGoogleButton.setOnClickListener{
            signIn()
        }

        val signInEmailButton = findViewById<Button>(R.id.log_button)
        signInEmailButton.setOnClickListener{

            val emailField = findViewById<EditText>(R.id.email_field)
            val email = emailField.text.toString()

            val passwordField = findViewById<EditText>(R.id.password_field)
            val password = passwordField.text.toString()

            signInEmailPass(email, password)
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val navBarListener : BottomNavigationView.OnNavigationItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener{ item ->
                when(item.itemId){
                    R.id.books ->{
                        startActivity(Intent(this, ListBooksActivity::class.java))
                        true
                    }
                    R.id.sales ->{
                        startActivity(Intent(this, ListSalesActivity::class.java))
                        true
                    }
                    R.id.home ->{
                        startActivity(Intent(this, MainActivity::class.java))
                        true
                    }
                    else -> true
                }
            }
        setupNavbar(findViewById(R.id.bottom_navigation), this, R.id.user_profile, navBarListener)
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                Toast.makeText(this@LoginActivity, "Hey "+ account.displayName + "! You are connected :)", Toast.LENGTH_LONG).show()
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
                Toast.makeText(this@LoginActivity, "Sorry... Something went wrong", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { taskGoogle ->
                    checkUser(taskGoogle)
                }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun signInEmailPass(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { taskEmailPass ->
                checkUser(taskEmailPass)
            }
    }

    private fun checkUser(task: Task<AuthResult>){
        if (task.isSuccessful) {
            successUser(auth.currentUser, this)
        } else {
            failedUser(auth.currentUser, this)
        }
    }

    fun signOut() {
        FirebaseProvider.getAuth().signOut()
        Log.d(TAG, "signed out")
    }

    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }
}