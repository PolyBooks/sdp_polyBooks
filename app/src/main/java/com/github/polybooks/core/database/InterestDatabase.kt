package com.github.polybooks.core.database

import com.github.polybooks.core.*
import java.util.concurrent.CompletableFuture

/**
 * Provides the API for accessing/modifying interests in a database.
 *
 * The list of what interests exist can be accessed with the dedicated methods. As for the interests
 * of the user, there is a distinction between the local and remote users. The interest of the user
 * should be stored in the local Interests, but an authenticated user can save their interests on the
 * cloud. The database provides methods for saving/restoring local interests to/from the cloud.
 * */
interface InterestDatabase {

    /**
     * List all the Fields in the database.
     * */
    fun listAllFields() : CompletableFuture<List<Field>>

    /**
     * List all the Semesters in the database.
     * */
    fun listAllSemesters() : CompletableFuture<List<Semester>>

    /**
     * List all the Courses in the database.
     * */
    fun listAllCourses() : CompletableFuture<List<Course>>

    /**
     * List all the interests in the database.
     * */
    fun listAllInterests() : CompletableFuture<Triple<List<Field>, List<Semester>, List<Course>>>
        = TODO("It can be implemented from the previous functions")

    /**
     * Get the interests of the local user.
     * */
    fun getLocalInterests() : CompletableFuture<Triple<List<Field>, List<Semester>, List<Course>>>

    /**
     * Sets the interests of the local user.
     * @return A Future to receive confirmation of success/failure asynchronously
     * */
    fun setLocalInterests(interests : List<Interest>) : CompletableFuture<Nothing>

    /**
     * Get the interests of the given User as stored on the cloud.
     * TODO: Might need to add an authentication token to restrict authenticated users to only access their interests.
     * */
    fun getInterestsFromCloud(user : User) : CompletableFuture<Triple<List<Field>, List<Semester>, List<Course>>>

    /**
     * Save the interests of the local user to the cloud to the given user's account.
     * @return A Future to receive confirmation of success/failure asynchronously
     * TODO: Might need to add an authentication token to restrict authenticated users to only modify their interests.
     * */
    fun updateCloudInterestsFromLocal(user : User) : CompletableFuture<Nothing>

    /**
     * Restore the interests of the local user from the cloud given the user's account:
     * @return A Future to receive confirmation of success/failure asynchronously
     * TODO: Might need to add an authentication token to restrict authenticated users to only access their interests.
     * */
    fun updateLocalInterestsFromCloud(user : User) : CompletableFuture<Nothing>

}
