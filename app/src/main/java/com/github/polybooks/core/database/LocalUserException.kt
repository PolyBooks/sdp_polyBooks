package com.github.polybooks.core.database

/**
 * Exception made for when a user attemps something that he needs to be logged in to do
 */
class LocalUserException(message: String): Exception(message)