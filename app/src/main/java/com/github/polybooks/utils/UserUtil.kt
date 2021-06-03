package com.github.polybooks.utils

import com.github.polybooks.core.LocalUser
import com.github.polybooks.core.LoggedUser
import com.github.polybooks.core.User
import com.google.firebase.auth.FirebaseUser

fun firebaseUserToUser(firebaseUser: FirebaseUser?): User {
    return firebaseUser?.let { LoggedUser(firebaseUser.uid, firebaseUser.displayName) } ?: LocalUser
}