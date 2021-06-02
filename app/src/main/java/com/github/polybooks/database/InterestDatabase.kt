package com.github.polybooks.database

import com.github.polybooks.core.*
import java.util.concurrent.CompletableFuture

/**
 * Provides the API for accessing/modifying interests in a database.
 * */
interface InterestDatabase {

    /**
     * Add a new Topic document to the Topics collection
     */
    fun addTopic(topic: Topic) : CompletableFuture<Topic>

    /**
     * Add a new semester document to the semesters collection
     */
    fun addSemester(semester: Semester) : CompletableFuture<Semester>

    /**
     * Add a new course document to the courses collection
     */
    fun addCourse(course: Course) : CompletableFuture<Course>

    /**
     * List all the Topics in the database.
     * */
    fun listAllTopics() : CompletableFuture<List<Topic>>

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
    fun listAllInterests() : CompletableFuture<Triple<List<Topic>, List<Semester>, List<Course>>>
        = listAllTopics().thenCombine(listAllSemesters()) { topics, semester -> Pair(topics, semester) }
        .thenCombine(listAllCourses()){pair, courses -> Triple(pair.first, pair.second, courses)}
    /**
     * Get the interests of the current user.
     * If the user is not auth, it will use exclusively the local storage. (TODO)
     * */
    fun getCurrentUserInterests() : CompletableFuture<List<Interest>>

    /**
     * Sets the interests of the current user.
     * If the user is not auth, it will use exclusively the local storage. (TODO)
     * @return A Future to receive confirmation of success/failure asynchronously
     * */
    fun setCurrentUserInterests(interests : List<Interest>) : CompletableFuture<List<Interest>>


}
