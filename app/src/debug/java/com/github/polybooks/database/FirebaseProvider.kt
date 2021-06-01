package com.github.polybooks.database

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseProvider {

    fun getFirestore() : FirebaseFirestore {
        val instance = FirebaseFirestore.getInstance()
        try { //Sorry, all the clean ways to prevent calling this twice don't work and I don't know why.
            instance.useEmulator("10.0.2.2", 8080)
            Log.d("Here", "===========")
        } catch(_: IllegalStateException) {}
        return instance
    }

    fun getAuth() : FirebaseAuth {
        val instance = FirebaseAuth.getInstance()
        try { //Sorry, all the clean ways to prevent calling this twice don't work and I don't know why.
            instance.useEmulator("10.0.2.2", 9099)
            Log.d("Here", "===========")
        } catch(_: IllegalStateException) {}
        return instance
    }

}
