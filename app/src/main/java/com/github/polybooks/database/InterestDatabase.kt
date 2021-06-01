package com.github.polybooks.database

import com.github.polybooks.core.*
import java.util.concurrent.CompletableFuture

/**
 * Provides the API for accessing/modifying interests in a database.
 * */
interface InterestDatabase {

    /**
     * Add a new field document to the fields collection
     */
    fun addField(field: Field) : CompletableFuture<Field>

    /**
     * Add a new semester document to the semesters collection
     */
    fun addSemester(semester: Semester) : CompletableFuture<Semester>

    /**
     * Add a new course document to the courses collection
     */
    fun addCourse(course: Course) : CompletableFuture<Course>

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
     * Get the interests of the current user.
     * If the user is not auth, it will use exclusively the local storage. (TODO)
     * */
    fun getCurrentUserInterests() : CompletableFuture<Triple<List<Field>, List<Semester>, List<Course>>>

    /**
     * Sets the interests of the current user.
     * If the user is not auth, it will use exclusively the local storage. (TODO)
     * @return A Future to receive confirmation of success/failure asynchronously
     * */
    fun setCurrentUserInterests(interests : List<Interest>) : CompletableFuture<Unit>


}
