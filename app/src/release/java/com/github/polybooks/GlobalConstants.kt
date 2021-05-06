package com.github.polybooks

import com.github.polybooks.core.database.implementation.OLBookDatabase
import com.github.polybooks.core.database.implementation.SaleDatabase
import com.github.polybooks.utils.url2json
import com.google.firebase.firestore.FirebaseFirestore

object GlobalConstants {
    fun firestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }
    val OLbookDB = OLBookDatabase { string -> url2json(string) }
    val salesDB = SaleDatabase(OLbookDB)
    val bookDB = FBBookDatabase(OLBookDB)
}