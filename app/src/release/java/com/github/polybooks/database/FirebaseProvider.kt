package com.github.polybooks.database

import com.google.firebase.firestore.FirebaseFirestore

object FirebaseProvider {

    fun getFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    fun getAuth() : FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

}
