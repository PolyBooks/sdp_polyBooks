package com.github.polybooks

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
    fun FillAndLoginWithPassword() {
        onView(withId(R.id.email_field)).perform(typeText("login@bypassword.test"), closeSoftKeyboard())
        onView(withId(R.id.password_field)).perform(typeText("123456"), closeSoftKeyboard())
        onView(withId(R.id.log_button)).perform(scrollTo(), click())
        Thread.sleep(1500);
        Intents.intended(IntentMatchers.hasComponent(UserProfileActivity::class.java.name))
        Intents.intended(toPackage("com.github.polybooks"));
        Intents.intended(hasExtra(EXTRA_MESSAGE, "TestLogin"));
        Thread.sleep(1500);
        onView(withId(R.id.button_disco)).perform(click())
        Thread.sleep(1500);
        Intents.intended(IntentMatchers.hasComponent(LoginActivity::class.java.name))
    }

    @Test
    fun navBarSales() {
        Espresso.onView(ViewMatchers.withId(R.id.sales)).perform(ViewActions.click())
        Intents.intended(IntentMatchers.hasComponent(FilteringSalesActivity::class.java.name))
    }

    @Test
    fun navBarProfile() {
        Espresso.onView(ViewMatchers.withId(R.id.user_profile)).perform(ViewActions.click())
        // Espresso.onView(ViewMatchers.withId(R.id.recyclerView)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun navBarBooks() {
        Espresso.onView(ViewMatchers.withId(R.id.books)).perform(ViewActions.click())
        Intents.intended(IntentMatchers.hasComponent(FilteringBooksActivity::class.java.name))
    }

    @Test
    fun navBarDefault() {
        Espresso.onView(ViewMatchers.withId(R.id.default_selected)).check(
            ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(
                    ViewMatchers.Visibility.GONE
                )
            )
        )
        Espresso.onView(ViewMatchers.withId(R.id.default_selected))
            .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isEnabled())))
    }

    @Test
    fun navBarSelected() {
        Espresso.onView(ViewMatchers.withId(R.id.user_profile))
            .check(ViewAssertions.matches(ViewMatchers.isSelected()))
    }

    @Test
    fun navBarHome() {
        Espresso.onView(ViewMatchers.withId(R.id.home)).perform(ViewActions.click())
        Intents.intended(IntentMatchers.hasComponent(MainActivity::class.java.name))
    }
}