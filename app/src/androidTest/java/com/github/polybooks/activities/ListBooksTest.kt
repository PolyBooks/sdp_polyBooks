package com.github.polybooks.activities

import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.polybooks.R
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class ListBooksTest {

    companion object
    {
        val intent: Intent = Intent(ApplicationProvider.getApplicationContext(), ListBooksActivity::class.java)
    }

    @get:Rule
    val activityRule = ActivityScenarioRule<ListBooksActivity>(intent)

    @Before
    fun before() {
        Intents.init()
    }

    @After
    fun after() {
        Intents.release()
    }

    @Test
    fun filterButton() {
        onView(withId(R.id.filter_button)).perform(click())
        intended(hasComponent(FilteringBooksActivity::class.java.name))
    }

    @Test
    fun navBarSales() {
        onView(withId(R.id.sales)).perform(click())
        intended(hasComponent(ListSalesActivity::class.java.name))
    }

    @Test
    fun navBarProfile() {
        onView(withId(R.id.user_profile)).perform(click())
        intended(hasComponent(LoginActivity::class.java.name))
    }

    @Test
    fun navBarBooks() {
        onView(withId(R.id.books)).perform(click())
        onView(withId(R.id.books))
            .check(ViewAssertions.matches(isSelected()))
    }

    @Test
    fun navBarDefault() {
        onView(withId(R.id.default_selected)).check(
            ViewAssertions.matches(
                withEffectiveVisibility(
                    Visibility.GONE
                )
            )
        )
        onView(withId(R.id.default_selected))
            .check(ViewAssertions.matches(Matchers.not(isEnabled())))
    }

    @Test
    fun navBarSelected() {
        onView(withId(R.id.books))
            .check(ViewAssertions.matches(isSelected()))
    }

    @Test
    fun navBarHome() {
        onView(withId(R.id.home)).perform(click())
        intended(hasComponent(MainActivity::class.java.name))
    }
}