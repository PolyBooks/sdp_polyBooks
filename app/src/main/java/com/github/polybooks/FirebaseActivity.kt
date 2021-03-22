package com.github.polybooks

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.polybooks.core.Sale
import com.github.polybooks.core.database.SalesDatabase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import java.util.function.Supplier


class FirebaseActivity : AppCompatActivity() {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val TAG: String = "FirebaseActivity"

    private val KEY_UID: String = "uid"
    private val KEY_USERNAME: String = "username"

    private val userRef: CollectionReference = db.collection("user")

    private lateinit var editTextUid: EditText
    private lateinit var editTextUsername: EditText
    private lateinit var textViewData: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firebase)

        editTextUid = findViewById(R.id.uid_text_field)
        editTextUsername = findViewById(R.id.username_text_field)
        textViewData = findViewById(R.id.text_view_data)
    }

    fun sendPayload(view: View) {
        val payload: MutableMap<String, Any> = HashMap()
        payload[KEY_UID] = editTextUid.text.toString()
        payload[KEY_USERNAME] = editTextUsername.text.toString()

        Log.d(TAG, "sendPayload: CLICKING =================")

        userRef.document(payload[KEY_UID].toString()).set(payload)
            .addOnSuccessListener {
                Toast.makeText(this@FirebaseActivity, "[SET] success", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "[SET] success")
            }
            .addOnFailureListener { e: Exception ->
                Toast.makeText(this@FirebaseActivity, "[SET] failure", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "[SET] failure $e")
            }
    }

    fun getPayload(view: View) {
        userRef.document(editTextUid.text.toString()).get()
            .addOnSuccessListener { documentSnapshot: DocumentSnapshot ->
                if (documentSnapshot.exists()) {
                    val uid: String? = documentSnapshot.getString(KEY_UID)
                    val username: String? = documentSnapshot.getString(KEY_USERNAME)
                    val message = "UID : $uid, username: $username"

                    textViewData.text = message
                    Log.d(TAG, "[GET] success")
                } else {
                    Log.d(TAG, "[GET] failure.getPayload: Document '${editTextUid.text}-${editTextUsername.text}' does not exist")
                }
            }
            .addOnFailureListener { e: Exception ->
                Log.d(TAG, "[GET] failure. getPayload: $e")
            }
    }

    fun testSalesDatabase(view: View) {
        val database : SalesDatabase = SalesDatabase()

        val future: CompletableFuture<List<Sale>> = database.querySales().getAll()
        future.thenApply { results ->
            println("-------------- SALES (${results.size}) --------------")
            results.forEach { s -> println("SALE : ${s.book}") }
            println("---------------------------------------")
        }
    }
}
