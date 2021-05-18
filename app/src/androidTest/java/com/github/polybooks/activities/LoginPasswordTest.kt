package com.github.polybooks.activities

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.polybooks.R
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)

class LoginPasswordTest {

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
    fun fillAndLoginWithPassword() {
        onView(withId(R.id.email_field)).perform(typeText("login@bypassword.test"), closeSoftKeyboard())
        onView(withId(R.id.password_field)).perform(typeText("123456"), closeSoftKeyboard())
        onView(withId(R.id.log_button)).perform(scrollTo(), click())
        Thread.sleep(1500)
        Intents.intended(hasComponent(UserProfileActivity::class.java.name))
        Intents.intended(toPackage("com.github.polybooks"))
        Intents.intended(hasExtra(EXTRA_MESSAGE, "TestLogin"))
        Thread.sleep(1500)
        onView(withId(R.id.button_disco)).perform(click())
        Thread.sleep(1500)
        Intents.intended(hasComponent(LoginActivity::class.java.name))
    }

    @Test
    fun navBarSales() {
        onView(withId(R.id.sales)).perform(click())
        Intents.intended(hasComponent(ListSalesActivity::class.java.name))
    }

    @Test
    fun navBarProfile() {
        onView(withId(R.id.user_profile)).perform(click())
        onView(withId(R.id.log_button)).check(ViewAssertions.matches(isDisplayed()))
    }

    @Test
    fun navBarBooks() {
        onView(withId(R.id.books)).perform(click())
        Intents.intended(hasComponent(ListBooksActivity::class.java.name))
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
        onView(withId(R.id.user_profile))
            .check(ViewAssertions.matches(isSelected()))
    }

    @Test
    fun navBarHome() {
        onView(withId(R.id.home)).perform(click())
        Intents.intended(hasComponent(MainActivity::class.java.name))
    }
}