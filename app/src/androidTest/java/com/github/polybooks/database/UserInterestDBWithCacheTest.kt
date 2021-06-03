package com.github.polybooks.database

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.polybooks.activities.MainActivity
import com.github.polybooks.core.Course
import com.github.polybooks.core.Interest
import com.github.polybooks.core.Semester
import com.github.polybooks.core.Topic
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

/**
 * The fact that the tests uses DummyInterestDatabase is actually nearly an advantage:
 * if they didn't it might be harder to determine whether the results come out of the cache or the DB
 * Like that, we can be sure that we're testing the cache,
 * while the real FBInterestDB is already unit-tested separately
 */
class UserInterestDBWithCacheTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)


    @Test
    fun storeAndRetrieveFromCache() {
        val context: Context = ApplicationProvider.getApplicationContext()
        val inputInterests: List<Interest> = listOf(
            Topic("Sci-Fi"),
            Topic("Computer Science"),
            Semester("IN", "BA6"),
            Course("COM-308"),
            Course("CS-322"),
            Course("CS-330"),
            Course("CS-306"),
            Semester("IN", "BA3"),
            Topic("Architecture")
            )

        UserInterestDBWithCache.storeUserInterests(context, inputInterests)

        val retrievedInterests = UserInterestDBWithCache.getCachedUserInterests(context)

        Assert.assertTrue(inputInterests.containsAll(retrievedInterests.get()))
        Assert.assertTrue(retrievedInterests.get().containsAll(inputInterests))
    }
}