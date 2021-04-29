package com.github.polybooks

import android.R
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)

class LoginGoogleTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @Before
    fun before() {
        Intents.init()
    }

    @After
    fun after() {
        Intents.release()
    }

    @Test
    fun signInButtonGoogle() {
        onView(withId(com.github.polybooks.R.id.sign_in_button)).perform(scrollTo(), click())
    }

    @Test
    fun signOutButtonGoogle() {
        onView(withId(com.github.polybooks.R.id.register_button)).perform(scrollTo(), click())
    }

    @Test
    fun navBarSales() {
        onView(withId(com.github.polybooks.R.id.sales)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(FilteringSalesActivity::class.java.name))
    }

    @Test
    fun navBarProfile() {
        onView(withId(com.github.polybooks.R.id.user_profile)).perform(click())
        onView(withId(com.github.polybooks.R.id.sign_in_button)).perform(scrollTo()).check(ViewAssertions.matches(isDisplayed()))
    }

    @Test
    fun navBarBooks() {
        onView(withId(com.github.polybooks.R.id.books)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(FilteringBooksActivity::class.java.name))
    }

    @Test
    fun navBarDefault() {
        onView(withId(com.github.polybooks.R.id.default_selected)).check(
            ViewAssertions.matches(
                withEffectiveVisibility(
                    Visibility.GONE
                )
            )
        )
        onView(withId(com.github.polybooks.R.id.default_selected)).check(
            ViewAssertions.matches(
                Matchers.not(isEnabled())))
    }

    @Test
    fun navBarSelected() {
        onView(withId(com.github.polybooks.R.id.default_selected)).check(ViewAssertions.matches(isSelected()))
    }

    @Test
    fun navBarHome() {
        onView(withId(com.github.polybooks.R.id.home)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(MainActivity::class.java.name))
    }
}