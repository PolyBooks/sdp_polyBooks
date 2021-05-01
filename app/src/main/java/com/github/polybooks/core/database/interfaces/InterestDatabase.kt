package com.github.polybooks.core.database.interfaces

import com.github.polybooks.core.*
import java.util.concurrent.CompletableFuture

/**
 * Provides the API for accessing/modifying interests in a database.
 * */
interface InterestDatabase {

    /**
     * List all the Fields in the database.
     * */
    fun listAllFields(): CompletableFuture<List<Field>>

    /**
     * List all the Semesters in the database.
     * */
    fun listAllSemesters(): CompletableFuture<List<Semester>>

    /**
     * List all the Courses in the database.
     * */
    fun listAllCourses(): CompletableFuture<List<Course>>

    /**
     * List all the interests in the database.
     * */
    fun listAllInterests(): CompletableFuture<Triple<List<Field>, List<Semester>, List<Course>>> =
        TODO("It can be implemented from the previous functions")

    /**
     * Get the interests of the specified user
     * TODO: Might need to add an authentication token to restrict authenticated users to only modify their interests.
     * */
    fun getUserInterests(user: User): CompletableFuture<Triple<List<Field>, List<Semester>, List<Course>>>

    /**
     * Sets the interests of the specified user.
     * TODO: Might need to add an authentication token to restrict authenticated users to only modify their interests.
     * @return A Future to receive confirmation of success/failure asynchronously
     * */
    fun setUserInterests(user: User, interests: List<Interest>): CompletableFuture<Unit>

}
