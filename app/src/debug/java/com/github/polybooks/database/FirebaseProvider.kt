package com.github.polybooks.database

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseProvider {

    fun getFirestore() : FirebaseFirestore {
        val instance = FirebaseFirestore.getInstance()
        try { //Sorry, all the clean ways to prevent calling this twice don't work and I don't know why.
            instance.useEmulator("10.0.2.2", 8080)
        } catch(_: IllegalStateException) {}
        return instance
    }

    fun getAuth() : FirebaseAuth {
        val instance = FirebaseAuth.getInstance()
        try { //Sorry, all the clean ways to prevent calling this twice don't work and I don't know why.
            instance.useEmulator("10.0.2.2", 9099)
            instance.createUserWithEmailAndPassword("login@bypassword.test","123456")
                .addOnSuccessListener { result -> result.user?.updateProfile(userProfileChangeRequest { displayName = "TestLogin" }) }
        } catch(_: IllegalStateException) {}
        return instance
    }

}
