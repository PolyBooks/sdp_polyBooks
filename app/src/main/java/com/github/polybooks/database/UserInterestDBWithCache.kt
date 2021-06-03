package com.github.polybooks.database

import android.content.Context
import com.github.polybooks.R
import com.github.polybooks.core.Interest
import java.util.concurrent.CompletableFuture

/**
 * A cache for user interests
 * Needs to be called manually, it is not automatically integrated in the DB processes,
 * because it needs to access the context of the application
 */
object UserInterestDBWithCache {

    private const val preferenceFileKey: String = "com.github.polybooks.database.USER_INTEREST_PREFERENCE_FILE_KEY"
    private const val timestampKey: String = "userInterestsLastEdited"
    private const val interestsListKey: String = "userInterestsList"


    /**
     * This functions stores user interests both online and in the local cache
     */
    fun storeUserInterests(context: Context, interestsList: List<Interest>) {
        Database.interestDatabase.setCurrentUserInterests(interestsList)

        storeUserInterestsToCache(context, interestsList)
    }

    /**
     * TOWRITE
     */
    fun getCachedUserInterests(context: Context): CompletableFuture<List<Interest>> {
        return

        // Read sharedPreferences and get the cached data
        val sharedPref = context.getSharedPreferences(preferenceFileKey, Context.MODE_PRIVATE) ?: return null // TODO then use the DB
        val lastEdited = sharedPref.getLong(timestampKey, 0)
        val interestsSingleString = sharedPref.getString(interestsListKey, "")
        // If empty, skip, and get from online and store it in the cache.
        // Else, check for recency, if older than 3 days, get from online and store it in the cache.
        // Else, if less than 3 days, use this cached data and deserialise it.
        // In a finished app product, the action of setting the user interest will be called much less often than getting them, so everytime they are set, it's fine to update both online and the cache.
    }

    private fun getUserInterestsFromDBStoreInCache(context: Context): CompletableFuture<List<Interest>> {
        val future = Database.interestDatabase.getCurrentUserInterests().thenAccept {
            storeUserInterestsToCache(context, it)
        }
        return future
    }


    private fun storeUserInterestsToCache(context: Context, interestsList: List<Interest>) {
        val sharedPref = context.getSharedPreferences(preferenceFileKey, Context.MODE_PRIVATE)
            ?: return
        // automatically overwrites what was previously stored
        with(sharedPref.edit()) {
            putLong(timestampKey, System.currentTimeMillis())
            putString(interestsListKey, interestsList.joinToString(separator = "•"))
            apply()
        }
    }

    fun checkCache() {
        val sharedPref = context.getSharedPreferences(
            preferenceFileKey, Context.MODE_PRIVATE)
    }
}