package com.github.polybooks.core

import java.io.Serializable

/**
 * This class contains all the information to describe a user.
 * It can either be the local user (a user that is not logged in),
 * or a logged in user.
 * */
sealed class User : Serializable
data class LoggedUser(val uid : String, val pseudo : String?) : User()
object LocalUser : User()

/**
 * Allows access to the name of a field
 */
enum class UserFields(val fieldName: String) {
    UID("uid"),
    PSEUDO("pseudo"),
}