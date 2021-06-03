package com.github.polybooks.database

import android.content.Context
import com.github.polybooks.core.Course
import com.github.polybooks.core.Interest
import com.github.polybooks.core.Semester
import com.github.polybooks.core.Topic
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.concurrent.CompletableFuture

/**
 * A cache for user interests
 * Needs to be called manually, it is not automatically integrated in the DB processes,
 * because it needs to access the context of the application
 */
object UserInterestDBWithCache {

    private const val preferenceFileKey: String = "com.github.polybooks.database.USER_INTEREST_PREFERENCE_FILE_KEY"
    private const val timestampKey: String = "userInterestsLastEdited"
    private const val topicsListKey: String = "userTopicsList"
    private const val coursesListKey: String = "userCoursesList"
    private const val semestersListKey: String = "userSemestersList"
    private const val refreshTimeMillis: Long = 259200000 // this amounts to 3 days
    private const val emptyRefreshTimeMillis: Long= 600000 // this amounts to 10 min


    /**
     * This functions stores user interests both online and in the local cache
     * In a finished app product, the action of setting the user interest will be called much less often than getting them,
     * so every time they are set, it's fine to update both online and the cache.
     */
    fun storeUserInterests(context: Context, interestsList: List<Interest>) {
        Database.interestDatabase.setCurrentUserInterests(interestsList)

        storeUserInterestsToCache(context, interestsList)
    }

    /**
     *  Read sharedPreferences and get the cached data
     *  If older than 3 days, get from online and store it in the cache and return the online version.
     *  If empty, get from online as soon as older than 10 minutes.
     *  It's an average compromise between not constantly requesting an emptyList, but also fixing caches issues by retrieving the online version.
     *  Otherwise, return the cache content wrapped in a future after deserialising it.
     */
    fun getCachedUserInterests(context: Context): CompletableFuture<List<Interest>> {

        val sharedPref = context.getSharedPreferences(preferenceFileKey, Context.MODE_PRIVATE) ?: return getUserInterestsFromDBThenStoreInCache(context)
        val lastEdited = sharedPref.getLong(timestampKey, 0L)
        val topics = sharedPref.getString(topicsListKey, "") ?: ""
        val courses = sharedPref.getString(coursesListKey, "") ?: ""
        val semesters = sharedPref.getString(semestersListKey, "") ?: ""

        if (lastEdited == 0L || (System.currentTimeMillis() - lastEdited) > refreshTimeMillis) return getUserInterestsFromDBThenStoreInCache(context)


        if (topics.isEmpty() && courses.isEmpty() && semesters.isEmpty() && (System.currentTimeMillis() - lastEdited) > emptyRefreshTimeMillis) return getUserInterestsFromDBThenStoreInCache(context)

        val interestsList = deserialiseAll(topics, courses, semesters)
        val future = CompletableFuture<List<Interest>>()
        future.complete(interestsList)
        return future
    }


    private fun deserialiseAll(topics: String, courses: String, semesters: String): List<Interest> {
        val gson = Gson()

        val topicListType = object : TypeToken<List<Topic>>() { }.type
        val topicsList = gson.fromJson<List<Topic>>(topics, topicListType)

        val courseListType = object : TypeToken<List<Course>>() { }.type
        val courseList = gson.fromJson<List<Course>>(courses, courseListType)

        val semesterListType = object : TypeToken<List<Semester>>() { }.type
        val semesterList = gson.fromJson<List<Semester>>(semesters, semesterListType)

        return topicsList + courseList + semesterList
    }

    /**
     * combine getting fresh data from DB and directly storing it in the cache
     */
    private fun getUserInterestsFromDBThenStoreInCache(context: Context): CompletableFuture<List<Interest>> {
        val future = Database.interestDatabase.getCurrentUserInterests()
        future.thenAccept {
            storeUserInterestsToCache(context, it)
        }
        return future
    }


    /**
     *  Automatically overwrites what was previously stored
     */
    private fun storeUserInterestsToCache(context: Context, interestsList: List<Interest>) {
        val gson = Gson()
        val topics = interestsList.partition { it is Topic }.first
        val courses = interestsList.partition { it is Course }.first
        val semesters = interestsList.partition { it is Semester }.first

        val sharedPref = context.getSharedPreferences(preferenceFileKey, Context.MODE_PRIVATE)
            ?: return

        with(sharedPref.edit()) {
            putLong(timestampKey, System.currentTimeMillis())
            putString(topicsListKey, gson.toJson(topics))
            putString(coursesListKey, gson.toJson(courses))
            putString(semestersListKey, gson.toJson(semesters))
            apply()
        }
    }

}