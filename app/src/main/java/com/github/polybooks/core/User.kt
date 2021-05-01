package com.github.polybooks.core

/**
 * This class contains all the information to describe a user.
 * It can either be the local user (a user that is not logged in),
 * or a logged in user.
 * */
sealed class User
data class LoggedUser(val uid: Int, val pseudo: String): User()
object LocalUser: User()

/**
 * Allows access to the name of a field
 */
enum class UserFields(val fieldName: String) {
    UID("uid"),
    PSEUDO("pseudo"),
}