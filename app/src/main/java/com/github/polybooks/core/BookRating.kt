package com.github.polybooks.core

import com.github.polybooks.database.FirebaseProvider
import com.google.firebase.auth.FirebaseAuth
import java.util.concurrent.CompletableFuture

class BookRating(ratingMap: Any) {
    companion object {
        val bookRatingRef = FirebaseProvider.getFirestore().collection("bookRating")
    }

    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    var rating = (ratingMap as HashMap<String, Any>)["rating"] as Map<String, List<String>>
    var totalVotes = (ratingMap as HashMap<String, Any>)["totalVotes"] as Long

    fun getUserVote(): String? {
        val uid = firebaseAuth.currentUser?.uid ?: return null

        for ((key, value) in rating.entries) {
            if (value.contains(uid)) return key
        }

        return null
    }

    private fun toDocument(): HashMap<String, Any> {
        return hashMapOf(
            "rating" to rating,
            "totalVotes" to totalVotes,
        )
    }

    fun uploadToFirebase(isbn: ISBN): CompletableFuture<Unit> {
        val future = CompletableFuture<Unit>()
        bookRatingRef.document(isbn).set(this.toDocument())
            .addOnSuccessListener {
                future.complete(Unit)
            }.addOnFailureListener {
                future.completeExceptionally(it)
            }

        return future
    }
}
