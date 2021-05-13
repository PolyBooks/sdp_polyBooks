package com.github.polybooks.activities

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.polybooks.R
import com.github.polybooks.adapter.MessageAdapter
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

/**
 * An activity where user can chat (ie. send and receive messages)
 * individually with an other user
 */
class ChatActivity: AppCompatActivity() {

    companion object {
        const val MESSAGES = "messages"
    }

    // TODO fetch this list from db in future
    private val messageListTemp = mutableListOf<String>()

    // TODO use future global static dbs
    private val db = Firebase.database
    private val messagesRef = db.reference.child(MESSAGES)

    // Send Message Logic
    private lateinit var mSend: ImageView
    private lateinit var mTextMessage: EditText

    // See messages Logic
    private lateinit var mMessages: RecyclerView
    private lateinit var mManager: LinearLayoutManager
    private lateinit var mAdapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        setViews()
    }

    private fun setViews() {
        // buttons
        mSend = findViewById(R.id.send_button)
        mTextMessage = findViewById(R.id.edit_text_message)

        // messages view
        mMessages = findViewById(R.id.messages)
        mManager = LinearLayoutManager(this)
        mManager.stackFromEnd = true
        mAdapter = MessageAdapter(messageListTemp)
        mMessages.layoutManager = mManager
        mMessages.adapter = mAdapter
    }

    fun sendMessage(view: View) {
        if (mTextMessage.text.isNotEmpty()) {
            messageListTemp.add(mTextMessage.text.toString())
            mAdapter.notifyItemInserted(messageListTemp.size - 1)
        }
    }
}