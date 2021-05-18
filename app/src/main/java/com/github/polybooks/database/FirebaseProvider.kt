package com.github.polybooks.database

import com.google.firebase.firestore.FirebaseFirestore

object FirebaseProvider {
    private var initialised = false

    fun getFirestore() : FirebaseFirestore = FirebaseFirestore.getInstance()

    fun getMockFirestore() : FirebaseFirestore {
        val instance = FirebaseFirestore.getInstance()
        if (!initialised) {
            instance.useEmulator("10.0.2.2", 8080)
            initialised = true
        }
        return instance
    }

}
