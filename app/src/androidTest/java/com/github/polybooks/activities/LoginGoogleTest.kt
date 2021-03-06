package com.github.polybooks.activities

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.polybooks.R
import org.hamcrest.Matchers
import org.junit.*
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
        onView(withId(R.id.sign_in_button)).perform(scrollTo(), click())
    }

    @Ignore("Bog needs to be fixed") //FIXME
    @Test
    fun signOutButtonGoogle() {
        onView(withId(R.id.log_button)).perform(scrollTo(), click())
    }

    @Test
    fun navBarSales() {
        onView(withId(R.id.sales)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(ListSalesActivity::class.java.name))
    }

    @Test
    fun navBarProfile() {
        onView(withId(R.id.user_profile)).perform(click())
        onView(withId(R.id.sign_in_button)).perform(scrollTo()).check(ViewAssertions.matches(isDisplayed()))
    }

    @Test
    fun navBarBooks() {
        onView(withId(R.id.books)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(ListBooksActivity::class.java.name))
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
        onView(withId(R.id.default_selected)).check(
            ViewAssertions.matches(
                Matchers.not(isEnabled())))
    }

    @Test
    fun navBarSelected() {
        onView(withId(R.id.user_profile)).check(ViewAssertions.matches(isSelected()))
    }

    @Test
    fun navBarHome() {
        onView(withId(R.id.home)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(MainActivity::class.java.name))
    }
}